package com.medislot.dto;

import com.medislot.model.Appointment;
import com.medislot.model.AppointmentStatus;
import com.medislot.model.PriorityLevel;
import com.medislot.model.Specialization;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AppointmentResponseDto {
    private Long id;
    private Long patientId;
    private String patientName;
    private String patientEmail;
    private String patientPhone;
    private Long doctorId;
    private String doctorName;
    private Specialization specialization;
    private String specializationDisplayName;
    private double consultationFee;
    private String roomNumber;
    private LocalDate appointmentDate;
    private String startTime;
    private String endTime;
    private int slotIndex;
    private PriorityLevel priority;
    private AppointmentStatus status;
    private double waitingTimeMinutes;
    private String reasonForVisit;
    private String consultationNotes;
    private LocalDateTime createdAt;
    private LocalDateTime checkedInAt;
    private LocalDateTime consultationStartedAt;
    private LocalDateTime consultationCompletedAt;

    public AppointmentResponseDto() {}

    public static AppointmentResponseDto fromEntity(Appointment a) {
        AppointmentResponseDto dto = new AppointmentResponseDto();
        dto.setId(a.getId());
        if (a.getPatient() != null) {
            dto.setPatientId(a.getPatient().getId());
            dto.setPatientName(a.getPatient().getFullName());
            dto.setPatientEmail(a.getPatient().getEmail());
            dto.setPatientPhone(a.getPatient().getPhone());
        }
        if (a.getDoctor() != null) {
            dto.setDoctorId(a.getDoctor().getId());
            if (a.getDoctor().getUser() != null) {
                dto.setDoctorName(a.getDoctor().getUser().getFullName());
            }
            dto.setSpecialization(a.getDoctor().getSpecialization());
            if (a.getDoctor().getSpecialization() != null) {
                dto.setSpecializationDisplayName(a.getDoctor().getSpecialization().getDisplayName());
            }
            dto.setConsultationFee(a.getDoctor().getConsultationFee());
            dto.setRoomNumber(a.getDoctor().getRoomNumber());
        }
        dto.setAppointmentDate(a.getAppointmentDate());
        dto.setStartTime(a.getStartTime());
        dto.setEndTime(a.getEndTime());
        dto.setSlotIndex(a.getSlotIndex());
        dto.setPriority(a.getPriority());
        dto.setStatus(a.getStatus());
        dto.setWaitingTimeMinutes(a.getWaitingTimeMinutes());
        dto.setReasonForVisit(a.getReasonForVisit());
        dto.setConsultationNotes(a.getConsultationNotes());
        dto.setCreatedAt(a.getCreatedAt());
        dto.setCheckedInAt(a.getCheckedInAt());
        dto.setConsultationStartedAt(a.getConsultationStartedAt());
        dto.setConsultationCompletedAt(a.getConsultationCompletedAt());
        return dto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public String getPatientEmail() { return patientEmail; }
    public void setPatientEmail(String patientEmail) { this.patientEmail = patientEmail; }

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

    public double getConsultationFee() { return consultationFee; }
    public void setConsultationFee(double consultationFee) { this.consultationFee = consultationFee; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

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
}
