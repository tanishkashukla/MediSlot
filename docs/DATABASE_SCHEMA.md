# MediSlot Relational Database Schema Documentation
**Database Engines:** H2 (In-Memory for Local Development) & PostgreSQL (Railway Production)

---

## 1. Entity-Relationship Overview

```
+-------------------+           +-----------------------+
|       USERS       | 1       1 |    PATIENT_PROFILES   |
|-------------------|-----------|-----------------------|
| id (PK)           |           | id (PK)               |
| email (UNIQUE)    |           | user_id (FK -> users) |
| password          |           | date_of_birth         |
| full_name         |           | gender, blood_group   |
| role, active      |           | medical_history       |
+-------------------+           +-----------------------+
          | 1
          |
          | 1
+-----------------------+ 1     * +------------------------+
|    DOCTOR_PROFILES    |---------|    DOCTOR_SCHEDULES    |
|-----------------------|         |------------------------|
| id (PK)               |         | id (PK)                |
| user_id (FK -> users) |         | doctor_id (FK)         |
| specialization (ENUM) |         | schedule_date          |
| qualifications, fee   |         | total_slots (16)       |
| work_days, work_hours |         | blocked_slots          |
| rating, department    |         | version (LOCK)         |
+-----------------------+         +------------------------+
          | 1                                 |
          |                                   |
          *                                   *
+----------------------------------------------------------+
|                       APPOINTMENTS                       |
|----------------------------------------------------------|
| id (PK)                                                  |
| patient_id (FK -> users)                                 |
| doctor_id (FK -> doctor_profiles)                        |
| appointment_date, slot_index                             |
| start_time, end_time                                     |
| priority (NORMAL, URGENT)                                |
| status (CONFIRMED, CHECKED_IN, COMPLETED, CANCELLED)     |
| waiting_time_minutes (DOUBLE, for Quickselect Analysis)  |
| consultation_notes, reason_for_visit                     |
| checked_in_at, consultation_started_at, completed_at     |
| version (OPTIMISTIC LOCKING FOR DOUBLE-BOOKING)          |
+----------------------------------------------------------+
          | 1
          |
          *
+----------------------------------------------------------+
|                   WAITING_LIST_ENTRIES                   |
|----------------------------------------------------------|
| id (PK)                                                  |
| patient_id (FK -> users)                                 |
| doctor_id (FK -> doctor_profiles, NULLABLE)              |
| specialization (ENUM)                                    |
| priority_level (NORMAL=2, URGENT=1)                      |
| preferred_date, preferred_time_range                     |
| status (WAITING, OFFERED, ACCEPTED, DECLINED, EXPIRED)   |
| offered_date, offered_slot_index, offered_start_time     |
| offer_expires_at (TIMESTAMP, 15-MIN COUNTDOWN)           |
| created_at (TIMESTAMP, FCFS TIE-BREAKER)                 |
| version (LOCK)                                           |
+----------------------------------------------------------+
```

---

## 2. Table Specifications & Indexes

### Table: `users`
- `id` (BIGINT, PRIMARY KEY AUTO_INCREMENT)
- `email` (VARCHAR(255), NOT NULL UNIQUE)
- `password` (VARCHAR(255), NOT NULL, BCrypt Encrypted)
- `full_name` (VARCHAR(255), NOT NULL)
- `phone` (VARCHAR(50))
- `role` (VARCHAR(20), NOT NULL: `PATIENT`, `DOCTOR`, `ADMIN`)
- `active` (BOOLEAN, NOT NULL DEFAULT TRUE)
- `created_at` (TIMESTAMP, NOT NULL)

### Table: `doctor_profiles`
- `id` (BIGINT, PRIMARY KEY AUTO_INCREMENT)
- `user_id` (BIGINT, NOT NULL UNIQUE, FK to `users(id)`)
- `specialization` (VARCHAR(50), NOT NULL: 18 values)
- `qualifications` (VARCHAR(255), NOT NULL)
- `years_of_experience` (INT, NOT NULL)
- `consultation_fee` (DOUBLE, NOT NULL)
- `rating` (DOUBLE, DEFAULT 4.8)
- `bio` (VARCHAR(1000))
- `department` (VARCHAR(255))
- `room_number` (VARCHAR(50))
- `active` (BOOLEAN, NOT NULL DEFAULT TRUE)
- `work_days` (VARCHAR(255), DEFAULT 'MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY,SATURDAY')
- `work_start_time` (VARCHAR(10), DEFAULT '09:00')
- `work_end_time` (VARCHAR(10), DEFAULT '17:00')
- `slot_duration_minutes` (INT, DEFAULT 30)

### Table: `appointments`
- `id` (BIGINT, PRIMARY KEY AUTO_INCREMENT)
- `patient_id` (BIGINT, NOT NULL, FK to `users(id)`)
- `doctor_id` (BIGINT, NOT NULL, FK to `doctor_profiles(id)`)
- `appointment_date` (DATE, NOT NULL)
- `slot_index` (INT, NOT NULL: 0 to 15 in Segment Tree)
- `start_time` (VARCHAR(10), NOT NULL)
- `end_time` (VARCHAR(10), NOT NULL)
- `priority` (VARCHAR(20), NOT NULL: `NORMAL`, `URGENT`)
- `status` (VARCHAR(30), NOT NULL: `CONFIRMED`, `CHECKED_IN`, `IN_CONSULTATION`, `COMPLETED`, `CANCELLED`, `NO_SHOW`)
- `waiting_time_minutes` (DOUBLE, NOT NULL DEFAULT 0.0)
- `reason_for_visit` (VARCHAR(500))
- `consultation_notes` (VARCHAR(2000))
- `checked_in_at` (TIMESTAMP)
- `consultation_started_at` (TIMESTAMP)
- `consultation_completed_at` (TIMESTAMP)
- `created_at` (TIMESTAMP, NOT NULL)
- `version` (BIGINT, Optimistic Locking Token)
- **Indexes:**
  - `idx_doctor_date`: `(doctor_id, appointment_date)`
  - `idx_patient`: `(patient_id)`
  - `idx_status`: `(status)`

### Table: `waiting_list_entries`
- `id` (BIGINT, PRIMARY KEY AUTO_INCREMENT)
- `patient_id` (BIGINT, NOT NULL, FK to `users(id)`)
- `doctor_id` (BIGINT, NULLABLE, FK to `doctor_profiles(id)`)
- `specialization` (VARCHAR(50), NOT NULL)
- `priority_level` (VARCHAR(20), NOT NULL: `NORMAL`=2, `URGENT`=1)
- `preferred_date` (DATE, NOT NULL)
- `preferred_time_range` (VARCHAR(50), DEFAULT 'ANY')
- `status` (VARCHAR(30), NOT NULL: `WAITING`, `OFFERED`, `ACCEPTED`, `DECLINED`, `EXPIRED`, `CANCELLED`)
- `offered_date` (DATE)
- `offered_slot_index` (INT)
- `offered_start_time` (VARCHAR(10))
- `offered_end_time` (VARCHAR(10))
- `offer_expires_at` (TIMESTAMP, 15-minute countdown boundary)
- `created_at` (TIMESTAMP, NOT NULL, used as FCFS heap tie-breaker)
- `version` (BIGINT)
- **Indexes:**
  - `idx_waiting_status`: `(status)`
  - `idx_waiting_spec_date`: `(specialization, preferred_date)`
