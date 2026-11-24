# MediSlot Live Demonstration Script
**B.Tech Advanced Algorithms Project Assessment (CIA 3)**

This demonstration script provides an exact, step-by-step walkthrough for presenting MediSlot to professors, external examiners, or evaluators.

---

## Pre-Requisites & System Setup
1. Launch backend and frontend using `run-all.bat` (or separate terminals: `mvn spring-boot:run` on port 8080 and `npm run dev` on port 5173).
2. Open browser at `http://localhost:5173`.

---

## Phase 1: Landing Page & Doctor Directory (2 Minutes)

1. **Healthcare Landing Interface:**
   - Show the modern blue/teal healthcare hero section with live stats.
   - Point out the 18 hospital specialties grid (Cardiology, Dermatology, Dentistry, Neurology, Orthopedics, Pediatrics, etc.).
   - Click on "Cardiology" to filter the doctor faculty. Show that only Cardiology specialists are shown.
2. **Find Doctors Directory:**
   - Search for "Vance" or "Arthur".
   - Highlight transparent fees ($95 - $150), verified qualifications, and clinical experience.
   - Click "Book Appointment" on Dr. Marcus Vance to trigger the modal.

---

## Phase 2: Patient Portal & Algorithmic Booking (3 Minutes)

1. **Sign In as Demo Patient:**
   - Click "Sign In" $\to$ Click "Patient Demo" button $\to$ Logs in as `patient.john@medislot.com`.
2. **7-Step Booking Workflow:**
   - Navigate to **"Book New Appointment"**.
   - Step 1: Select "Cardiology".
   - Step 2: Select "Dr. Marcus Vance".
   - Step 3: Pick a date in the 30-day horizon (e.g. tomorrow).
   - Step 4: Select "Urgent Priority".
   - Step 5: Enter reason: "Chest discomfort upon exertion".
   - Step 6: Observe the **Segment Tree 30-min slot picker**. Point out that past, booked, or blocked slots are disabled/hidden. Select an available slot (e.g., 10:00 - 10:30).
   - Step 7: Click **"Confirm Appointment"**. The slot is atomically reserved and Segment Tree node updated.
3. **Double-Booking Prevention Demonstration:**
   - Open a secondary browser or incognito window. Try to book the exact same slot for Dr. Vance on the same date.
   - Observe the error: *"Slot was just reserved by another patient. Double-booking prevented."*
4. **Waiting List & 15-Minute Reassignment:**
   - On the Patient Dashboard, observe the **Active 15-Minute Slot Offer** banner.
   - Note the real-time countdown timer.
   - Click **"Accept Slot Now"** $\to$ The waiting list entry is confirmed into a scheduled appointment!

---

## Phase 3: Doctor Clinical Portal (2 Minutes)

1. **Sign In as Demo Doctor:**
   - Sign out $\to$ Sign In $\to$ Click "Doctor Demo" $\to$ `doctor.cardio@medislot.com` / `Doctor@123`.
2. **Daily Schedule & Clinical Actions:**
   - View today's schedule and patient queue.
   - Click **"Check In"** on the first patient. Status changes to `Checked In`.
   - Click **"Start Visit"**. Status changes to `In Consultation`. Explain that the system records the exact patient waiting time in minutes.
   - Click **"Complete Visit"** $\to$ Enter clinical notes: *"ECG normal. Prescribed beta-blocker."* $\to$ Completed!
3. **Slot & Schedule Manager:**
   - Navigate to **"Slot & Hours Manager"**.
   - Select tomorrow's date. Click **"Block Slot"** on slot #3.
   - The slot turns red (`BLOCKED`). Explain that this updates the Segment Tree leaf node to 0 immediately.

---

## Phase 4: Admin Portal & Algorithm Performance Lab (4 Minutes)

1. **Sign In as Administrator:**
   - Sign out $\to$ Sign In $\to$ Click "Admin Demo" $\to$ `admin@medislot.com` / `Admin@123`.
2. **Hospital Analytics & CO5 Quickselect:**
   - Navigate to **"Hospital Analytics"**.
   - Show the **Randomized Quickselect Statistical Summary Box (Unit 6 / CO5)**:
     - Median Waiting Time (e.g. ~24.5 min)
     - 90th Percentile Delay (e.g. ~54.0 min)
     - 95th Percentile Delay (e.g. ~74.0 min)
     - Sample size: **10,000+ completed records analyzed in Expected $O(N)$ time** without full sorting!
3. **Interactive Algorithm Performance Lab (Requirement #7):**
   - Click **"Algorithm Lab"** in the sidebar.
   - Select dataset size: **100 records** $\to$ Click Re-run.
   - Select dataset size: **1,000 records** $\to$ Observe microsecond timings and operation counters.
   - Select dataset size: **10,000 records** $\to$ Run experiment.
   - Walk through the three comparative experiments:
     - **Experiment 1:** Segment Tree vs Linear Scan ($O(\log N)$ vs $O(N)$, $> 20\times$ speedup at $10k$).
     - **Experiment 2:** Custom Binary Min Heap vs Linear Search ($O(N \log N)$ vs $O(N^2)$, $> 15\times$ speedup).
     - **Experiment 3:** Randomized Quickselect vs Dual-Pivot Quicksort (Expected $O(N)$ vs $O(N \log N)$, $> 3.5\times$ speedup).
   - Point out the **Correctness Verified (100% Agreement)** badges confirming mathematical correctness on identical data!
   - Show the interactive Recharts bar charts comparing execution time and operations.
