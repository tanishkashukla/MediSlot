package com.medislot.controller;

import com.medislot.dto.AdminStatsDto;
import com.medislot.dto.AppointmentResponseDto;
import com.medislot.dto.DoctorDto;
import com.medislot.dto.WaitingListResponseDto;
import com.medislot.model.DoctorProfile;
import com.medislot.model.PatientProfile;
import com.medislot.model.Role;
import com.medislot.model.User;
import com.medislot.repository.DoctorProfileRepository;
import com.medislot.repository.PatientProfileRepository;
import com.medislot.repository.UserRepository;
import com.medislot.service.AdminAnalyticsService;
import com.medislot.service.AppointmentService;
import com.medislot.service.WaitingListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminAnalyticsService adminAnalyticsService;

    @Autowired
    private DoctorProfileRepository doctorProfileRepository;

    @Autowired
    private PatientProfileRepository patientProfileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private WaitingListService waitingListService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/stats")
    public ResponseEntity<AdminStatsDto> getHospitalStats() {
        return ResponseEntity.ok(adminAnalyticsService.getHospitalAnalytics());
    }

    @GetMapping("/doctors")
    public ResponseEntity<List<DoctorDto>> getAllDoctors() {
        List<DoctorProfile> list = doctorProfileRepository.findAll();
        return ResponseEntity.ok(list.stream().map(DoctorDto::fromEntity).toList());
    }

    @PostMapping("/doctors")
    public ResponseEntity<DoctorDto> createDoctor(@RequestBody Map<String, Object> req) {
        String email = ((String) req.get("email")).trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("User with this email already exists");
        }

        String rawPass = req.get("password") != null ? (String) req.get("password") : "Doctor@123";
        User user = new User(
                email,
                passwordEncoder.encode(rawPass),
                (String) req.get("fullName"),
                (String) req.get("phone"),
                Role.DOCTOR
        );
        user = userRepository.save(user);

        DoctorProfile doc = new DoctorProfile();
        doc.setUser(user);
        doc.setSpecialization(com.medislot.model.Specialization.valueOf((String) req.get("specialization")));
        doc.setQualifications((String) req.get("qualifications"));
        doc.setYearsOfExperience(req.get("yearsOfExperience") != null ? ((Number) req.get("yearsOfExperience")).intValue() : 5);
        doc.setConsultationFee(req.get("consultationFee") != null ? ((Number) req.get("consultationFee")).doubleValue() : 80.0);
        doc.setBio((String) req.get("bio"));
        doc.setDepartment((String) req.get("department"));
        doc.setRoomNumber((String) req.get("roomNumber"));
        doc.setActive(true);

        doc = doctorProfileRepository.save(doc);
        return ResponseEntity.ok(DoctorDto.fromEntity(doc));
    }

    @PutMapping("/doctors/{id}")
    public ResponseEntity<DoctorDto> updateDoctor(@PathVariable Long id, @RequestBody DoctorDto dto) {
        DoctorProfile doc = doctorProfileRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found: " + id));

        if (dto.getFullName() != null && doc.getUser() != null) {
            doc.getUser().setFullName(dto.getFullName());
            userRepository.save(doc.getUser());
        }
        if (dto.getSpecialization() != null) doc.setSpecialization(dto.getSpecialization());
        if (dto.getQualifications() != null) doc.setQualifications(dto.getQualifications());
        if (dto.getYearsOfExperience() > 0) doc.setYearsOfExperience(dto.getYearsOfExperience());
        if (dto.getConsultationFee() > 0) doc.setConsultationFee(dto.getConsultationFee());
        if (dto.getBio() != null) doc.setBio(dto.getBio());
        if (dto.getDepartment() != null) doc.setDepartment(dto.getDepartment());
        if (dto.getRoomNumber() != null) doc.setRoomNumber(dto.getRoomNumber());

        doc = doctorProfileRepository.save(doc);
        return ResponseEntity.ok(DoctorDto.fromEntity(doc));
    }

    @PatchMapping("/doctors/{id}/toggle-active")
    public ResponseEntity<DoctorDto> toggleDoctorActive(@PathVariable Long id) {
        DoctorProfile doc = doctorProfileRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found: " + id));
        doc.setActive(!doc.isActive());
        if (doc.getUser() != null) {
            doc.getUser().setActive(doc.isActive());
            userRepository.save(doc.getUser());
        }
        doc = doctorProfileRepository.save(doc);
        return ResponseEntity.ok(DoctorDto.fromEntity(doc));
    }

    @GetMapping("/patients")
    public ResponseEntity<List<PatientProfile>> getAllPatients() {
        return ResponseEntity.ok(patientProfileRepository.findAll());
    }

    @GetMapping("/appointments")
    public ResponseEntity<List<AppointmentResponseDto>> getAllAppointments() {
        return ResponseEntity.ok(appointmentService.getAllAppointments());
    }

    @GetMapping("/waiting-list")
    public ResponseEntity<List<WaitingListResponseDto>> getAllWaitingList() {
        return ResponseEntity.ok(waitingListService.getAllWaitingEntries());
    }
}
