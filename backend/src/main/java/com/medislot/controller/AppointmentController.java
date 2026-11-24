package com.medislot.controller;

import com.medislot.dto.SlotAvailabilityDto;
import com.medislot.service.DoctorScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    @Autowired
    private DoctorScheduleService doctorScheduleService;

    /**
     * Queries Segment Tree for real-time doctor slot availability on a specific date.
     * Returns genuinely available slots and automatic suggestions if full or unavailable.
     */
    @GetMapping("/slots")
    public ResponseEntity<SlotAvailabilityDto> getAvailableSlots(
            @RequestParam Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        SlotAvailabilityDto availability = doctorScheduleService.getSlotAvailability(doctorId, date);
        return ResponseEntity.ok(availability);
    }
}
