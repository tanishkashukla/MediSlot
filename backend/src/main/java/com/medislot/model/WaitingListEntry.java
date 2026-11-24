package com.medislot.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "waiting_list_entries", indexes = {
    @Index(name = "idx_waiting_status", columnList = "status"),
    @Index(name = "idx_waiting_spec_date", columnList = "specialization, preferred_date")
})
public class WaitingListEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_id")
    private DoctorProfile doctor; // Optional: specific doctor requested

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Specialization specialization;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PriorityLevel priorityLevel = PriorityLevel.NORMAL;

    @Column(name = "preferred_date", nullable = false)
    private LocalDate preferredDate;

    // Time window preference: "MORNING" (09:00-12:00), "AFTERNOON" (12:00-17:00), "ANY"
    private String preferredTimeRange = "ANY";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WaitingListStatus status = WaitingListStatus.WAITING;

    // Offer details when a slot becomes available
    private LocalDate offeredDate;
    private Integer offeredSlotIndex;
    private String offeredStartTime;
    private String offeredEndTime;
    private LocalDateTime offerExpiresAt; // 15-minute window timestamp

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Version
    private Long version;

    public WaitingListEntry() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getPatient() { return patient; }
    public void setPatient(User patient) { this.patient = patient; }

    public DoctorProfile getDoctor() { return doctor; }
    public void setDoctor(DoctorProfile doctor) { this.doctor = doctor; }

    public Specialization getSpecialization() { return specialization; }
    public void setSpecialization(Specialization specialization) { this.specialization = specialization; }

    public PriorityLevel getPriorityLevel() { return priorityLevel; }
    public void setPriorityLevel(PriorityLevel priorityLevel) { this.priorityLevel = priorityLevel; }

    public LocalDate getPreferredDate() { return preferredDate; }
    public void setPreferredDate(LocalDate preferredDate) { this.preferredDate = preferredDate; }

    public String getPreferredTimeRange() { return preferredTimeRange; }
    public void setPreferredTimeRange(String preferredTimeRange) { this.preferredTimeRange = preferredTimeRange; }

    public WaitingListStatus getStatus() { return status; }
    public void setStatus(WaitingListStatus status) { this.status = status; }

    public LocalDate getOfferedDate() { return offeredDate; }
    public void setOfferedDate(LocalDate offeredDate) { this.offeredDate = offeredDate; }

    public Integer getOfferedSlotIndex() { return offeredSlotIndex; }
    public void setOfferedSlotIndex(Integer offeredSlotIndex) { this.offeredSlotIndex = offeredSlotIndex; }

    public String getOfferedStartTime() { return offeredStartTime; }
    public void setOfferedStartTime(String offeredStartTime) { this.offeredStartTime = offeredStartTime; }

    public String getOfferedEndTime() { return offeredEndTime; }
    public void setOfferedEndTime(String offeredEndTime) { this.offeredEndTime = offeredEndTime; }

    public LocalDateTime getOfferExpiresAt() { return offerExpiresAt; }
    public void setOfferExpiresAt(LocalDateTime offerExpiresAt) { this.offerExpiresAt = offerExpiresAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
}
