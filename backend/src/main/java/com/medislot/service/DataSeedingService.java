package com.medislot.service;

import com.medislot.model.*;
import com.medislot.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Data Seeding Service (Requirement #8).
 * Automatically seeds:
 *   - 1 Admin account
 *   - 18 Medical Specializations
 *   - 36 Doctors (2 per specialization) with complete profiles & schedules
 *   - 250+ Patients with complete profiles
 *   - 10,000+ Historical Completed Appointments with realistic waiting times for Quickselect analysis
 *   - Rolling 30-day future bookings and available slots
 *   - Active waiting-list requests
 *   - Demo login credentials
 */
@Service
public class DataSeedingService implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeedingService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DoctorProfileRepository doctorProfileRepository;

    @Autowired
    private PatientProfileRepository patientProfileRepository;

    @Autowired
    private DoctorScheduleRepository doctorScheduleRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private WaitingListRepository waitingListRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Database already contains data. Skipping initial seeding.");
            return;
        }

        log.info("Starting MediSlot database seeding...");

        String defaultPass = passwordEncoder.encode("Password@123");
        String adminPass = passwordEncoder.encode("Admin@123");
        String doctorPass = passwordEncoder.encode("Doctor@123");
        String patientPass = passwordEncoder.encode("Patient@123");

        // 1. Seed Admin
        User admin = new User("admin@medislot.com", adminPass, "Hospital Super Administrator", "+1-555-0100", Role.ADMIN);
        userRepository.save(admin);
        log.info("Created Admin account: admin@medislot.com");

        // 2. Seed 36 Doctors across 18 Specializations (2 per specialization)
        Specialization[] specs = Specialization.values();
        List<DoctorProfile> doctorList = new ArrayList<>();

        String[][] doctorData = {
            // CARDIOLOGY
            {"Dr. Marcus Vance", "MD, DM (Cardiology), FACC", "18", "120", "4.9", "Senior Interventional Cardiologist with extensive expertise in coronary angioplasty and heart failure management.", "Cardiovascular Sciences", "Room 401"},
            {"Dr. Sarah Lin", "MBBS, MD (Cardiology), MRCP", "11", "95", "4.8", "Specialist in non-invasive clinical cardiology, echocardiography, and preventive cardiovascular wellness.", "Cardiovascular Sciences", "Room 402"},
            // DERMATOLOGY
            {"Dr. Elena Rostova", "MD (Dermatology), DNB", "14", "85", "4.9", "Consultant Dermatologist and cosmetic laser specialist treating autoimmune dermatoses and skin oncology.", "Dermatology & Cosmetology", "Room 205"},
            {"Dr. James Thorne", "MBBS, DVD, MD (Skin & VD)", "9", "70", "4.7", "General dermatologist specializing in pediatric eczema, psoriasis management, and acne therapy.", "Dermatology & Cosmetology", "Room 206"},
            // DENTISTRY
            {"Dr. David Sterling", "BDS, MDS (Oral & Maxillofacial)", "16", "90", "4.8", "Maxillofacial and reconstructive dental surgeon specialized in implantology and corrective jaw surgery.", "Dental Care Center", "Room 108"},
            {"Dr. Maya Patel", "BDS, MDS (Endodontics)", "8", "65", "4.8", "Endodontist with focus on microscopic root canal treatments and aesthetic restorative dentistry.", "Dental Care Center", "Room 109"},
            // NEUROLOGY
            {"Dr. Arthur Pendelton", "MD, DM (Neurology)", "22", "150", "4.9", "Distinguished Neurologist focusing on neurodegenerative diseases, Parkinson's, and stroke rehabilitation.", "Neurosciences Pavilion", "Room 501"},
            {"Dr. Clara Oswald", "MBBS, MD (Neurology), FAAN", "12", "110", "4.8", "Clinical neurologist specializing in epilepsy, neuromuscular disorders, and chronic headache syndromes.", "Neurosciences Pavilion", "Room 502"},
            // ORTHOPEDICS
            {"Dr. Robert Miller", "MS (Orthopedics), MCh", "19", "130", "4.9", "Chief orthopedic joint replacement surgeon performing computer-assisted knee and hip arthroplasty.", "Orthopedic Institute", "Room 310"},
            {"Dr. Ananya Roy", "MBBS, D.Ortho, DNB (Ortho)", "10", "85", "4.7", "Sports medicine specialist focused on arthroscopic shoulder and ligament reconstruction.", "Orthopedic Institute", "Room 311"},
            // PEDIATRICS
            {"Dr. Emily Watson", "MD (Pediatrics), DCH", "15", "80", "4.9", "Senior Pediatrician devoted to neonatal intensive care, developmental milestones, and child immunization.", "Children's Health Center", "Room 115"},
            {"Dr. Kevin Zhang", "MBBS, MD (Pediatrics)", "7", "60", "4.8", "Pediatric care physician focused on infectious pediatric ailments and adolescent medicine.", "Children's Health Center", "Room 116"},
            // GYNECOLOGY
            {"Dr. Priya Sharma", "MD, MS (Obstetrics & Gynecology)", "17", "115", "4.9", "Senior Obstetrician and Gynecologist specializing in high-risk pregnancy and laparoscopic surgeries.", "Women's Health Pavilion", "Room 218"},
            {"Dr. Hannah Abbott", "MBBS, DGO, DNB (OBGYN)", "9", "75", "4.7", "Gynecologist with expertise in adolescent gynaecology, fertility preservation, and hormonal therapies.", "Women's Health Pavilion", "Room 219"},
            // ENT
            {"Dr. Julian Bashir", "MS (ENT), DLO, FRCS", "15", "90", "4.8", "Otolaryngologist skilled in endoscopic sinus surgery, cochlear implants, and head-neck surgery.", "ENT Care Wing", "Room 225"},
            {"Dr. Leila Hosseini", "MBBS, MS (Otorhinolaryngology)", "8", "65", "4.7", "ENT specialist focusing on pediatric airway disorders, sleep apnea, and allergy rhinitis.", "ENT Care Wing", "Room 226"},
            // OPHTHALMOLOGY
            {"Dr. Gabriel Cruz", "MS (Ophthalmology), FICO", "16", "95", "4.9", "Senior cataract and refractive surgeon performing stitch-less phacoemulsification and LASIK.", "Ophthalmic Care Institute", "Room 305"},
            {"Dr. Sofia Chen", "MBBS, DO, DNB (Ophthalmology)", "11", "75", "4.8", "Retinal disease and glaucoma specialist managing diabetic retinopathy and pediatric ocular health.", "Ophthalmic Care Institute", "Room 306"},
            // PSYCHIATRY
            {"Dr. Jonathan Crane", "MD (Psychiatry), MRCPsych", "20", "140", "4.8", "Consultant Neuropsychiatrist specialized in adult affective disorders, anxiety, and PTSD.", "Behavioral Health Wing", "Room 601"},
            {"Dr. Rachel Amber", "MBBS, DPM, MD (Psychiatry)", "9", "85", "4.8", "Child, adolescent, and family psychiatrist specializing in cognitive behavioral interventions.", "Behavioral Health Wing", "Room 602"},
            // GENERAL MEDICINE
            {"Dr. Thomas Drake", "MD (Internal Medicine)", "21", "75", "4.9", "Senior Internal Medicine Consultant offering holistic management of complex multisystem disorders.", "Internal Medicine Department", "Room 101"},
            {"Dr. Nadia Foley", "MBBS, DNB (General Medicine)", "10", "55", "4.8", "Primary care physician focusing on metabolic syndrome, hypertension, and preventive health screenings.", "Internal Medicine Department", "Room 102"},
            // UROLOGY
            {"Dr. Kenneth Harris", "MS (Gen Surg), MCh (Urology)", "18", "125", "4.9", "Consultant Urologist and Andrologist with expertise in laser endourology and kidney stone surgery.", "Urology & Nephrology Center", "Room 415"},
            {"Dr. Vikram Rao", "MBBS, DNB (Urology)", "8", "85", "4.7", "Reconstructive urologist managing urinary incontinence, benign prostatic hyperplasia, and male fertility.", "Urology & Nephrology Center", "Room 416"},
            // GASTROENTEROLOGY
            {"Dr. Laura Croft", "MD, DM (Gastroenterology)", "16", "120", "4.9", "Consultant Gastroenterologist specializing in therapeutic endoscopy, ERCP, and inflammatory bowel disease.", "Digestive Diseases Clinic", "Room 320"},
            {"Dr. Omar Farooq", "MBBS, MD, DNB (Gastro)", "10", "90", "4.8", "Hepatologist and clinical gastroenterologist managing fatty liver, viral hepatitis, and peptic ulcers.", "Digestive Diseases Clinic", "Room 321"},
            // PULMONOLOGY
            {"Dr. Anthony Bennett", "MD, DTCD, FCCP (USA)", "17", "110", "4.9", "Senior Pulmonologist and critical care consultant treating COPD, asthma, and interstitial lung diseases.", "Respiratory Medicine Center", "Room 425"},
            {"Dr. Fiona Gallagher", "MBBS, DNB (Pulmonology)", "9", "75", "4.7", "Respiratory specialist treating sleep disorders, chronic cough, and occupational lung ailments.", "Respiratory Medicine Center", "Room 426"},
            // ENDOCRINOLOGY
            {"Dr. Gregory House", "MD, DM (Endocrinology)", "24", "160", "4.9", "Leading Clinical Endocrinologist specializing in complex thyroid disorders, pituitary tumors, and diabetes.", "Endocrine & Diabetes Care", "Room 510"},
            {"Dr. Lisa Cuddy", "MBBS, MD (Endocrinology)", "13", "100", "4.8", "Diabetologist and hormone specialist managing gestational diabetes, PCOS, and metabolic health.", "Endocrine & Diabetes Care", "Room 511"},
            // ONCOLOGY
            {"Dr. Allison Cameron", "MD, DM (Medical Oncology)", "15", "145", "4.9", "Medical Oncologist focusing on targeted molecular therapies, immunotherapy, and breast cancer.", "Comprehensive Cancer Center", "Room 701"},
            {"Dr. Robert Chase", "MS, MCh (Surgical Oncology)", "12", "130", "4.8", "Surgical oncologist specializing in gastrointestinal, thoracic, and head-and-neck tumor resections.", "Comprehensive Cancer Center", "Room 702"},
            // RADIOLOGY
            {"Dr. Eric Foreman", "MD (Radio-Diagnosis), DNB", "14", "95", "4.8", "Consultant Diagnostic Radiologist with sub-specialty in musculoskeletal MRI and neuro-imaging.", "Diagnostic Imaging Wing", "Room 130"},
            {"Dr. Remy Hadley", "MBBS, DMRD, DNB (Radiology)", "10", "80", "4.8", "Interventional and diagnostic radiologist focused on women's imaging and CT-guided procedures.", "Diagnostic Imaging Wing", "Room 131"},
            // NEPHROLOGY
            {"Dr. Neil Melendez", "MD, DM (Nephrology)", "18", "135", "4.9", "Chief Nephrologist leading the hemodialysis center, acute kidney injury protocols, and renal transplant.", "Renal Sciences Unit", "Room 430"},
            {"Dr. Audrey Lim", "MBBS, MD (Nephrology)", "11", "95", "4.8", "Clinical nephrologist focused on chronic kidney disease progression, glomerulonephritis, and dialysis care.", "Renal Sciences Unit", "Room 431"}
        };

        for (int i = 0; i < doctorData.length; i++) {
            Specialization spec = specs[i / 2];
            String[] d = doctorData[i];
            String email = (i == 0) ? "doctor.cardio@medislot.com" :
                    "doctor." + d[0].toLowerCase().replace("dr. ", "").replace(" ", ".") + "@medislot.com";

            User docUser = new User(email, doctorPass, d[0], "+1-555-02" + String.format("%02d", i), Role.DOCTOR);
            docUser = userRepository.save(docUser);

            DoctorProfile profile = new DoctorProfile();
            profile.setUser(docUser);
            profile.setSpecialization(spec);
            profile.setQualifications(d[1]);
            profile.setYearsOfExperience(Integer.parseInt(d[2]));
            profile.setConsultationFee(Double.parseDouble(d[3]));
            profile.setRating(Double.parseDouble(d[4]));
            profile.setBio(d[5]);
            profile.setDepartment(d[6]);
            profile.setRoomNumber(d[7]);
            profile.setActive(true);
            profile.setWorkDays("MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY,SATURDAY");
            profile.setWorkStartTime("09:00");
            profile.setWorkEndTime("17:00");
            profile.setSlotDurationMinutes(30);

            profile = doctorProfileRepository.save(profile);
            doctorList.add(profile);
        }
        log.info("Created 36 Doctors across 18 specializations.");

        // 3. Seed 255 Patients
        String[] firstNames = {"John", "Emma", "Michael", "Sophia", "William", "Olivia", "James", "Ava", "Alexander", "Isabella",
                "Daniel", "Mia", "Matthew", "Charlotte", "Joseph", "Amelia", "David", "Harper", "Andrew", "Evelyn",
                "Lucas", "Abigail", "Benjamin", "Emily", "Samuel", "Elizabeth", "Henry", "Sofia", "Sebastian", "Avery",
                "Jack", "Ella", "Owen", "Scarlett", "Gabriel", "Grace", "Carter", "Chloe", "Jayden", "Victoria",
                "Julian", "Riley", "Levi", "Aria", "Luke", "Lily", "Isaac", "Aubrey", "Anthony", "Zoey", "Ryan", "Hannah"};

        String[] lastNames = {"Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis", "Rodriguez", "Martinez",
                "Hernandez", "Lopez", "Gonzalez", "Wilson", "Anderson", "Thomas", "Taylor", "Moore", "Jackson", "Martin",
                "Lee", "Perez", "Thompson", "White", "Harris", "Sanchez", "Clark", "Ramirez", "Lewis", "Robinson",
                "Walker", "Young", "Allen", "King", "Wright", "Scott", "Torres", "Nguyen", "Hill", "Flores",
                "Green", "Adams", "Nelson", "Baker", "Hall", "Rivera", "Campbell", "Mitchell", "Carter", "Roberts"};

        String[] bloodGroups = {"A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-"};
        String[] genders = {"Male", "Female", "Other"};

        Random rand = new Random(42);
        List<User> patientList = new ArrayList<>();

        // Demo Patient
        User demoPatient = new User("patient.john@medislot.com", patientPass, "John Doe", "+1-555-0301", Role.PATIENT);
        demoPatient = userRepository.save(demoPatient);
        PatientProfile demoProf = new PatientProfile();
        demoProf.setUser(demoPatient);
        demoProf.setDateOfBirth(LocalDate.of(1992, 5, 14));
        demoProf.setGender("Male");
        demoProf.setBloodGroup("O+");
        demoProf.setEmergencyContact("+1-555-0999");
        demoProf.setAddress("742 Evergreen Terrace, Springfield");
        demoProf.setMedicalHistory("Mild seasonal allergies; no prior major surgeries.");
        patientProfileRepository.save(demoProf);
        patientList.add(demoPatient);

        for (int p = 1; p < 255; p++) {
            String fname = firstNames[p % firstNames.length];
            String lname = lastNames[(p * 3) % lastNames.length];
            String email = "patient." + fname.toLowerCase() + "." + lname.toLowerCase() + p + "@example.com";
            String phone = "+1-555-" + String.format("%04d", 1000 + p);

            User patient = new User(email, defaultPass, fname + " " + lname, phone, Role.PATIENT);
            patient = userRepository.save(patient);

            PatientProfile prof = new PatientProfile();
            prof.setUser(patient);
            prof.setDateOfBirth(LocalDate.of(1960 + rand.nextInt(45), 1 + rand.nextInt(12), 1 + rand.nextInt(28)));
            prof.setGender(genders[rand.nextInt(genders.length)]);
            prof.setBloodGroup(bloodGroups[rand.nextInt(bloodGroups.length)]);
            prof.setEmergencyContact("+1-555-" + String.format("%04d", 5000 + p));
            prof.setAddress(rand.nextInt(900) + " Medical Center Blvd, Suite " + (100 + rand.nextInt(400)));
            prof.setMedicalHistory(rand.nextDouble() < 0.35 ? "Hypertension managed with medication." : "No significant past medical history.");
            patientProfileRepository.save(prof);
            patientList.add(patient);
        }
        log.info("Created 255 registered patients.");

        // 4. Seed 10,000+ Completed Historical Appointments with realistic waiting times (Requirement #8)
        log.info("Generating 10,000 historical completed appointments for statistical analysis & Quickselect benchmarking...");
        List<Appointment> historicalBatch = new ArrayList<>(10050);

        String[] visitReasons = {
            "Routine health checkup and preventative screening",
            "Follow-up consultation after diagnostic lab tests",
            "Recurrent mild chest discomfort upon exertion",
            "Chronic lower back pain worsening over past month",
            "Persistent dry cough and mild dyspnea",
            "Skin rash evaluation and biopsy consultation",
            "Pre-operative anesthetic clearance assessment",
            "Medication dosage adjustment and blood pressure review",
            "Migraine headache management and prophylactic therapy",
            "Joint swelling and morning stiffness in knee"
        };

        LocalDate historyStartDate = LocalDate.now().minusDays(180);

        for (int i = 0; i < 10050; i++) {
            DoctorProfile doc = doctorList.get(i % doctorList.size());
            User patient = patientList.get(i % patientList.size());

            // Scatter over past 180 days
            int dayOffset = rand.nextInt(180);
            LocalDate appDate = historyStartDate.plusDays(dayOffset);

            int slotIdx = rand.nextInt(16);
            LocalTime startT = LocalTime.of(9, 0).plusMinutes((long) slotIdx * 30);
            LocalTime endT = startT.plusMinutes(30);

            // Realistic patient waiting time distribution:
            // Centered around 25-35 minutes, with normal distribution + long tail
            double waitTime;
            double u1 = rand.nextDouble();
            if (u1 < 0.60) {
                // Typical wait time: 15 to 35 mins
                waitTime = 15.0 + rand.nextInt(21) + Math.round(rand.nextDouble() * 10.0) / 10.0;
            } else if (u1 < 0.85) {
                // Moderate delay: 35 to 55 mins
                waitTime = 35.0 + rand.nextInt(21) + Math.round(rand.nextDouble() * 10.0) / 10.0;
            } else if (u1 < 0.95) {
                // Peak delay: 55 to 75 mins
                waitTime = 55.0 + rand.nextInt(21) + Math.round(rand.nextDouble() * 10.0) / 10.0;
            } else {
                // Long tail (95th+ percentile): 75 to 95 mins
                waitTime = 75.0 + rand.nextInt(21) + Math.round(rand.nextDouble() * 10.0) / 10.0;
            }

            Appointment app = new Appointment();
            app.setDoctor(doc);
            app.setPatient(patient);
            app.setAppointmentDate(appDate);
            app.setSlotIndex(slotIdx);
            app.setStartTime(startT.format(TIME_FMT));
            app.setEndTime(endT.format(TIME_FMT));
            app.setPriority(rand.nextDouble() < 0.20 ? PriorityLevel.URGENT : PriorityLevel.NORMAL);
            app.setStatus(AppointmentStatus.COMPLETED);
            app.setWaitingTimeMinutes(waitTime);
            app.setReasonForVisit(visitReasons[rand.nextInt(visitReasons.length)]);
            app.setConsultationNotes("Patient examined. Vital signs within acceptable clinical range. Prescribed standard course and scheduled routine 3-month review.");
            app.setCreatedAt(LocalDateTime.of(appDate.minusDays(rand.nextInt(7) + 1), LocalTime.of(10, 0)));
            app.setCheckedInAt(LocalDateTime.of(appDate, startT.minusMinutes(5)));
            app.setConsultationStartedAt(LocalDateTime.of(appDate, startT.plusMinutes((long) waitTime)));
            app.setConsultationCompletedAt(LocalDateTime.of(appDate, startT.plusMinutes((long) waitTime + 20)));

            historicalBatch.add(app);

            // Batch save every 2000 records for performance
            if (historicalBatch.size() >= 2000) {
                appointmentRepository.saveAll(historicalBatch);
                historicalBatch.clear();
            }
        }
        if (!historicalBatch.isEmpty()) {
            appointmentRepository.saveAll(historicalBatch);
            historicalBatch.clear();
        }
        log.info("Completed seeding 10,050 historical appointments.");

        // 5. Seed Rolling 30-Day Future Bookings and Active Appointments
        log.info("Seeding rolling 30-day future bookings and doctor schedules...");
        List<Appointment> futureBookings = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (int day = 0; day < 30; day++) {
            LocalDate bookDate = today.plusDays(day);

            for (DoctorProfile doc : doctorList) {
                // Pre-book 4 to 7 slots per doctor per day
                int bookedCount = 4 + rand.nextInt(4);
                Set<Integer> chosenSlots = new HashSet<>();
                while (chosenSlots.size() < bookedCount) {
                    chosenSlots.add(rand.nextInt(16));
                }

                List<Integer> sortedSlots = new ArrayList<>(chosenSlots);
                Collections.sort(sortedSlots);

                for (int sIdx = 0; sIdx < sortedSlots.size(); sIdx++) {
                    int slot = sortedSlots.get(sIdx);
                    LocalTime startT = LocalTime.of(9, 0).plusMinutes((long) slot * 30);
                    LocalTime endT = startT.plusMinutes(30);

                    Appointment app = new Appointment();
                    app.setDoctor(doc);
                    app.setPatient(patientList.get(rand.nextInt(patientList.size())));
                    app.setAppointmentDate(bookDate);
                    app.setSlotIndex(slot);
                    app.setStartTime(startT.format(TIME_FMT));
                    app.setEndTime(endT.format(TIME_FMT));
                    app.setPriority(rand.nextDouble() < 0.25 ? PriorityLevel.URGENT : PriorityLevel.NORMAL);

                    if (day == 0) {
                        // For TODAY, provide active variety of statuses so doctor can demo all workflows:
                        if (sIdx == 0) {
                            app.setStatus(AppointmentStatus.COMPLETED);
                            app.setWaitingTimeMinutes(14.0 + rand.nextInt(10));
                            app.setConsultationNotes("Patient vital signs normal. Heart rate 68 bpm. Scheduled for routine annual follow-up.");
                        } else if (sIdx == 1) {
                            app.setStatus(AppointmentStatus.IN_CONSULTATION);
                        } else if (sIdx == 2) {
                            app.setStatus(AppointmentStatus.CHECKED_IN);
                        } else {
                            app.setStatus(AppointmentStatus.CONFIRMED);
                        }
                    } else {
                        app.setStatus(AppointmentStatus.CONFIRMED);
                    }

                    app.setReasonForVisit(visitReasons[rand.nextInt(visitReasons.length)]);
                    app.setCreatedAt(LocalDateTime.now().minusHours(rand.nextInt(48) + 1));

                    futureBookings.add(app);
                }
            }
        }
        appointmentRepository.saveAll(futureBookings);
        log.info("Created {} future active appointments across the next 30 days.", futureBookings.size());

        // Also give demoPatient some active and past appointments
        Appointment demoToday = new Appointment();
        demoToday.setPatient(demoPatient);
        demoToday.setDoctor(doctorList.get(0)); // Dr. Marcus Vance
        demoToday.setAppointmentDate(today);
        demoToday.setSlotIndex(4); // 11:00 - 11:30
        demoToday.setStartTime("11:00");
        demoToday.setEndTime("11:30");
        demoToday.setPriority(PriorityLevel.URGENT);
        demoToday.setStatus(AppointmentStatus.CHECKED_IN);
        demoToday.setReasonForVisit("Chest tightness and high BP follow-up");
        demoToday.setCreatedAt(LocalDateTime.now().minusHours(3));
        demoToday.setCheckedInAt(LocalDateTime.now().minusMinutes(25));
        appointmentRepository.save(demoToday);

        Appointment demoUpcoming = new Appointment();
        demoUpcoming.setPatient(demoPatient);
        demoUpcoming.setDoctor(doctorList.get(0)); // Dr. Marcus Vance
        demoUpcoming.setAppointmentDate(today.plusDays(2));
        demoUpcoming.setSlotIndex(2); // 10:00 - 10:30
        demoUpcoming.setStartTime("10:00");
        demoUpcoming.setEndTime("10:30");
        demoUpcoming.setPriority(PriorityLevel.URGENT);
        demoUpcoming.setStatus(AppointmentStatus.CONFIRMED);
        demoUpcoming.setReasonForVisit("Annual cardiovascular evaluation and stress test");
        demoUpcoming.setCreatedAt(LocalDateTime.now().minusDays(1));
        appointmentRepository.save(demoUpcoming);

        // 6. Seed Waiting-List Requests
        List<WaitingListEntry> waitingList = new ArrayList<>();
        for (int w = 0; w < 30; w++) {
            WaitingListEntry we = new WaitingListEntry();
            we.setPatient(patientList.get(10 + w));
            we.setDoctor(w % 2 == 0 ? doctorList.get(w % doctorList.size()) : null);
            we.setSpecialization(specs[w % specs.length]);
            we.setPriorityLevel(w % 3 == 0 ? PriorityLevel.URGENT : PriorityLevel.NORMAL);
            we.setPreferredDate(today.plusDays(1 + (w % 5)));
            we.setPreferredTimeRange(w % 2 == 0 ? "MORNING" : "AFTERNOON");
            we.setStatus(WaitingListStatus.WAITING);
            we.setCreatedAt(LocalDateTime.now().minusHours(35 - w));
            waitingList.add(we);
        }

        // Add an OFFERED slot with active 15-minute countdown for demo patient
        WaitingListEntry demoOffer = new WaitingListEntry();
        demoOffer.setPatient(demoPatient);
        demoOffer.setDoctor(doctorList.get(2)); // Dr. Elena Rostova (Dermatology)
        demoOffer.setSpecialization(Specialization.DERMATOLOGY);
        demoOffer.setPriorityLevel(PriorityLevel.URGENT);
        demoOffer.setPreferredDate(today.plusDays(1));
        demoOffer.setPreferredTimeRange("MORNING");
        demoOffer.setStatus(WaitingListStatus.OFFERED);
        demoOffer.setOfferedDate(today.plusDays(1));
        demoOffer.setOfferedSlotIndex(1);
        demoOffer.setOfferedStartTime("09:30");
        demoOffer.setOfferedEndTime("10:00");
        demoOffer.setOfferExpiresAt(LocalDateTime.now().plusMinutes(12)); // 12 minutes remaining
        demoOffer.setCreatedAt(LocalDateTime.now().minusHours(3));
        waitingList.add(demoOffer);

        waitingListRepository.saveAll(waitingList);
        log.info("Created {} waiting list requests including active offer for demo patient.", waitingList.size());

        log.info("===================================================================");
        log.info("MediSlot Database Seeding Completed Successfully!");
        log.info("DEMO CREDENTIALS:");
        log.info("  Admin:   admin@medislot.com / Admin@123");
        log.info("  Doctor:  doctor.cardio@medislot.com / Doctor@123");
        log.info("  Patient: patient.john@medislot.com / Patient@123");
        log.info("===================================================================");
    }
}
