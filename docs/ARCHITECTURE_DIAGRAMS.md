# MediSlot System Architecture & Data Flow Diagrams
**Course:** B.Tech Advanced Algorithms (CIA 3)

---

## 1. High-Level System Architecture

```mermaid
graph TD
    subgraph ClientLayer["Frontend Client (React 18 + Vite + Tailwind CSS)"]
        UI_Landing["Public Hospital Portal\n(18 Specialties, 36 Doctors)"]
        UI_Patient["Patient Portal\n(Segment Tree Booking, Waitlist)"]
        UI_Doctor["Doctor Clinical Portal\n(Queue, Check-in, Slot Block)"]
        UI_Admin["Admin Analytics & Lab\n(CO5 Quickselect, Benchmarks)"]
    end

    subgraph SecurityLayer["Spring Security 6 & JWT Filter"]
        JWT_Filter["JwtAuthenticationFilter\n(Stateless Token Verification)"]
        CORS_Filter["WebCorsConfig\n(Cross-Origin Access)"]
    end

    subgraph CoreAlgorithmLayer["Core Custom Algorithms (Implemented from Scratch in Java)"]
        Alg_ST["Segment Tree (Unit 3)\n- O(log N) Availability Count\n- O(log N) Next Slot Search\n- O(log N) Atomic Point Updates"]
        Alg_Heap["Custom Binary Min Heap (Unit 2)\n- Urgent > Normal Priority\n- FCFS Timestamp Tie-Breaker\n- 15-Min Reassignment Window"]
        Alg_QS["Randomized Quickselect (Unit 6 / CO5)\n- Dutch National Flag 3-Way Partition\n- Expected O(N) Linear Time\n- Median, P90, P95 Waiting Times"]
    end

    subgraph ServiceLayer["Spring Boot Services"]
        Svc_Sched["DoctorScheduleService\n(Segment Tree Caching & Suggestions)"]
        Svc_Appt["AppointmentService\n(Atomic Booking & Cancellation)"]
        Svc_Wait["WaitingListService\n(Min-Heap Reallocation Engine)"]
        Svc_Admin["AdminAnalyticsService\n(Quickselect Statistical Pipeline)"]
        Svc_Bench["AlgorithmBenchmarkService\n(Empirical Performance Lab)"]
    end

    subgraph PersistenceLayer["Database Storage (Spring Data JPA)"]
        DB_H2["H2 In-Memory Database (Local Dev)\n10,000+ Completed Records Seeded"]
        DB_PG["PostgreSQL (Railway Production Deploy)"]
    end

    UI_Landing --> SecurityLayer
    UI_Patient --> SecurityLayer
    UI_Doctor --> SecurityLayer
    UI_Admin --> SecurityLayer

    SecurityLayer --> Svc_Sched
    SecurityLayer --> Svc_Appt
    SecurityLayer --> Svc_Wait
    SecurityLayer --> Svc_Admin
    SecurityLayer --> Svc_Bench

    Svc_Sched <--> Alg_ST
    Svc_Wait <--> Alg_Heap
    Svc_Admin <--> Alg_QS
    Svc_Bench <--> Alg_ST
    Svc_Bench <--> Alg_Heap
    Svc_Bench <--> Alg_QS

    Svc_Sched --> PersistenceLayer
    Svc_Appt --> PersistenceLayer
    Svc_Wait --> PersistenceLayer
    Svc_Admin --> PersistenceLayer
```

---

## 2. Cancellation & Waiting-List Offer Sequence Diagram

```mermaid
sequenceDiagram
    autonumber
    actor PatientA as Patient A (Scheduled)
    participant ApptSvc as AppointmentService
    participant SegTree as SegmentTree (Unit 3)
    participant WaitSvc as WaitingListService
    participant MinHeap as CustomBinaryMinHeap (Unit 2)
    actor PatientB as Patient B (Waiting List)

    PatientA ->> ApptSvc: cancelAppointment(apptId)
    ApptSvc ->> ApptSvc: Set status = CANCELLED
    ApptSvc ->> SegTree: updateSlot(slotIndex, isAvailable=true)
    Note over SegTree: Restores leaf node to 1<br/>Propagates sum O(log N)
    ApptSvc ->> WaitSvc: offerSlotToHighestPriorityCandidate()
    WaitSvc ->> MinHeap: Build heap from eligible candidates
    Note over MinHeap: Urgent (rank 1) > Normal (rank 2)<br/>Ties broken by earliest timestamp (FCFS)
    WaitSvc ->> MinHeap: extractMin()
    MinHeap -->> WaitSvc: Return winner (Patient B)
    WaitSvc ->> WaitSvc: Set status = OFFERED, offerExpiresAt = now + 15m
    WaitSvc -->> PatientB: Display 15-Minute Countdown Offer Banner
    alt Patient B Accepts within 15 mins
        PatientB ->> WaitSvc: acceptOffer(entryId)
        WaitSvc ->> SegTree: updateSlot(slotIndex, isAvailable=false)
        WaitSvc ->> ApptSvc: Create CONFIRMED Appointment
    else Patient B Declines or 15 mins Expire
        PatientB ->> WaitSvc: declineOffer(entryId) OR Scheduled Sweeper triggers
        WaitSvc ->> WaitSvc: Set status = DECLINED / EXPIRED
        WaitSvc ->> MinHeap: extractMin() next eligible candidate
    end
```

---

## 3. Double-Booking Prevention Flowchart

```mermaid
flowchart TD
    Start["Patient Submits Slot Booking"] --> ST_Check{"Segment Tree check:\nisSlotAvailable(idx)?"}
    ST_Check -- "No (Tree Node = 0)" --> Reject1["Reject: Slot Unavailable\nOffer Suggestions Box"]
    ST_Check -- "Yes (Tree Node = 1)" --> Lock["Acquire Service Layer Lock"]
    Lock --> DB_Check{"Database Check:\nActive Booking exists for\n(docId, date, slotIndex)?"}
    DB_Check -- "Yes (Conflict)" --> Rollback["Tree Leaf Re-synced to 0"]
    Rollback --> Reject2["Reject: Double-Booking Prevented"]
    DB_Check -- "No (Free)" --> SaveDB["Persist Appointment in DB\nSet Version Token"]
    SaveDB --> ST_Update["Update Segment Tree:\nupdateSlot(idx, false) in O(log N)"]
    ST_Update --> Success["Confirm Appointment (HTTP 200)"]
```
