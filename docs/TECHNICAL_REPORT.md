# Smart Appointment Scheduling System (MediSlot)
## Comprehensive Technical & Academic Report
**Program:** Bachelor of Technology in Computer Science & Engineering  
**Subject:** Advanced Algorithms — Continuous Internal Assessment 3 (CIA 3)  
**Academic Module Mappings:**
- **Unit 2:** Binary Min Heap (Priority Queues & Dynamic Reallocation)
- **Unit 3:** Segment Tree (Discrete Interval Scheduling & Availability Management)
- **Unit 6 / CO5:** Randomized Quickselect with 3-Way Partitioning (Order Statistics & Percentiles)

---

## 1. Introduction & Project Scope

Modern outpatient departments encounter high patient dissatisfaction driven by prolonged physical queues, appointment double-booking, and idle doctor consultation time when scheduled patients cancel. Conventional scheduling software relies on relational database `SELECT` queries across booking tables, resulting in $O(N)$ linear scans that degrade under heavy user concurrency and large datasets.

**MediSlot** was designed to fundamentally overcome these challenges by translating theoretical computer science algorithms into production-grade healthcare operations:
1. **Segment Tree Availability Engine (Unit 3):** Encapsulates daily 30-minute doctor consultation intervals into a binary range-aggregate tree. Queries for available slot counts, earliest next available slots, and atomic slot reservations execute in $O(\log N)$ time.
2. **Priority Waiting-List Queue (Unit 2):** A custom Binary Min Heap dynamically prioritizes Urgent requests over Normal requests while strictly adhering to First-Come-First-Served (FCFS) timestamps for equal priorities. Upon appointment cancellation, slots are automatically offered to the top candidate with a 15-minute acceptance window.
3. **Randomized Quickselect Pipeline (Unit 6 / CO5):** Rather than sorting 10,000+ historical audit logs in $O(N \log N)$ time to calculate patient waiting percentiles, MediSlot uses in-place Dutch National Flag Three-Way Partitioning to compute the Median (50th), 90th, and 95th percentile waiting times in Expected $O(N)$ linear time.

---

## 2. Technology Stack Architecture

- **Backend Framework:** Java Spring Boot 3.2.5 (JDK 17+)
- **Security & Authorization:** Spring Security 6 with stateless HMAC SHA-256 JSON Web Tokens (JWT)
- **Persistence & ORM:** Spring Data JPA with Hibernate 6
- **Database:**
  - *Local Development:* Embedded H2 Database (in-memory mode, auto-seeded on launch)
  - *Production Hosted:* PostgreSQL on Railway
- **Frontend Framework:** React 18, Vite 5, Tailwind CSS 3
- **Visualization:** Recharts 2 (interactive Area, Bar, and Grouped Benchmark charts)
- **Icons:** Lucide React

---

## 3. Algorithm Implementation Details

### 3.1 Segment Tree (`com.medislot.algorithm.SegmentTree`)
Each doctor-date schedule represents a working day (e.g. 09:00 to 17:00 with 30-min slots = 16 discrete slots). The tree allocates an array of $4N$ integers.
- **Leaf Nodes ($L = R$):** $T[node] = 1$ if slot is available; $0$ if booked, blocked, or in the past.
- **Internal Nodes:** $T[node] = T[2 \cdot node] + T[2 \cdot node + 1]$.
- **Find Next Slot Pruning:** If $T[node] == 0$, the search tree branch is pruned immediately, bypassing subtree traversal.
- **Atomic Concurrency:** Reentrant synchronization guarantees thread safety during simultaneous bookings and cancellations.

### 3.2 Custom Binary Min Heap (`com.medislot.algorithm.CustomBinaryMinHeap`)
- **Ordering Key:** Tuple $(P, T, ID)$ where $P_1 = \text{Urgent} < P_2 = \text{Normal}$. Ties are broken by creation epoch timestamp $T$.
- **Dynamic Array:** Automatically doubles array capacity upon reaching threshold ($size \ge capacity$).
- **Operation Counting:** Built-in counters record exact element comparisons and swaps during `siftUp` and `siftDown`.
- **Integrity Guarantee:** Zero usage of `java.util.PriorityQueue`.

### 3.3 Randomized Quickselect (`com.medislot.algorithm.RandomizedQuickselect`)
- **Pivot Selection:** `ThreadLocalRandom.current().nextInt(low, high + 1)`.
- **Three-Way Partitioning:** Separates elements into:
  - $[low \dots lt-1] < \text{pivot}$
  - $[lt \dots gt] == \text{pivot}$
  - $[gt+1 \dots high] > \text{pivot}$
- **Iterative Tail-Call Elimination:** Recursion is replaced with a `while (low <= high)` loop, guaranteeing $O(1)$ auxiliary memory overhead.

---

## 4. Synthetic Data Seeding Pipeline

On application startup, `DataSeedingService` seeds:
1. **1 Super Administrator:** `admin@medislot.com` / `Admin@123`
2. **18 Clinical Specializations:** Cardiology, Dermatology, Dentistry, Neurology, Orthopedics, Pediatrics, Gynecology, ENT, Ophthalmology, Psychiatry, General Medicine, Urology, Gastroenterology, Pulmonology, Endocrinology, Oncology, Radiology, Nephrology.
3. **36 Board-Certified Doctors:** 2 specialists per division with realistic fees ($55 - $160), qualifications, bios, and room numbers.
4. **255 Registered Patients:** Detailed medical records, blood groups, emergency contacts.
5. **10,050 Historical Completed Appointments:** Seeded with realistic clinical waiting times (centered around 25-35 minutes with long tail distribution) to feed the Quickselect analytics engine.
6. **Rolling 30-Day Future Bookings:** Existing appointments and available slots.
7. **Active Waiting-List Requests:** Including an active 15-minute slot offer ready for acceptance by demo patient `patient.john@medislot.com`.

---

## 5. Security & Role-Based Permissions

MediSlot enforces strict separation of concerns across three roles:
- **`ROLE_PATIENT`:** Can browse doctors, query availability, book appointments, cancel their own appointments, join waiting lists, accept/decline offers, and update their personal health profile.
- **`ROLE_DOCTOR`:** Access to their daily patient queue, check-in patients, start/complete consultations with clinical notes, block/unblock time slots, and monitor doctor utilization metrics.
- **`ROLE_ADMIN`:** Full administrative control: manage doctor faculty (add, edit, activate/deactivate), audit all appointments, monitor hospital analytics, and run the **Algorithm Performance Lab**.

---

## 6. Conclusion

MediSlot demonstrates that fundamental computer science data structures—when designed rigorously from first principles—solve complex, high-concurrency real-world challenges. The integration of custom Segment Trees, Binary Min Heaps, and Randomized Quickselect establishes a scalable, high-performance platform capable of powering modern outpatient healthcare delivery.
