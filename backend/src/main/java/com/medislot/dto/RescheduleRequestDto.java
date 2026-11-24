package com.medislot.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class RescheduleRequestDto {
    @NotNull(message = "New appointment date is required")
    private LocalDate newAppointmentDate;

    @NotNull(message = "New slot index is required")
    private Integer newSlotIndex;

    public RescheduleRequestDto() {}

    public LocalDate getNewAppointmentDate() { return newAppointmentDate; }
    public void setNewAppointmentDate(LocalDate newAppointmentDate) { this.newAppointmentDate = newAppointmentDate; }

    public Integer getNewSlotIndex() { return newSlotIndex; }
    public void setNewSlotIndex(Integer newSlotIndex) { this.newSlotIndex = newSlotIndex; }
}
