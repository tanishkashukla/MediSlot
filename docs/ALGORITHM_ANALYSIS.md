# Comprehensive Algorithm Analysis & Complexity Documentation
**Course Module:** B.Tech Advanced Algorithms Assessment (CIA 3)  
**Algorithms Analyzed:**
- **Algorithm 1:** Segment Tree (Unit 3 — Advanced Trees & Range Queries)
- **Algorithm 2:** Custom Binary Min Heap (Unit 2 — Priority Queues & Heaps)
- **Algorithm 3:** Randomized Quickselect with 3-Way Partitioning (Unit 6 / CO5 — Order Statistics)

---

## 1. Algorithm 1: Segment Tree for Doctor Slot Availability (Unit 3)

### 1.1 Mathematical Formulation
Let a doctor's working day be partitioned into $N$ non-overlapping time slots $S = \{s_0, s_1, \dots, s_{N-1}\}$ where:
$$s_i = \begin{cases} 1, & \text{if slot } i \text{ is genuinely available} \\ 0, & \text{if slot } i \text{ is booked, blocked, or in the past} \end{cases}$$

A binary Segment Tree $T$ of height $h = \lceil \log_2 N \rceil + 1$ is constructed where each node $v$ represents an interval $[L, R]$ ($0 \le L \le R < N$):
- If $L = R$, node $v$ is a leaf: $T[v] = s_L$.
- If $L < R$, node $v$ has children $v_{\text{left}} = 2v$ and $v_{\text{right}} = 2v + 1$:
$$T[v] = T[2v] + T[2v + 1] = \sum_{i=L}^{R} s_i$$

### 1.2 Core Operations & Pseudocode

#### 1. Range Availability Query ($ql, qr$)
```
Function QueryRange(node, start, end, ql, qr):
    if ql <= start and end <= qr then
        return tree[node]
    if end < ql or start > qr then
        return 0
    mid = start + (end - start) / 2
    leftSum = QueryRange(2 * node, start, mid, ql, qr)
    rightSum = QueryRange(2 * node + 1, mid + 1, end, ql, qr)
    return leftSum + rightSum
```

#### 2. Find Next Available Slot ($\ge \text{fromIndex}$)
```
Function FindNextAvailable(node, start, end, ql, qr):
    if tree[node] == 0 or end < ql or start > qr then
        return -1
    if start == end then
        return start
    mid = start + (end - start) / 2
    leftRes = FindNextAvailable(2 * node, start, mid, ql, qr)
    if leftRes != -1 then
        return leftRes
    return FindNextAvailable(2 * node + 1, mid + 1, end, ql, qr)
```

#### 3. Point Update (Book, Cancel, Block, Unblock)
```
Function UpdateSlot(node, start, end, idx, val):
    if start == end then
        tree[node] = val
        return
    mid = start + (end - start) / 2
    if idx <= mid then
        UpdateSlot(2 * node, start, mid, idx, val)
    else
        UpdateSlot(2 * node + 1, mid + 1, end, idx, val)
    tree[node] = tree[2 * node] + tree[2 * node + 1]
```

### 1.3 Complexity Derivations

| Operation | Segment Tree | Linear Scan Baseline | Speedup / Impact |
| :--- | :---: | :---: | :---: |
| **Tree Construction** | $O(N)$ | $O(N)$ | Same initial allocation |
| **Count Available in Range** | $O(\log N)$ | $O(N)$ | $N / \log N$ speedup |
| **Find Next Available Slot** | $O(\log N)$ | $O(N)$ | Early pruning when subtree sum $= 0$ |
| **Book / Cancel Update** | $O(\log N)$ | $O(1)$ | Tree propagation along single path |
| **Auxiliary Space** | $4N \text{ ints}$ | $N \text{ ints}$ | $O(N)$ linear space bound |

**Proof for Range Query $O(\log N)$:**  
At each level of the tree, at most 4 nodes are visited because at most two subtrees contain the boundaries $ql$ and $qr$. Since tree height is $\lceil \log_2 N \rceil$, the maximum number of recursive calls is $4 \lceil \log_2 N \rceil \in O(\log N)$.

---

## 2. Algorithm 2: Custom Binary Min Heap (Unit 2)

### 2.1 Multi-Tier Priority Model
A waiting-list entry is represented as tuple $E = (P, T, ID)$ where:
- $P \in \{1, 2\}$ is administrative priority ($1 = \text{Urgent}, 2 = \text{Normal}$).
- $T \in \mathbb{N}$ is the epoch millisecond registration timestamp.
- $ID \in \mathbb{N}$ is the unique database primary key.

The strict total ordering relation $\prec$ for min-heap arrangement is:
$$E_A \prec E_B \iff (P_A < P_B) \lor (P_A = P_B \land T_A < T_B) \lor (P_A = P_B \land T_A = T_B \land ID_A < ID_B)$$

### 2.2 Heap Operations & Invariants
In array representation:
- Parent: $\text{parent}(i) = \lfloor (i - 1) / 2 \rfloor$
- Left Child: $\text{left}(i) = 2i + 1$
- Right Child: $\text{right}(i) = 2i + 2$

Min-Heap Invariant: For all $i > 0$, $\text{heap}[\text{parent}(i)] \preceq \text{heap}[i]$.

#### Sift-Up Pseudocode
```
Procedure SiftUp(index):
    while index > 0 do:
        p = (index - 1) / 2
        if heap[index] < heap[p] then
            swap(heap[index], heap[p])
            index = p
        else
            break
```

#### Sift-Down Pseudocode
```
Procedure SiftDown(index):
    while true do:
        left = 2 * index + 1
        right = 2 * index + 2
        smallest = index
        if left < size and heap[left] < heap[smallest] then
            smallest = left
        if right < size and heap[right] < heap[smallest] then
            smallest = right
        if smallest != index then
            swap(heap[index], heap[smallest])
            index = smallest
        else
            break
```

### 2.3 Complexity Derivations

| Operation | Custom Binary Min Heap | Unsorted List Baseline | Operational Benefit |
| :--- | :---: | :---: | :---: |
| **Insert (Enqueue)** | $O(\log N)$ | $O(1)$ | logarithmic sift-up |
| **ExtractMin (Reallocate)** | $O(\log N)$ | $O(N)$ | immediate root access + sift-down |
| **Peek Highest Priority** | $O(1)$ | $O(N)$ | root index $0$ |
| **Total for $N$ Allocations** | $O(N \log N)$ | $O(N^2)$ | quadratic vs near-linear |
| **Space Complexity** | $O(N)$ | $O(N)$ | compact contiguous array |

---

## 3. Algorithm 3: Randomized Quickselect (Unit 6 / CO5 Mandatory)

### 3.1 Purpose & Role in MediSlot
MediSlot computes patient waiting time metrics (Median, 90th percentile, 95th percentile) across 10,000+ historical records. Completely sorting the dataset using Dual-Pivot Quicksort requires $O(N \log N)$ time and array copying. Randomized Quickselect finds any $k$-th order statistic in Expected $O(N)$ linear time and $O(1)$ auxiliary memory.

### 3.2 Three-Way Partitioning (Dutch National Flag)
Real hospital waiting-time datasets exhibit frequent duplicate values (e.g., standard 15, 20, 30, and 45-minute consultations). Standard Lomuto or Hoare two-way partitioning degrades to $O(N^2)$ when duplicates dominate.

Three-way partitioning divides the sub-array $[low \dots high]$ around pivot $v$ into three contiguous zones:
1. $[low \dots lt - 1]$ elements strictly $< v$
2. $[lt \dots gt]$ elements strictly $== v$
3. $[gt + 1 \dots high]$ elements strictly $> v$

```
Function Quickselect(arr, k):
    low = 0, high = arr.length - 1
    while low <= high do:
        if low == high then return arr[low]
        pivotIndex = Random(low, high)
        pivotValue = arr[pivotIndex]
        lt = low, i = low, gt = high
        while i <= gt do:
            if arr[i] < pivotValue then
                swap(arr, lt, i); lt++; i++
            else if arr[i] > pivotValue then
                swap(arr, i, gt); gt--
            else
                i++
        if k < lt then
            high = lt - 1
        else if k > gt then
            low = gt + 1
        else
            return arr[k]  // Target is inside equal partition!
```

### 3.3 Theoretical Proof of Expected $O(N)$ Complexity
Let $T(N)$ be the expected running time on an array of size $N$. With a uniformly chosen random pivot, the target partition size is at most $3N/4$ with probability at least $1/2$ (a "balanced" split).

$$T(N) \le T(3N/4) + cN$$

By Master Theorem / substitution method:
$$T(N) \le cN \sum_{j=0}^{\infty} \left(\frac{3}{4}\right)^j = cN \cdot \frac{1}{1 - 3/4} = 4cN \in O(N)$$

Thus, the expected execution time is strictly linear $O(N)$, saving millions of operations compared to $O(N \log N)$ sorting on hospital logs.
