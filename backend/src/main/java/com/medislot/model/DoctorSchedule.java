package com.medislot.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "doctor_schedules", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"doctor_id", "schedule_date"})
})
public class DoctorSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_id", nullable = false)
    private DoctorProfile doctor;

    @Column(name = "schedule_date", nullable = false)
    private LocalDate scheduleDate;

    private String startTime = "09:00";
    private String endTime = "17:00";
    private int slotDurationMinutes = 30;
    private int totalSlots = 16; // 8 hours * 2 = 16 slots

    // Blocked slots stored as comma-separated indices, e.g. "4,5"
    private String blockedSlots = "";

    @Version
    private Long version;

    public DoctorSchedule() {}

    public DoctorSchedule(DoctorProfile doctor, LocalDate scheduleDate) {
        this.doctor = doctor;
        this.scheduleDate = scheduleDate;
        this.startTime = doctor.getWorkStartTime() != null ? doctor.getWorkStartTime() : "09:00";
        this.endTime = doctor.getWorkEndTime() != null ? doctor.getWorkEndTime() : "17:00";
        this.slotDurationMinutes = doctor.getSlotDurationMinutes() > 0 ? doctor.getSlotDurationMinutes() : 30;
        this.totalSlots = calculateTotalSlots();
    }

    public int calculateTotalSlots() {
        try {
            String[] s = startTime.split(":");
            String[] e = endTime.split(":");
            int startMinutes = Integer.parseInt(s[0]) * 60 + Integer.parseInt(s[1]);
            int endMinutes = Integer.parseInt(e[0]) * 60 + Integer.parseInt(e[1]);
            int diff = endMinutes - startMinutes;
            return diff > 0 ? diff / slotDurationMinutes : 16;
        } catch (Exception ex) {
            return 16;
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public DoctorProfile getDoctor() { return doctor; }
    public void setDoctor(DoctorProfile doctor) { this.doctor = doctor; }

    public LocalDate getScheduleDate() { return scheduleDate; }
    public void setScheduleDate(LocalDate scheduleDate) { this.scheduleDate = scheduleDate; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public int getSlotDurationMinutes() { return slotDurationMinutes; }
    public void setSlotDurationMinutes(int slotDurationMinutes) { this.slotDurationMinutes = slotDurationMinutes; }

    public int getTotalSlots() { return totalSlots; }
    public void setTotalSlots(int totalSlots) { this.totalSlots = totalSlots; }

    public String getBlockedSlots() { return blockedSlots; }
    public void setBlockedSlots(String blockedSlots) { this.blockedSlots = blockedSlots; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
}
