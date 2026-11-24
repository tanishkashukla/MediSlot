package com.medislot.service;

import com.medislot.algorithm.SegmentTree;
import com.medislot.dto.DoctorScheduleUpdateDto;
import com.medislot.dto.SlotAvailabilityDto;
import com.medislot.model.Appointment;
import com.medislot.model.AppointmentStatus;
import com.medislot.model.DoctorProfile;
import com.medislot.model.DoctorSchedule;
import com.medislot.repository.AppointmentRepository;
import com.medislot.repository.DoctorProfileRepository;
import com.medislot.repository.DoctorScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service managing doctor schedules and slot availability powered by Segment Tree (Unit 3).
 */
@Service
public class DoctorScheduleService {

    @Autowired
    private DoctorProfileRepository doctorProfileRepository;

    @Autowired
    private DoctorScheduleRepository doctorScheduleRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    // In-memory cache of Segment Trees keyed by "doctorId_date"
    private final Map<String, SegmentTree> segmentTreeCache = new ConcurrentHashMap<>();

    private String getCacheKey(Long doctorId, LocalDate date) {
        return doctorId + "_" + date.toString();
    }

    /**
     * Builds or retrieves the Segment Tree for a doctor on a specific date.
     */
    public SegmentTree getOrBuildSegmentTree(DoctorProfile doctor, LocalDate date) {
        String key = getCacheKey(doctor.getId(), date);
        return segmentTreeCache.compute(key, (k, existing) -> buildSegmentTreeFromDb(doctor, date));
    }

    /**
     * Evicts or updates the Segment Tree in cache when bookings or blockings change.
     */
    public void invalidateCache(Long doctorId, LocalDate date) {
        segmentTreeCache.remove(getCacheKey(doctorId, date));
    }

    private SegmentTree buildSegmentTreeFromDb(DoctorProfile doctor, LocalDate date) {
        DoctorSchedule schedule = doctorScheduleRepository.findByDoctorAndScheduleDate(doctor, date)
                .orElseGet(() -> {
                    DoctorSchedule newSched = new DoctorSchedule(doctor, date);
                    return doctorScheduleRepository.save(newSched);
                });

        int totalSlots = schedule.getTotalSlots();
        int[] rawSlots = new int[totalSlots];
        Arrays.fill(rawSlots, 1); // initially all slots available

        // Parse blocked slots
        Set<Integer> blocked = parseBlockedSlots(schedule.getBlockedSlots());
        for (int idx : blocked) {
            if (idx >= 0 && idx < totalSlots) {
                rawSlots[idx] = 0; // blocked
            }
        }

        // Query active booked appointments from DB
        List<AppointmentStatus> activeStatuses = Arrays.asList(
                AppointmentStatus.CONFIRMED,
                AppointmentStatus.CHECKED_IN,
                AppointmentStatus.IN_CONSULTATION
        );
        List<Appointment> bookedAppointments = appointmentRepository.findByDoctorIdAndAppointmentDateAndStatusIn(
                doctor.getId(), date, activeStatuses);

        for (Appointment app : bookedAppointments) {
            int slotIdx = app.getSlotIndex();
            if (slotIdx >= 0 && slotIdx < totalSlots) {
                rawSlots[slotIdx] = 0; // booked
            }
        }

        // Mark past slots on today as unavailable
        if (date.isEqual(LocalDate.now())) {
            LocalTime nowTime = LocalTime.now();
            LocalTime start = LocalTime.parse(schedule.getStartTime(), TIME_FMT);
            int duration = schedule.getSlotDurationMinutes();
            for (int i = 0; i < totalSlots; i++) {
                LocalTime slotStart = start.plusMinutes((long) i * duration);
                if (slotStart.isBefore(nowTime)) {
                    rawSlots[i] = 0; // past
                }
            }
        } else if (date.isBefore(LocalDate.now())) {
            Arrays.fill(rawSlots, 0); // past date
        }

        return new SegmentTree(rawSlots);
    }

    /**
     * Retrieves full slot availability for a doctor on a date using the Segment Tree.
     */
    public SlotAvailabilityDto getSlotAvailability(Long doctorId, LocalDate date) {
        DoctorProfile doctor = doctorProfileRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found: " + doctorId));

        DoctorSchedule schedule = doctorScheduleRepository.findByDoctorAndScheduleDate(doctor, date)
                .orElseGet(() -> doctorScheduleRepository.save(new DoctorSchedule(doctor, date)));

        SegmentTree segTree = getOrBuildSegmentTree(doctor, date);
        int totalSlots = schedule.getTotalSlots();
        int duration = schedule.getSlotDurationMinutes();
        LocalTime startTime = LocalTime.parse(schedule.getStartTime(), TIME_FMT);

        Set<Integer> blocked = parseBlockedSlots(schedule.getBlockedSlots());
        List<AppointmentStatus> activeStatuses = Arrays.asList(
                AppointmentStatus.CONFIRMED,
                AppointmentStatus.CHECKED_IN,
                AppointmentStatus.IN_CONSULTATION
        );
        List<Appointment> bookedList = appointmentRepository.findByDoctorIdAndAppointmentDateAndStatusIn(
                doctorId, date, activeStatuses);
        Set<Integer> bookedSlots = new HashSet<>();
        for (Appointment a : bookedList) {
            bookedSlots.add(a.getSlotIndex());
        }

        SlotAvailabilityDto dto = new SlotAvailabilityDto();
        dto.setDoctorId(doctor.getId());
        dto.setDoctorName(doctor.getUser() != null ? doctor.getUser().getFullName() : "Doctor");
        dto.setSpecialization(doctor.getSpecialization().getDisplayName());
        dto.setScheduleDate(date);
        dto.setTotalSlots(totalSlots);
        dto.setAvailableCount(segTree.countAvailable(0, totalSlots - 1));

        List<SlotAvailabilityDto.SlotItem> items = new ArrayList<>();
        LocalTime nowTime = LocalTime.now();
        boolean isToday = date.isEqual(LocalDate.now());
        boolean isPastDate = date.isBefore(LocalDate.now());

        for (int i = 0; i < totalSlots; i++) {
            LocalTime sTime = startTime.plusMinutes((long) i * duration);
            LocalTime eTime = sTime.plusMinutes(duration);
            String sStr = sTime.format(TIME_FMT);
            String eStr = eTime.format(TIME_FMT);

            boolean isAvailable = segTree.isSlotAvailable(i);
            String reason = "AVAILABLE";

            if (isPastDate || (isToday && sTime.isBefore(nowTime))) {
                reason = "PAST";
                isAvailable = false;
            } else if (bookedSlots.contains(i)) {
                reason = "BOOKED";
                isAvailable = false;
            } else if (blocked.contains(i)) {
                reason = "BLOCKED";
                isAvailable = false;
            }

            items.add(new SlotAvailabilityDto.SlotItem(i, sStr, eStr, isAvailable, reason));
        }
        dto.setSlots(items);

        // Compute alternative suggestions if slots are limited or full
        if (dto.getAvailableCount() == 0 || dto.getAvailableCount() < totalSlots / 2) {
            dto.setSuggestions(computeAlternativeSuggestions(doctor, date, 0));
        }

        return dto;
    }

    /**
     * Suggests next suitable slot with:
     * 1. Same doctor (next available slot or next available date)
     * 2. Another doctor of the same specialization
     * 3. Alternative date
     */
    public List<SlotAvailabilityDto.AlternativeSuggestion> computeAlternativeSuggestions(
            DoctorProfile doctor, LocalDate requestedDate, int preferredSlotIndex) {

        List<SlotAvailabilityDto.AlternativeSuggestion> suggestions = new ArrayList<>();

        // 1. Check next available slot for same doctor on same date
        SegmentTree currentTree = getOrBuildSegmentTree(doctor, requestedDate);
        int nextSlotSameDay = currentTree.findNextAvailableSlot(preferredSlotIndex + 1, currentTree.getSize() - 1);
        if (nextSlotSameDay != -1) {
            LocalTime start = LocalTime.parse(doctor.getWorkStartTime(), TIME_FMT)
                    .plusMinutes((long) nextSlotSameDay * doctor.getSlotDurationMinutes());
            LocalTime end = start.plusMinutes(doctor.getSlotDurationMinutes());
            suggestions.add(new SlotAvailabilityDto.AlternativeSuggestion(
                    "SAME_DOCTOR_NEXT_SLOT",
                    "Next slot today with " + doctor.getUser().getFullName() + " at " + start.format(TIME_FMT),
                    doctor.getId(),
                    doctor.getUser().getFullName(),
                    requestedDate,
                    nextSlotSameDay,
                    start.format(TIME_FMT),
                    end.format(TIME_FMT)
            ));
        }

        // 2. Check another doctor of the same specialization on the same date
        List<DoctorProfile> peers = doctorProfileRepository.findBySpecializationAndActiveTrue(doctor.getSpecialization());
        for (DoctorProfile peer : peers) {
            if (!peer.getId().equals(doctor.getId())) {
                SegmentTree peerTree = getOrBuildSegmentTree(peer, requestedDate);
                int peerSlot = peerTree.findNextAvailableSlot(0, peerTree.getSize() - 1);
                if (peerSlot != -1) {
                    LocalTime peerStart = LocalTime.parse(peer.getWorkStartTime(), TIME_FMT)
                            .plusMinutes((long) peerSlot * peer.getSlotDurationMinutes());
                    LocalTime peerEnd = peerStart.plusMinutes(peer.getSlotDurationMinutes());
                    suggestions.add(new SlotAvailabilityDto.AlternativeSuggestion(
                            "OTHER_DOCTOR_SAME_SPEC",
                            "Available with Dr. " + peer.getUser().getFullName() + " (" + peer.getSpecialization().getDisplayName() + ") at " + peerStart.format(TIME_FMT),
                            peer.getId(),
                            peer.getUser().getFullName(),
                            requestedDate,
                            peerSlot,
                            peerStart.format(TIME_FMT),
                            peerEnd.format(TIME_FMT)
                    ));
                    break;
                }
            }
        }

        // 3. Check upcoming dates for same doctor (next 7 days)
        for (int dayOffset = 1; dayOffset <= 7; dayOffset++) {
            LocalDate altDate = requestedDate.plusDays(dayOffset);
            SegmentTree altTree = getOrBuildSegmentTree(doctor, altDate);
            int altSlot = altTree.findNextAvailableSlot(0, altTree.getSize() - 1);
            if (altSlot != -1) {
                LocalTime altStart = LocalTime.parse(doctor.getWorkStartTime(), TIME_FMT)
                        .plusMinutes((long) altSlot * doctor.getSlotDurationMinutes());
                LocalTime altEnd = altStart.plusMinutes(doctor.getSlotDurationMinutes());
                suggestions.add(new SlotAvailabilityDto.AlternativeSuggestion(
                        "ALTERNATIVE_DATE",
                        "Available on " + altDate + " with " + doctor.getUser().getFullName() + " at " + altStart.format(TIME_FMT),
                        doctor.getId(),
                        doctor.getUser().getFullName(),
                        altDate,
                        altSlot,
                        altStart.format(TIME_FMT),
                        altEnd.format(TIME_FMT)
                ));
                break;
            }
        }

        return suggestions;
    }

    /**
     * Updates doctor schedule and blocks/unblocks slots.
     */
    @Transactional
    public void updateScheduleAndBlockedSlots(Long doctorId, DoctorScheduleUpdateDto updateDto) {
        DoctorProfile doctor = doctorProfileRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found: " + doctorId));

        LocalDate date = updateDto.getScheduleDate() != null ? updateDto.getScheduleDate() : LocalDate.now();
        DoctorSchedule schedule = doctorScheduleRepository.findByDoctorAndScheduleDate(doctor, date)
                .orElseGet(() -> new DoctorSchedule(doctor, date));

        if (updateDto.getStartTime() != null) schedule.setStartTime(updateDto.getStartTime());
        if (updateDto.getEndTime() != null) schedule.setEndTime(updateDto.getEndTime());
        if (updateDto.getSlotDurationMinutes() > 0) schedule.setSlotDurationMinutes(updateDto.getSlotDurationMinutes());
        schedule.setTotalSlots(schedule.calculateTotalSlots());

        if (updateDto.getBlockedSlotIndices() != null) {
            String joined = String.join(",", updateDto.getBlockedSlotIndices().stream().map(String::valueOf).toList());
            schedule.setBlockedSlots(joined);
        }

        doctorScheduleRepository.save(schedule);
        invalidateCache(doctorId, date);
    }

    /**
     * Blocks or unblocks a single slot for a doctor on a specific date.
     */
    @Transactional
    public void toggleBlockSlot(Long doctorId, LocalDate date, int slotIndex, boolean block) {
        DoctorProfile doctor = doctorProfileRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found: " + doctorId));

        DoctorSchedule schedule = doctorScheduleRepository.findByDoctorAndScheduleDate(doctor, date)
                .orElseGet(() -> doctorScheduleRepository.save(new DoctorSchedule(doctor, date)));

        Set<Integer> blocked = parseBlockedSlots(schedule.getBlockedSlots());
        if (block) {
            blocked.add(slotIndex);
        } else {
            blocked.remove(slotIndex);
        }

        schedule.setBlockedSlots(String.join(",", blocked.stream().map(String::valueOf).toList()));
        doctorScheduleRepository.save(schedule);

        // Update Segment Tree in cache
        SegmentTree tree = getOrBuildSegmentTree(doctor, date);
        tree.updateSlot(slotIndex, !block);
    }

    private Set<Integer> parseBlockedSlots(String blockedStr) {
        Set<Integer> set = new HashSet<>();
        if (blockedStr != null && !blockedStr.trim().isEmpty()) {
            for (String part : blockedStr.split(",")) {
                try {
                    set.add(Integer.parseInt(part.trim()));
                } catch (NumberFormatException ignored) {}
            }
        }
        return set;
    }

    public LocalTime calculateSlotStartTime(DoctorProfile doctor, int slotIndex) {
        LocalTime start = LocalTime.parse(doctor.getWorkStartTime() != null ? doctor.getWorkStartTime() : "09:00", TIME_FMT);
        int duration = doctor.getSlotDurationMinutes() > 0 ? doctor.getSlotDurationMinutes() : 30;
        return start.plusMinutes((long) slotIndex * duration);
    }

    public LocalTime calculateSlotEndTime(DoctorProfile doctor, int slotIndex) {
        LocalTime start = calculateSlotStartTime(doctor, slotIndex);
        int duration = doctor.getSlotDurationMinutes() > 0 ? doctor.getSlotDurationMinutes() : 30;
        return start.plusMinutes(duration);
    }
}
