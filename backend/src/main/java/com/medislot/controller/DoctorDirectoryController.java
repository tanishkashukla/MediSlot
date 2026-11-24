package com.medislot.controller;

import com.medislot.dto.DoctorDto;
import com.medislot.model.DoctorProfile;
import com.medislot.model.Specialization;
import com.medislot.repository.DoctorProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
public class DoctorDirectoryController {

    @Autowired
    private DoctorProfileRepository doctorProfileRepository;

    @GetMapping("/specializations")
    public ResponseEntity<List<Map<String, String>>> getSpecializations() {
        List<Map<String, String>> list = new ArrayList<>();
        for (Specialization s : Specialization.values()) {
            Map<String, String> item = new HashMap<>();
            item.put("key", s.name());
            item.put("name", s.getDisplayName());
            item.put("description", s.getDescription());
            list.add(item);
        }
        return ResponseEntity.ok(list);
    }

    @GetMapping("/doctors/directory")
    public ResponseEntity<List<DoctorDto>> getDoctorsDirectory(
            @RequestParam(required = false) Specialization specialization,
            @RequestParam(required = false) String search) {

        List<DoctorProfile> list = doctorProfileRepository.searchDoctors(
                specialization,
                (search != null && !search.trim().isEmpty()) ? search.trim() : null
        );
        return ResponseEntity.ok(list.stream().map(DoctorDto::fromEntity).toList());
    }

    @GetMapping("/doctors/{id}")
    public ResponseEntity<DoctorDto> getDoctorById(@PathVariable Long id) {
        DoctorProfile doc = doctorProfileRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found: " + id));
        return ResponseEntity.ok(DoctorDto.fromEntity(doc));
    }
}
