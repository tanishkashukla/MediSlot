package com.medislot.controller;

import com.medislot.dto.*;
import com.medislot.model.Appointment;
import com.medislot.model.PatientProfile;
import com.medislot.model.User;
import com.medislot.repository.PatientProfileRepository;
import com.medislot.repository.UserRepository;
import com.medislot.service.AppointmentService;
import com.medislot.service.WaitingListService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patient")
public class PatientController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private WaitingListService waitingListService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PatientProfileRepository patientProfileRepository;

    @GetMapping("/profile")
    public ResponseEntity<PatientProfile> getProfile(@AuthenticationPrincipal User user) {
        PatientProfile profile = patientProfileRepository.findByUser(user)
                .orElseGet(() -> {
                    PatientProfile p = new PatientProfile();
                    p.setUser(user);
                    return patientProfileRepository.save(p);
                });
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/profile")
    public ResponseEntity<PatientProfile> updateProfile(
            @AuthenticationPrincipal User user,
            @RequestBody PatientProfile updated) {

        PatientProfile profile = patientProfileRepository.findByUser(user)
                .orElseGet(() -> {
                    PatientProfile p = new PatientProfile();
                    p.setUser(user);
                    return p;
                });

        if (updated.getDateOfBirth() != null) profile.setDateOfBirth(updated.getDateOfBirth());
        if (updated.getGender() != null) profile.setGender(updated.getGender());
        if (updated.getBloodGroup() != null) profile.setBloodGroup(updated.getBloodGroup());
        if (updated.getEmergencyContact() != null) profile.setEmergencyContact(updated.getEmergencyContact());
        if (updated.getAddress() != null) profile.setAddress(updated.getAddress());
        if (updated.getMedicalHistory() != null) profile.setMedicalHistory(updated.getMedicalHistory());

        return ResponseEntity.ok(patientProfileRepository.save(profile));
    }

    @GetMapping("/appointments")
    public ResponseEntity<List<AppointmentResponseDto>> getAppointments(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(appointmentService.getPatientAppointments(user.getId()));
    }

    @PostMapping("/book")
    public ResponseEntity<AppointmentResponseDto> bookAppointment(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody AppointmentRequestDto request) {

        return ResponseEntity.ok(appointmentService.bookAppointment(user.getId(), request));
    }

    @PostMapping("/cancel/{id}")
    public ResponseEntity<AppointmentResponseDto> cancelAppointment(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {

        return ResponseEntity.ok(appointmentService.cancelAppointment(id, user.getId(), "ROLE_PATIENT"));
    }

    @PostMapping("/reschedule/{id}")
    public ResponseEntity<AppointmentResponseDto> rescheduleAppointment(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @Valid @RequestBody RescheduleRequestDto request) {

        return ResponseEntity.ok(appointmentService.rescheduleAppointment(id, user.getId(), request));
    }

    @GetMapping("/waiting-list")
    public ResponseEntity<List<WaitingListResponseDto>> getWaitingList(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(waitingListService.getPatientWaitingList(user.getId()));
    }

    @PostMapping("/waiting-list/join")
    public ResponseEntity<WaitingListResponseDto> joinWaitingList(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody WaitingListRequestDto request) {

        return ResponseEntity.ok(waitingListService.joinWaitingList(user.getId(), request));
    }

    @PostMapping("/waiting-list/offer/{id}/accept")
    public ResponseEntity<AppointmentResponseDto> acceptOffer(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {

        Appointment app = waitingListService.acceptOffer(user.getId(), id);
        return ResponseEntity.ok(AppointmentResponseDto.fromEntity(app));
    }

    @PostMapping("/waiting-list/offer/{id}/decline")
    public ResponseEntity<Void> declineOffer(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {

        waitingListService.declineOffer(user.getId(), id);
        return ResponseEntity.ok().build();
    }
}
