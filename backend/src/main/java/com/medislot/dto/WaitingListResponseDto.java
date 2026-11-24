package com.medislot.dto;

import com.medislot.model.PriorityLevel;
import com.medislot.model.Specialization;
import com.medislot.model.WaitingListEntry;
import com.medislot.model.WaitingListStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class WaitingListResponseDto {
    private Long id;
    private Long patientId;
    private String patientName;
    private String patientPhone;
    private Long doctorId;
    private String doctorName;
    private Specialization specialization;
    private String specializationDisplayName;
    private PriorityLevel priorityLevel;
    private LocalDate preferredDate;
    private String preferredTimeRange;
    private WaitingListStatus status;
    private int queuePosition; // calculated live queue position
    private LocalDate offeredDate;
    private Integer offeredSlotIndex;
    private String offeredStartTime;
    private String offeredEndTime;
    private LocalDateTime offerExpiresAt;
    private long secondsRemaining;
    private LocalDateTime createdAt;

    public WaitingListResponseDto() {}

    public static WaitingListResponseDto fromEntity(WaitingListEntry w, int queuePosition) {
        WaitingListResponseDto dto = new WaitingListResponseDto();
        dto.setId(w.getId());
        if (w.getPatient() != null) {
            dto.setPatientId(w.getPatient().getId());
            dto.setPatientName(w.getPatient().getFullName());
            dto.setPatientPhone(w.getPatient().getPhone());
        }
        if (w.getDoctor() != null) {
            dto.setDoctorId(w.getDoctor().getId());
            if (w.getDoctor().getUser() != null) {
                dto.setDoctorName(w.getDoctor().getUser().getFullName());
            }
        }
        dto.setSpecialization(w.getSpecialization());
        if (w.getSpecialization() != null) {
            dto.setSpecializationDisplayName(w.getSpecialization().getDisplayName());
        }
        dto.setPriorityLevel(w.getPriorityLevel());
        dto.setPreferredDate(w.getPreferredDate());
        dto.setPreferredTimeRange(w.getPreferredTimeRange());
        dto.setStatus(w.getStatus());
        dto.setQueuePosition(queuePosition);
        dto.setOfferedDate(w.getOfferedDate());
        dto.setOfferedSlotIndex(w.getOfferedSlotIndex());
        dto.setOfferedStartTime(w.getOfferedStartTime());
        dto.setOfferedEndTime(w.getOfferedEndTime());
        dto.setOfferExpiresAt(w.getOfferExpiresAt());
        if (w.getOfferExpiresAt() != null) {
            long remaining = java.time.Duration.between(LocalDateTime.now(), w.getOfferExpiresAt()).getSeconds();
            dto.setSecondsRemaining(Math.max(0, remaining));
        } else {
            dto.setSecondsRemaining(0);
        }
        dto.setCreatedAt(w.getCreatedAt());
        return dto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public String getPatientPhone() { return patientPhone; }
    public void setPatientPhone(String patientPhone) { this.patientPhone = patientPhone; }

    public Long getDoctorId() { return doctorId; }
    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }

    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }

    public Specialization getSpecialization() { return specialization; }
    public void setSpecialization(Specialization specialization) { this.specialization = specialization; }

    public String getSpecializationDisplayName() { return specializationDisplayName; }
    public void setSpecializationDisplayName(String specializationDisplayName) { this.specializationDisplayName = specializationDisplayName; }

    public PriorityLevel getPriorityLevel() { return priorityLevel; }
    public void setPriorityLevel(PriorityLevel priorityLevel) { this.priorityLevel = priorityLevel; }

    public LocalDate getPreferredDate() { return preferredDate; }
    public void setPreferredDate(LocalDate preferredDate) { this.preferredDate = preferredDate; }

    public String getPreferredTimeRange() { return preferredTimeRange; }
    public void setPreferredTimeRange(String preferredTimeRange) { this.preferredTimeRange = preferredTimeRange; }

    public WaitingListStatus getStatus() { return status; }
    public void setStatus(WaitingListStatus status) { this.status = status; }

    public int getQueuePosition() { return queuePosition; }
    public void setQueuePosition(int queuePosition) { this.queuePosition = queuePosition; }

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

    public long getSecondsRemaining() { return secondsRemaining; }
    public void setSecondsRemaining(long secondsRemaining) { this.secondsRemaining = secondsRemaining; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
