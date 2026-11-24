package com.medislot.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SlotAvailabilityDto {

    public static class SlotItem {
        private int slotIndex;
        private String startTime;
        private String endTime;
        private boolean available;
        private String reason; // AVAILABLE, BOOKED, BLOCKED, PAST

        public SlotItem() {}

        public SlotItem(int slotIndex, String startTime, String endTime, boolean available, String reason) {
            this.slotIndex = slotIndex;
            this.startTime = startTime;
            this.endTime = endTime;
            this.available = available;
            this.reason = reason;
        }

        public int getSlotIndex() { return slotIndex; }
        public void setSlotIndex(int slotIndex) { this.slotIndex = slotIndex; }

        public String getStartTime() { return startTime; }
        public void setStartTime(String startTime) { this.startTime = startTime; }

        public String getEndTime() { return endTime; }
        public void setEndTime(String endTime) { this.endTime = endTime; }

        public boolean isAvailable() { return available; }
        public void setAvailable(boolean available) { this.available = available; }

        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }

    public static class AlternativeSuggestion {
        private String type; // SAME_DOCTOR_NEXT_SLOT, OTHER_DOCTOR_SAME_SPEC, ALTERNATIVE_DATE
        private String message;
        private Long doctorId;
        private String doctorName;
        private LocalDate date;
        private int slotIndex;
        private String startTime;
        private String endTime;

        public AlternativeSuggestion() {}

        public AlternativeSuggestion(String type, String message, Long doctorId, String doctorName,
                                     LocalDate date, int slotIndex, String startTime, String endTime) {
            this.type = type;
            this.message = message;
            this.doctorId = doctorId;
            this.doctorName = doctorName;
            this.date = date;
            this.slotIndex = slotIndex;
            this.startTime = startTime;
            this.endTime = endTime;
        }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public Long getDoctorId() { return doctorId; }
        public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }

        public String getDoctorName() { return doctorName; }
        public void setDoctorName(String doctorName) { this.doctorName = doctorName; }

        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }

        public int getSlotIndex() { return slotIndex; }
        public void setSlotIndex(int slotIndex) { this.slotIndex = slotIndex; }

        public String getStartTime() { return startTime; }
        public void setStartTime(String startTime) { this.startTime = startTime; }

        public String getEndTime() { return endTime; }
        public void setEndTime(String endTime) { this.endTime = endTime; }
    }

    private Long doctorId;
    private String doctorName;
    private String specialization;
    private LocalDate scheduleDate;
    private int totalSlots;
    private int availableCount;
    private List<SlotItem> slots = new ArrayList<>();
    private List<AlternativeSuggestion> suggestions = new ArrayList<>();

    public SlotAvailabilityDto() {}

    public Long getDoctorId() { return doctorId; }
    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }

    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public LocalDate getScheduleDate() { return scheduleDate; }
    public void setScheduleDate(LocalDate scheduleDate) { this.scheduleDate = scheduleDate; }

    public int getTotalSlots() { return totalSlots; }
    public void setTotalSlots(int totalSlots) { this.totalSlots = totalSlots; }

    public int getAvailableCount() { return availableCount; }
    public void setAvailableCount(int availableCount) { this.availableCount = availableCount; }

    public List<SlotItem> getSlots() { return slots; }
    public void setSlots(List<SlotItem> slots) { this.slots = slots; }

    public List<AlternativeSuggestion> getSuggestions() { return suggestions; }
    public void setSuggestions(List<AlternativeSuggestion> suggestions) { this.suggestions = suggestions; }
}
