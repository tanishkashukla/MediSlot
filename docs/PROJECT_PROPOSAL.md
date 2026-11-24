# Project Proposal: Smart Appointment Scheduling System (MediSlot)
**Course:** B.Tech Computer Science & Engineering — Advanced Algorithms (CIA 3)  
**Academic Component Focus:** Unit 2 (Binary Min Heap), Unit 3 (Segment Tree), Unit 6 / CO5 (Randomized Quickselect)

---

## 1. Executive Summary

Healthcare institutions face severe operational inefficiencies arising from double-booking, sub-optimal doctor calendar utilization, manual waiting list tracking, and inability to analyze patient waiting times without prohibitive computation. **MediSlot** is an intelligent, full-stack hospital appointment scheduling platform engineered from the ground up to replace linear database searches and naive FIFO queues with custom, mathematically rigorous algorithms implemented from scratch in Java.

MediSlot bridges algorithm design with real-world clinical operations:
1. **Doctor Availability Management (Unit 3):** Replaces $O(N)$ linear scans with a custom **Segment Tree** providing $O(\log N)$ slot counting, next-slot queries, point booking updates, and cancellation restoration.
2. **Priority Waiting-List Reassignment (Unit 2):** Implements a custom **Binary Min Heap** resolving multi-tier priorities (Urgent vs Normal) with First-Come-First-Served (FCFS) timestamp tie-breaking. On cancellation, slots are automatically offered with a 15-minute response window.
3. **Clinical Waiting-Time Analytics (Unit 6 / CO5 Mandatory):** Employs **Randomized Quickselect** with Dutch National Flag Three-Way Partitioning to compute Median, 90th percentile, and 95th percentile waiting times in Expected $O(N)$ time and $O(1)$ auxiliary space across 10,000+ historical records without full dataset sorting.

---

## 2. Problem Statement

Standard hospital management systems suffer from three fundamental computational and operational shortcomings:

1. **Scheduling Race Conditions & Scalability Bottlenecks:** Naive linear scans of doctor daily schedules fail to scale when checking availability across multi-specialty clinics. Concurrent booking requests frequently cause double-bookings.
2. **Unfair Waiting List Handling:** When patients cancel scheduled visits, slots are either left vacant or manually assigned on an arbitrary basis, ignoring urgent clinical needs and arrival precedence.
3. **Inefficient Percentile & KPI Computation:** Computing median and tail-percentile patient wait times via conventional $O(N \log N)$ sorting wastes CPU cycles and memory on large hospital audit logs.

---

## 3. Proposed Solution & Architecture

MediSlot solves these problems through an integrated three-tier architecture:

```
+-------------------------------------------------------------------------+
|                              REACT FRONTEND                             |
|    Vite + Tailwind CSS + Lucide Icons + Recharts Analytics + Router     |
|   [Patient Portal]     [Doctor Clinical Portal]     [Admin Portal]     |
+-------------------------------------------------------------------------+
                                    | HTTP / REST (JWT Auth)
+-------------------------------------------------------------------------+
|                         JAVA SPRING BOOT BACKEND                        |
|                                                                         |
|  +--------------------+  +--------------------+  +--------------------+  |
|  |    Segment Tree    |  |  Binary Min Heap   |  |   Quickselect CO5  |  |
|  |      (Unit 3)      |  |      (Unit 2)      |  |      (Unit 6)      |  |
|  | Slot Availability  |  | Priority Waiting   |  | Percentiles & KPIs |  |
|  | O(log N) Query/Upd |  | O(log N) Enq/Deq   |  | Expected O(N) Time |  |
|  +--------------------+  +--------------------+  +--------------------+  |
|                                                                         |
|    Spring Security + JWT  |  Scheduled Offer Sweeper  |  Benchmark Lab   |
+-------------------------------------------------------------------------+
                                    | Spring Data JPA / Hibernate
+-------------------------------------------------------------------------+
|                                DATABASE                                 |
|         H2 In-Memory (Local Dev) / PostgreSQL (Railway Hosted)          |
|    18 Specializations | 36 Doctors | 255 Patients | 10,000+ Records    |
+-------------------------------------------------------------------------+
```

---

## 4. Key Differentiators & Academic Value

- **Zero Third-Party Algorithm Libraries:** Custom Java implementations for Segment Tree, Binary Min Heap, and Randomized Quickselect.
- **Algorithm Performance Lab:** Interactive admin benchmark running live experiments on 100, 1,000, and 10,000 records with warmups, wall-clock timing, and operation counters.
- **Real Synthetic Dataset:** Pre-seeded with 36 doctors across 18 specializations, 255 patients, and 10,000+ historical completed visits.
- **Double-Booking Prevention:** Synchronized Segment Tree atomic state transitions combined with database version locking.
