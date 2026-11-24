package com.medislot.dto;

import com.medislot.model.PriorityLevel;
import com.medislot.model.Specialization;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class WaitingListRequestDto {
    private Long doctorId; // Optional: specific doctor requested

    @NotNull(message = "Specialization is required")
    private Specialization specialization;

    @NotNull(message = "Preferred date is required")
    private LocalDate preferredDate;

    private String preferredTimeRange = "ANY"; // "MORNING", "AFTERNOON", "ANY"
    private PriorityLevel priorityLevel = PriorityLevel.NORMAL;

    public WaitingListRequestDto() {}

    public Long getDoctorId() { return doctorId; }
    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }

    public Specialization getSpecialization() { return specialization; }
    public void setSpecialization(Specialization specialization) { this.specialization = specialization; }

    public LocalDate getPreferredDate() { return preferredDate; }
    public void setPreferredDate(LocalDate preferredDate) { this.preferredDate = preferredDate; }

    public String getPreferredTimeRange() { return preferredTimeRange; }
    public void setPreferredTimeRange(String preferredTimeRange) { this.preferredTimeRange = preferredTimeRange; }

    public PriorityLevel getPriorityLevel() { return priorityLevel; }
    public void setPriorityLevel(PriorityLevel priorityLevel) { this.priorityLevel = priorityLevel; }
}
