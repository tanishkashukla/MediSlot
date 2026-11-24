package com.medislot.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointments", indexes = {
    @Index(name = "idx_doctor_date", columnList = "doctor_id, appointment_date"),
    @Index(name = "idx_patient", columnList = "patient_id"),
    @Index(name = "idx_status", columnList = "status")
})
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_id", nullable = false)
    private DoctorProfile doctor;

    @Column(name = "appointment_date", nullable = false)
    private LocalDate appointmentDate;

    @Column(nullable = false)
    private String startTime; // e.g. "09:30"

    @Column(nullable = false)
    private String endTime;   // e.g. "10:00"

    @Column(nullable = false)
    private int slotIndex;    // index in the Segment Tree

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PriorityLevel priority = PriorityLevel.NORMAL;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status = AppointmentStatus.CONFIRMED;

    // Actual recorded waiting time (minutes from arrival/appointment to doctor consultation start)
    private double waitingTimeMinutes = 0.0;

    private String reasonForVisit;

    @Column(length = 2000)
    private String consultationNotes;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime checkedInAt;
    private LocalDateTime consultationStartedAt;
    private LocalDateTime consultationCompletedAt;

    @Version
    private Long version; // Optimistic locking prevents concurrent double-booking

    public Appointment() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getPatient() { return patient; }
    public void setPatient(User patient) { this.patient = patient; }

    public DoctorProfile getDoctor() { return doctor; }
    public void setDoctor(DoctorProfile doctor) { this.doctor = doctor; }

    public LocalDate getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(LocalDate appointmentDate) { this.appointmentDate = appointmentDate; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public int getSlotIndex() { return slotIndex; }
    public void setSlotIndex(int slotIndex) { this.slotIndex = slotIndex; }

    public PriorityLevel getPriority() { return priority; }
    public void setPriority(PriorityLevel priority) { this.priority = priority; }

    public AppointmentStatus getStatus() { return status; }
    public void setStatus(AppointmentStatus status) { this.status = status; }

    public double getWaitingTimeMinutes() { return waitingTimeMinutes; }
    public void setWaitingTimeMinutes(double waitingTimeMinutes) { this.waitingTimeMinutes = waitingTimeMinutes; }

    public String getReasonForVisit() { return reasonForVisit; }
    public void setReasonForVisit(String reasonForVisit) { this.reasonForVisit = reasonForVisit; }

    public String getConsultationNotes() { return consultationNotes; }
    public void setConsultationNotes(String consultationNotes) { this.consultationNotes = consultationNotes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getCheckedInAt() { return checkedInAt; }
    public void setCheckedInAt(LocalDateTime checkedInAt) { this.checkedInAt = checkedInAt; }

    public LocalDateTime getConsultationStartedAt() { return consultationStartedAt; }
    public void setConsultationStartedAt(LocalDateTime consultationStartedAt) { this.consultationStartedAt = consultationStartedAt; }

    public LocalDateTime getConsultationCompletedAt() { return consultationCompletedAt; }
    public void setConsultationCompletedAt(LocalDateTime consultationCompletedAt) { this.consultationCompletedAt = consultationCompletedAt; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
}
