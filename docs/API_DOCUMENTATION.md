# MediSlot REST API Documentation
**Base URL:** `http://localhost:8080/api`  
**Authentication:** HTTP Bearer Token (`Authorization: Bearer <JWT>`)

---

## 1. Authentication Endpoints

### 1.1 User Login
- **Method:** `POST`
- **Path:** `/api/auth/login`
- **Auth:** Public
- **Request Body:**
```json
{
  "email": "patient.john@medislot.com",
  "password": "Patient@123"
}
```
- **Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsIn...",
  "tokenType": "Bearer",
  "userId": 38,
  "email": "patient.john@medislot.com",
  "fullName": "John Doe",
  "role": "PATIENT",
  "profileId": 1
}
```

### 1.2 Patient Registration
- **Method:** `POST`
- **Path:** `/api/auth/register`
- **Auth:** Public (Patients only)
- **Request Body:**
```json
{
  "fullName": "Sarah Jenkins",
  "email": "sarah.j@example.com",
  "password": "SecurePassword@123",
  "phone": "+1-555-4321",
  "dateOfBirth": "1994-08-22",
  "gender": "Female",
  "bloodGroup": "A+",
  "emergencyContact": "+1-555-9988",
  "address": "120 Elm St, Boston, MA"
}
```

---

## 2. Doctor Directory & Public Endpoints

### 2.1 List All Specializations
- **Method:** `GET`
- **Path:** `/api/specializations`
- **Auth:** Public
- **Response:** Returns list of 18 medical specializations with keys, names, and descriptions.

### 2.2 Search Doctor Directory
- **Method:** `GET`
- **Path:** `/api/doctors/directory?specialization=CARDIOLOGY&search=Vance`
- **Auth:** Public
- **Response (200 OK):**
```json
[
  {
    "id": 1,
    "userId": 2,
    "fullName": "Dr. Marcus Vance",
    "email": "doctor.cardio@medislot.com",
    "specialization": "CARDIOLOGY",
    "specializationDisplayName": "Cardiology",
    "qualifications": "MD, DM (Cardiology), FACC",
    "yearsOfExperience": 18,
    "consultationFee": 120.0,
    "rating": 4.9,
    "bio": "Senior Interventional Cardiologist...",
    "department": "Cardiovascular Sciences",
    "roomNumber": "Room 401",
    "active": true
  }
]
```

---

## 3. Slot Availability (Segment Tree Query)

### 3.1 Get Doctor Availability by Date
- **Method:** `GET`
- **Path:** `/api/appointments/slots?doctorId=1&date=2026-11-25`
- **Auth:** Public / Authenticated
- **Description:** Evaluates doctor daily schedule in $O(\log N)$ time using custom Segment Tree.
- **Response (200 OK):**
```json
{
  "doctorId": 1,
  "doctorName": "Dr. Marcus Vance",
  "specialization": "Cardiology",
  "scheduleDate": "2026-11-25",
  "totalSlots": 16,
  "availableCount": 11,
  "slots": [
    {
      "slotIndex": 0,
      "startTime": "09:00",
      "endTime": "09:30",
      "available": true,
      "reason": "AVAILABLE"
    },
    {
      "slotIndex": 1,
      "startTime": "09:30",
      "endTime": "10:00",
      "available": false,
      "reason": "BOOKED"
    }
  ],
  "suggestions": [
    {
      "type": "SAME_DOCTOR_NEXT_SLOT",
      "message": "Next slot today with Dr. Marcus Vance at 10:00",
      "doctorId": 1,
      "doctorName": "Dr. Marcus Vance",
      "date": "2026-11-25",
      "slotIndex": 2,
      "startTime": "10:00",
      "endTime": "10:30"
    }
  ]
}
```

---

## 4. Patient Operations

### 4.1 Book Appointment
- **Method:** `POST`
- **Path:** `/api/patient/book`
- **Auth:** `ROLE_PATIENT`
- **Request Body:**
```json
{
  "doctorId": 1,
  "appointmentDate": "2026-11-25",
  "slotIndex": 2,
  "priority": "URGENT",
  "reasonForVisit": "Evaluation of chest pressure upon exertion"
}
```

### 4.2 Cancel Appointment
- **Method:** `POST`
- **Path:** `/api/patient/cancel/{id}`
- **Auth:** `ROLE_PATIENT` / `ROLE_DOCTOR`
- **Effect:** Slot restored via Segment Tree (`updateSlot(idx, true)`), and offered to highest-priority waiting patient via Binary Min Heap.

### 4.3 Reschedule Appointment
- **Method:** `POST`
- **Path:** `/api/patient/reschedule/{id}`
- **Request Body:**
```json
{
  "newAppointmentDate": "2026-11-28",
  "newSlotIndex": 4
}
```

### 4.4 Join Priority Waiting List
- **Method:** `POST`
- **Path:** `/api/patient/waiting-list/join`
- **Request Body:**
```json
{
  "doctorId": 1,
  "specialization": "CARDIOLOGY",
  "preferredDate": "2026-11-26",
  "priorityLevel": "URGENT",
  "preferredTimeRange": "MORNING"
}
```

### 4.5 Accept Offered Slot
- **Method:** `POST`
- **Path:** `/api/patient/waiting-list/offer/{id}/accept`
- **Effect:** Converts offer to Confirmed appointment within 15-minute window.

---

## 5. Doctor Clinical Operations

### 5.1 Patient Check-In
- **Method:** `POST`
- **Path:** `/api/doctor/appointment/{id}/checkin`
- **Auth:** `ROLE_DOCTOR`

### 5.2 Start Consultation (Wait Time Clock)
- **Method:** `POST`
- **Path:** `/api/doctor/appointment/{id}/start`
- **Auth:** `ROLE_DOCTOR`
- **Effect:** Computes actual waiting time in minutes.

### 5.3 Complete Consultation
- **Method:** `POST`
- **Path:** `/api/doctor/appointment/{id}/complete`
- **Request Body:**
```json
{
  "notes": "Patient examined. Resting ECG normal. Prescribed lifestyle modification."
}
```

### 5.4 Block / Unblock Slot
- **Method:** `POST`
- **Path:** `/api/doctor/schedule/slot/{slotIndex}/toggle-block?date=2026-11-26&block=true`
- **Effect:** Updates doctor schedule and modifies Segment Tree leaf node immediately.

---

## 6. Admin & Algorithm Performance Lab Endpoints

### 6.1 Hospital Analytics & Quickselect Stats
- **Method:** `GET`
- **Path:** `/api/admin/stats`
- **Auth:** `ROLE_ADMIN`
- **Response:** Returns hospital counts, utilization, and Quickselect-calculated `medianWaitingTimeMinutes`, `p90WaitingTimeMinutes`, and `p95WaitingTimeMinutes`.

### 6.2 Run Algorithmic Benchmark Suite
- **Method:** `POST`
- **Path:** `/api/benchmark/run`
- **Auth:** Public / Admin
- **Request Body:**
```json
{
  "algorithmCategory": "ALL",
  "datasetSize": 1000,
  "iterations": 5
}
```
- **Response (200 OK):**
```json
{
  "requestedDatasetSize": 1000,
  "executionTimestamp": 1732450000000,
  "experiments": [
    {
      "category": "Availability Management",
      "customAlgorithm": "Segment Tree (Custom Unit 3)",
      "baselineAlgorithm": "Linear Array Scan",
      "datasetSize": 1000,
      "customTimeMicros": 298.5,
      "baselineTimeMicros": 3842.1,
      "customOperations": 18740,
      "baselineOperations": 974100,
      "timeComplexityCustom": "O(log N) query / O(log N) update",
      "timeComplexityBaseline": "O(N) query / O(1) update",
      "speedup": 12.87,
      "operationsReductionPercent": 98.1,
      "correctnessMatched": true,
      "correctnessDetails": "Identical total available count across queries"
    }
  ]
}
```
