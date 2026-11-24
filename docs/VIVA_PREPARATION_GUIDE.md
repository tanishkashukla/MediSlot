# MediSlot Viva Preparation Guide & Defense Reference
**Course:** B.Tech Advanced Algorithms Assessment (CIA 3)  
**Academic Modules:** Unit 2 (Heaps), Unit 3 (Trees), Unit 6 / CO5 (Randomized Algorithms)

---

## 1. Core Algorithm Architecture Questions

### Q1: Why did you choose a Segment Tree over a Simple Bitmask or Interval Tree for slot availability?
**Answer:**
A simple bitset or boolean array requires an $O(N)$ linear scan to find available slots or count availability within an arbitrary time window $[ql, qr]$ (e.g. "available between 1:00 PM and 5:00 PM"). An Interval Tree is optimized for storing arbitrary overlapping intervals rather than fixed discrete time slots.  
The **Segment Tree (Unit 3)** is the ideal data structure because:
1. It maintains pre-aggregated subtree sums, enabling $O(\log N)$ range availability counting.
2. It allows $O(\log N)$ next-available-slot search by immediately skipping subtrees whose sum is zero.
3. Updating availability upon booking, cancellation, or doctor blocking takes $O(\log N)$ point updates, ensuring immediate atomic state propagation.

---

### Q2: Why did you implement a Custom Binary Min Heap instead of using `java.util.PriorityQueue`?
**Answer:**
1. **Academic Assessment Requirement:** The assessment explicitly requires all core algorithms to be implemented from scratch without using Java's built-in priority collections.
2. **Multi-Attribute Priority Invariant:** MediSlot requires a two-tiered priority system:
   - Priority Rank ($P_1 = \text{Urgent}, P_2 = \text{Normal}$). Urgent has lower numerical rank, placing it closer to the root.
   - First-Come-First-Served (FCFS) Timestamp ($T_1 < T_2$): When two patients share the same priority, the earlier registration timestamp must break the tie.
3. **Benchmarking & Operation Profiling:** Our custom heap incorporates atomic operation counters tracking exact element comparisons and tree swaps during `siftUp` and `siftDown`, which powers the live Algorithm Performance Lab.

---

### Q3: What is the significance of Randomized Quickselect (CO5) and why use Three-Way Partitioning?
**Answer:**
**Randomized Quickselect (Unit 6 / CO5)** computes the $k$-th smallest element (e.g., Median at $k = \lfloor N/2 \rfloor$, 90th percentile at $k = \lfloor 0.90 N \rfloor$, and 95th percentile at $k = \lfloor 0.95 N \rfloor$) from 10,000+ completed appointment waiting times.  
- **Why not full sorting?** Dual-Pivot Quicksort (`Arrays.sort`) requires $O(N \log N)$ time and full array duplication. Quickselect only recurses into the partition containing $k$, achieving Expected $O(N)$ linear time and $O(1)$ auxiliary memory.
- **Why Three-Way Partitioning (Dutch National Flag)?** Hospital wait-time datasets contain heavy clusters of duplicate values (e.g. hundreds of standard 15, 20, 30 min intervals). Standard Lomuto/Hoare partitioning degrades to $O(N^2)$ when duplicates dominate. Three-way partitioning isolates all elements equal to the pivot into $[lt \dots gt]$. If $k \in [lt, gt]$, the algorithm terminates in $O(1)$ without further recursion!

---

## 2. Concurrency & Clinical Logic Questions

### Q4: How does MediSlot prevent double-booking when two patients click the same slot simultaneously?
**Answer:**
MediSlot utilizes a multi-layered concurrency safeguard:
1. **In-Memory Segment Tree Verification:** The slot's availability is validated against the doctor's Segment Tree in $O(\log N)$ time.
2. **Synchronized Critical Section:** The booking method is synchronized at the service layer, preventing concurrent threads from entering the validation block simultaneously.
3. **Database Version Locking (`@Version`):** Spring Data JPA optimistic locking ensures that if two concurrent transactions attempt to write a conflicting booking for the same `(doctorId, appointmentDate, slotIndex)`, the second transaction is rejected with an `OptimisticLockException` or state conflict exception.
4. **Segment Tree Rollback:** If a race condition is detected, the tree leaf is re-synchronized with the database state.

---

### Q5: How does the 15-Minute Waiting List Offer Window work?
**Answer:**
When an appointment is cancelled:
1. The slot is freed in the Segment Tree (`updateSlot(slotIndex, true)`).
2. The `WaitingListService` builds the `CustomBinaryMinHeap` from eligible waiting patients.
3. The root element (`extractMin()`) is popped—this is the highest-priority patient (Urgent first, then earliest FCFS timestamp).
4. The entry status changes to `OFFERED`, setting `offerExpiresAt = LocalDateTime.now().plusMinutes(15)`.
5. If the patient accepts within 15 minutes, the slot is confirmed.
6. If the patient declines or the `@Scheduled` background sweeper detects an expired window, the slot is automatically offered to the *next* patient extracted from the Min Heap.

---

### Q6: How does Urgent priority differ from clinical emergency triage?
**Answer:**
Per project guidelines, Urgent priority is strictly an **administrative scheduling queue precedence**. It grants earlier allocation when waiting for an appointment slot or cancellation. It **never** cancels or overrides existing confirmed appointments of other patients and is explicitly not a replacement for clinical emergency department (ER) triage.
