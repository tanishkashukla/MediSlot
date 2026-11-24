package com.medislot.controller;

import com.medislot.dto.*;
import com.medislot.model.DoctorProfile;
import com.medislot.model.User;
import com.medislot.repository.AppointmentRepository;
import com.medislot.repository.DoctorProfileRepository;
import com.medislot.repository.WaitingListRepository;
import com.medislot.service.AppointmentService;
import com.medislot.service.DoctorScheduleService;
import com.medislot.service.WaitingListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/doctor")
public class DoctorController {

    @Autowired
    private DoctorProfileRepository doctorProfileRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private DoctorScheduleService doctorScheduleService;

    @Autowired
    private WaitingListService waitingListService;

    private DoctorProfile getDoctorProfile(User user) {
        return doctorProfileRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("Doctor profile not found for user: " + user.getEmail()));
    }

    @GetMapping("/profile")
    public ResponseEntity<DoctorDto> getProfile(@AuthenticationPrincipal User user) {
        DoctorProfile doc = getDoctorProfile(user);
        return ResponseEntity.ok(DoctorDto.fromEntity(doc));
    }

    @PutMapping("/profile")
    public ResponseEntity<DoctorDto> updateProfile(
            @AuthenticationPrincipal User user,
            @RequestBody DoctorDto updated) {

        DoctorProfile doc = getDoctorProfile(user);
        if (updated.getBio() != null) doc.setBio(updated.getBio());
        if (updated.getQualifications() != null) doc.setQualifications(updated.getQualifications());
        if (updated.getConsultationFee() > 0) doc.setConsultationFee(updated.getConsultationFee());
        if (updated.getDepartment() != null) doc.setDepartment(updated.getDepartment());
        if (updated.getRoomNumber() != null) doc.setRoomNumber(updated.getRoomNumber());
        if (updated.getWorkDays() != null) doc.setWorkDays(updated.getWorkDays());
        if (updated.getWorkStartTime() != null) doc.setWorkStartTime(updated.getWorkStartTime());
        if (updated.getWorkEndTime() != null) doc.setWorkEndTime(updated.getWorkEndTime());
        if (updated.getSlotDurationMinutes() > 0) doc.setSlotDurationMinutes(updated.getSlotDurationMinutes());

        doc = doctorProfileRepository.save(doc);
        return ResponseEntity.ok(DoctorDto.fromEntity(doc));
    }

    @GetMapping("/appointments")
    public ResponseEntity<List<AppointmentResponseDto>> getAppointments(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        DoctorProfile doc = getDoctorProfile(user);
        if (date != null) {
            return ResponseEntity.ok(appointmentService.getDoctorDailySchedule(doc.getId(), date));
        }
        return ResponseEntity.ok(appointmentService.getDoctorAppointments(doc.getId()));
    }

    @GetMapping("/schedule")
    public ResponseEntity<SlotAvailabilityDto> getSchedule(
            @AuthenticationPrincipal User user,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        DoctorProfile doc = getDoctorProfile(user);
        return ResponseEntity.ok(doctorScheduleService.getSlotAvailability(doc.getId(), date));
    }

    @PutMapping("/schedule")
    public ResponseEntity<Void> updateSchedule(
            @AuthenticationPrincipal User user,
            @RequestBody DoctorScheduleUpdateDto updateDto) {

        DoctorProfile doc = getDoctorProfile(user);
        doctorScheduleService.updateScheduleAndBlockedSlots(doc.getId(), updateDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/schedule/slot/{slotIndex}/toggle-block")
    public ResponseEntity<Void> toggleBlockSlot(
            @AuthenticationPrincipal User user,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @PathVariable int slotIndex,
            @RequestParam boolean block) {

        DoctorProfile doc = getDoctorProfile(user);
        doctorScheduleService.toggleBlockSlot(doc.getId(), date, slotIndex, block);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/appointment/{id}/checkin")
    public ResponseEntity<AppointmentResponseDto> checkIn(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {

        return ResponseEntity.ok(appointmentService.checkInPatient(id, user.getId()));
    }

    @PostMapping("/appointment/{id}/start")
    public ResponseEntity<AppointmentResponseDto> startConsultation(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {

        return ResponseEntity.ok(appointmentService.startConsultation(id, user.getId()));
    }

    @PostMapping("/appointment/{id}/complete")
    public ResponseEntity<AppointmentResponseDto> completeConsultation(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body) {

        String notes = body != null ? body.get("notes") : "";
        return ResponseEntity.ok(appointmentService.completeConsultation(id, user.getId(), notes));
    }

    @PostMapping("/appointment/{id}/noshow")
    public ResponseEntity<AppointmentResponseDto> markNoShow(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {

        return ResponseEntity.ok(appointmentService.markNoShow(id, user.getId()));
    }

    @PostMapping("/appointment/{id}/cancel")
    public ResponseEntity<AppointmentResponseDto> cancelAppointment(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {

        return ResponseEntity.ok(appointmentService.cancelAppointment(id, user.getId(), "ROLE_DOCTOR"));
    }

    @GetMapping("/waiting-list")
    public ResponseEntity<List<WaitingListResponseDto>> getWaitingList(@AuthenticationPrincipal User user) {
        DoctorProfile doc = getDoctorProfile(user);
        List<WaitingListResponseDto> all = waitingListService.getAllWaitingEntries();
        List<WaitingListResponseDto> filtered = all.stream()
                .filter(w -> (w.getDoctorId() != null && w.getDoctorId().equals(doc.getId()))
                        || (w.getDoctorId() == null && w.getSpecialization() == doc.getSpecialization()))
                .toList();
        return ResponseEntity.ok(filtered);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats(@AuthenticationPrincipal User user) {
        DoctorProfile doc = getDoctorProfile(user);
        long completed = appointmentRepository.countCompletedByDoctor(doc.getId());
        List<AppointmentResponseDto> todayList = appointmentService.getDoctorDailySchedule(doc.getId(), LocalDate.now());

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCompletedConsultations", completed);
        stats.put("todayAppointmentsCount", todayList.size());
        stats.put("todayCheckedInCount", todayList.stream().filter(a -> a.getStatus().name().equals("CHECKED_IN")).count());
        stats.put("consultationFee", doc.getConsultationFee());
        stats.put("rating", doc.getRating());
        stats.put("department", doc.getDepartment());
        stats.put("roomNumber", doc.getRoomNumber());
        return ResponseEntity.ok(stats);
    }
}
