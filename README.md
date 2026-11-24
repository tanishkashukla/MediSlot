# MediSlot — Smart Appointment Scheduling System
**B.Tech Advanced Algorithms CIA 3 Assessment Project**

MediSlot is a full-stack, enterprise-grade hospital appointment scheduling web application engineered to intelligently manage doctor availability, eliminate double-booking, prioritize urgent requests, and provide clinical waiting-time analytics.

All core algorithms are implemented **from scratch in Java** without third-party algorithm libraries:
- **Unit 3:** Custom **Segment Tree** for doctor slot availability, $O(\log N)$ range queries, next available slot search, and atomic updates.
- **Unit 2:** Custom **Binary Min Heap** for multi-tier waiting list management with Urgent priority precedence, FCFS timestamp tie-breaking, and a 15-minute cancellation reallocation window.
- **Unit 6 / CO5 (Mandatory):** **Randomized Quickselect** with Dutch National Flag 3-Way Partitioning to compute Median, 90th percentile, and 95th percentile waiting times from 10,000+ historical records in Expected $O(N)$ time.

---

## Technology Stack

- **Frontend:** React 18, Vite 5, Tailwind CSS, Lucide Icons, Recharts, React Router DOM
- **Backend:** Java Spring Boot 3.2.5, JDK 17+
- **Database:** Embedded H2 (in-memory for local development) & PostgreSQL (for hosted production)
- **Security:** Spring Security 6 with stateless JWT (HMAC SHA-256)
- **Deployment:** Vercel (Frontend SPA) & Railway (Java Backend)

---

## Demo Login Credentials

The system seeds 36 doctors, 255 patients, and 10,050 historical completed appointments automatically on launch:

| Role | Email | Password | Access / Dashboard |
| :--- | :--- | :--- | :--- |
| **Admin** | `admin@medislot.com` | `Admin@123` | `/admin` — Hospital Overview, Doctor Management, Quickselect Analytics, Algorithm Lab |
| **Doctor** | `doctor.cardio@medislot.com` | `Doctor@123` | `/doctor` — Daily Queue, Patient Check-In, Start/Complete Visits, Slot Block/Unblock |
| **Patient** | `patient.john@medislot.com` | `Patient@123` | `/patient` — 7-Step Booking, My Appointments, Active 15-Min Offer Banner, Profile |

---

## Quick Start (Localhost Execution)

No Docker or paid cloud services required. Runs 100% locally.

### Option A: One-Click Windows Launch (Recommended)
Double-click `run-all.bat` or run in terminal:
```bash
.\run-all.bat
```
This opens two terminal windows:
1. **Spring Boot Backend:** Starts on `http://localhost:8080` (seeds 10,050+ records on first boot).
2. **React Vite Frontend:** Starts on `http://localhost:5173`.

### Option B: Manual Terminal Execution

#### 1. Start Java Spring Boot Backend:
```bash
cd backend
mvn spring-boot:run
```
*(Or on Windows with Maven Wrapper: `mvnw.cmd spring-boot:run`)*  
Backend runs on: `http://localhost:8080`  
H2 Web Console available at: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:medislotdb`, user: `sa`, password: `password`)

#### 2. Start React Vite Frontend:
```bash
cd frontend
npm install
npm run dev
```
Frontend runs on: `http://localhost:5173`

---

## Core Algorithm Implementation Details

### 1. Segment Tree (Unit 3 — `com.medislot.algorithm.SegmentTree`)
- **Purpose:** Doctor daily schedule availability management.
- **Structure:** Array of $4N$ nodes representing discrete 30-minute intervals (16 slots/day).
- **Complexity:**
  - Build: $O(N)$
  - Count available slots in time window $[ql, qr]$: $O(\log N)$
  - Find earliest available slot $\ge fromIndex$: $O(\log N)$ with subtree zero-sum pruning
  - Book / Cancel / Block / Unblock point updates: $O(\log N)$
- **Integration:** Integrated into `DoctorScheduleService` and `AppointmentService`. Only genuinely free slots appear in the booking interface.

### 2. Custom Binary Min Heap (Unit 2 — `com.medislot.algorithm.CustomBinaryMinHeap`)
- **Purpose:** Priority queue for outpatient waiting lists without using `java.util.PriorityQueue`.
- **Priority Rules:**
  1. Urgent priority ($rank = 1$) precedes Normal priority ($rank = 2$).
  2. Patients with equal priority are ordered First-Come-First-Served (FCFS) by creation timestamp.
- **Workflow:** When an appointment is cancelled, the Min Heap pops the highest-priority eligible candidate, granting them an exclusive 15-minute offer window with a live countdown timer. If declined or expired, the next eligible patient in the heap is offered the slot.

### 3. Randomized Quickselect (Unit 6 / CO5 — `com.medislot.algorithm.RandomizedQuickselect`)
- **Purpose:** Calculates Median, 90th percentile, and 95th percentile waiting times from 10,050 completed appointments.
- **Features:** True random pivot selection and **Dutch National Flag Three-Way Partitioning** ($< v, == v, > v$) to eliminate performance degradation on duplicate waiting times.
- **Complexity:** Expected $O(N)$ linear time and $O(1)$ auxiliary memory (iterative tail-call elimination), avoiding the $O(N \log N)$ cost of full sorting.

---

## Algorithm Performance Lab (Requirement #7)

Located inside the Admin Dashboard (`/admin/performance-lab`):
- Run experiments across **100**, **1,000**, and **10,000** records.
- Compares:
  1. **Segment Tree vs Linear Scan**
  2. **Custom Binary Min Heap vs Linear Search**
  3. **Randomized Quickselect vs Dual-Pivot Quicksort**
- Measures actual microsecond wall-clock time and exact operation counters with warm-up runs.
- Asserts 100% mathematical equality between custom algorithms and baselines.

---

## Running Automated Tests

Run the test suite containing unit tests, boundary tests, baseline comparisons, and concurrent double-booking tests:
```bash
cd backend
mvn test
```

### Documented Test Suites:
- `SegmentTreeTest.java`: 5 documented tests (range query, next slot, updates, boundary edge cases, randomized comparison with `LinearScanBaseline`).
- `CustomBinaryMinHeapTest.java`: 5 documented tests (Urgent precedence, FCFS tie-breaking, dynamic resizing, node removal, randomized comparison with `LinearSearchHeapBaseline`).
- `RandomizedQuickselectTest.java`: 5 documented tests (k-th selection, odd/even medians, p90/p95 percentiles, duplicate handling with 3-way partition, randomized comparison with `Arrays.sort`).
- `AppointmentConcurrencyTest.java`: 10 simultaneous threads attempting to book the exact same slot to verify race condition prevention.
- `SecurityAuthTest.java`: JWT generation, password validation, role-based security.

---

## Production Deployment Guide

### Frontend Deployment (Vercel)
1. Push project to GitHub.
2. In Vercel, set root directory to `frontend`.
3. Set Build Command: `npm run build` and Output Directory: `dist`.
4. In `vercel.json`, update the backend rewrite destination with your Railway backend domain.

### Backend Deployment (Railway)
1. In Railway, create a new project with PostgreSQL.
2. Connect your GitHub repository and set root directory to `backend`.
3. Railway uses `railway.json` with the Nixpacks builder:
   `mvn spring-boot:run -Dspring-boot.run.profiles=prod`
4. Set Environment Variables:
   - `DATABASE_URL`: `jdbc:postgresql://${{PGHOST}}:${{PGPORT}}/${{PGDATABASE}}`
   - `DATABASE_USERNAME`: `${{PGUSER}}`
   - `DATABASE_PASSWORD`: `${{PGPASSWORD}}`
   - `JWT_SECRET`: `404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970`

---

## Project Documentation Index

- `docs/PROJECT_PROPOSAL.md` — Problem statement, solution architecture, and goals.
- `docs/TECHNICAL_REPORT.md` — Formal technical report for B.Tech CIA 3 submission.
- `docs/ARCHITECTURE_DIAGRAMS.md` — Mermaid flowcharts, sequence diagrams, and class layouts.
- `docs/DATABASE_SCHEMA.md` — Complete relational schema, indexes, and constraints.
- `docs/API_DOCUMENTATION.md` — Full REST API specifications with JSON payloads.
- `docs/ALGORITHM_ANALYSIS.md` — Mathematical derivations, pseudocode, and proofs.
- `docs/BENCHMARK_REPORT.md` — Empirical benchmark measurements and analysis.
- `docs/VIVA_PREPARATION_GUIDE.md` — Evaluator Q&A and defense reference.
- `docs/DEMONSTRATION_SCRIPT.md` — Step-by-step evaluator presentation script.
