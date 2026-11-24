package com.medislot.dto;

import com.medislot.model.DoctorProfile;
import com.medislot.model.Specialization;

public class DoctorDto {
    private Long id;
    private Long userId;
    private String fullName;
    private String email;
    private String phone;
    private Specialization specialization;
    private String specializationDisplayName;
    private String qualifications;
    private int yearsOfExperience;
    private double consultationFee;
    private double rating;
    private String bio;
    private String department;
    private String roomNumber;
    private boolean active;
    private String workDays;
    private String workStartTime;
    private String workEndTime;
    private int slotDurationMinutes;

    public DoctorDto() {}

    public static DoctorDto fromEntity(DoctorProfile profile) {
        DoctorDto dto = new DoctorDto();
        dto.setId(profile.getId());
        if (profile.getUser() != null) {
            dto.setUserId(profile.getUser().getId());
            dto.setFullName(profile.getUser().getFullName());
            dto.setEmail(profile.getUser().getEmail());
            dto.setPhone(profile.getUser().getPhone());
        }
        dto.setSpecialization(profile.getSpecialization());
        if (profile.getSpecialization() != null) {
            dto.setSpecializationDisplayName(profile.getSpecialization().getDisplayName());
        }
        dto.setQualifications(profile.getQualifications());
        dto.setYearsOfExperience(profile.getYearsOfExperience());
        dto.setConsultationFee(profile.getConsultationFee());
        dto.setRating(profile.getRating());
        dto.setBio(profile.getBio());
        dto.setDepartment(profile.getDepartment());
        dto.setRoomNumber(profile.getRoomNumber());
        dto.setActive(profile.isActive());
        dto.setWorkDays(profile.getWorkDays());
        dto.setWorkStartTime(profile.getWorkStartTime());
        dto.setWorkEndTime(profile.getWorkEndTime());
        dto.setSlotDurationMinutes(profile.getSlotDurationMinutes());
        return dto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Specialization getSpecialization() { return specialization; }
    public void setSpecialization(Specialization specialization) { this.specialization = specialization; }

    public String getSpecializationDisplayName() { return specializationDisplayName; }
    public void setSpecializationDisplayName(String specializationDisplayName) { this.specializationDisplayName = specializationDisplayName; }

    public String getQualifications() { return qualifications; }
    public void setQualifications(String qualifications) { this.qualifications = qualifications; }

    public int getYearsOfExperience() { return yearsOfExperience; }
    public void setYearsOfExperience(int yearsOfExperience) { this.yearsOfExperience = yearsOfExperience; }

    public double getConsultationFee() { return consultationFee; }
    public void setConsultationFee(double consultationFee) { this.consultationFee = consultationFee; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public String getWorkDays() { return workDays; }
    public void setWorkDays(String workDays) { this.workDays = workDays; }

    public String getWorkStartTime() { return workStartTime; }
    public void setWorkStartTime(String workStartTime) { this.workStartTime = workStartTime; }

    public String getWorkEndTime() { return workEndTime; }
    public void setWorkEndTime(String workEndTime) { this.workEndTime = workEndTime; }

    public int getSlotDurationMinutes() { return slotDurationMinutes; }
    public void setSlotDurationMinutes(int slotDurationMinutes) { this.slotDurationMinutes = slotDurationMinutes; }
}
