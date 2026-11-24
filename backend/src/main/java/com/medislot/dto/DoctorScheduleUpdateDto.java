package com.medislot.dto;

import java.time.LocalDate;
import java.util.List;

public class DoctorScheduleUpdateDto {
    private LocalDate scheduleDate;
    private String workDays;
    private String startTime;
    private String endTime;
    private int slotDurationMinutes;
    private List<Integer> blockedSlotIndices;

    public DoctorScheduleUpdateDto() {}

    public LocalDate getScheduleDate() { return scheduleDate; }
    public void setScheduleDate(LocalDate scheduleDate) { this.scheduleDate = scheduleDate; }

    public String getWorkDays() { return workDays; }
    public void setWorkDays(String workDays) { this.workDays = workDays; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public int getSlotDurationMinutes() { return slotDurationMinutes; }
    public void setSlotDurationMinutes(int slotDurationMinutes) { this.slotDurationMinutes = slotDurationMinutes; }

    public List<Integer> getBlockedSlotIndices() { return blockedSlotIndices; }
    public void setBlockedSlotIndices(List<Integer> blockedSlotIndices) { this.blockedSlotIndices = blockedSlotIndices; }
}
