package com.medislot.service;

import com.medislot.algorithm.CustomBinaryMinHeap;
import com.medislot.dto.WaitingListRequestDto;
import com.medislot.dto.WaitingListResponseDto;
import com.medislot.model.*;
import com.medislot.repository.AppointmentRepository;
import com.medislot.repository.DoctorProfileRepository;
import com.medislot.repository.UserRepository;
import com.medislot.repository.WaitingListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Waiting List Service implemented using Custom Binary Min Heap (Unit 2).
 * 
 * Strict rules:
 *   - No java.util.PriorityQueue is used.
 *   - Urgent priority receives higher administrative queue rank (rank 1 < rank 2).
 *   - Ties in priority are resolved First-Come-First-Served (FCFS) by creation timestamp.
 *   - On appointment cancellation, the available slot is offered to the highest-priority eligible patient.
 *   - 15-minute response window: if declined or expired, automatically offered to next patient in Min Heap.
 */
@Service
public class WaitingListService {

    @Autowired
    private WaitingListRepository waitingListRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DoctorProfileRepository doctorProfileRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DoctorScheduleService doctorScheduleService;

    @Value("${medislot.waiting-list.offer-expiry-ms:900000}")
    private long offerExpiryMs; // 15 minutes in ms

    /**
     * Patient joins the waiting list.
     */
    @Transactional
    public WaitingListResponseDto joinWaitingList(Long patientId, WaitingListRequestDto request) {
        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found: " + patientId));

        DoctorProfile doctor = null;
        if (request.getDoctorId() != null) {
            doctor = doctorProfileRepository.findById(request.getDoctorId())
                    .orElseThrow(() -> new IllegalArgumentException("Doctor not found: " + request.getDoctorId()));
        }

        WaitingListEntry entry = new WaitingListEntry();
        entry.setPatient(patient);
        entry.setDoctor(doctor);
        entry.setSpecialization(request.getSpecialization());
        entry.setPriorityLevel(request.getPriorityLevel() != null ? request.getPriorityLevel() : PriorityLevel.NORMAL);
        entry.setPreferredDate(request.getPreferredDate());
        entry.setPreferredTimeRange(request.getPreferredTimeRange() != null ? request.getPreferredTimeRange() : "ANY");
        entry.setStatus(WaitingListStatus.WAITING);
        entry.setCreatedAt(LocalDateTime.now());

        entry = waitingListRepository.save(entry);

        int position = calculateLiveQueuePosition(entry);
        return WaitingListResponseDto.fromEntity(entry, position);
    }

    /**
     * Calculates the patient's current 1-based position in the waiting list using CustomBinaryMinHeap.
     */
    public int calculateLiveQueuePosition(WaitingListEntry targetEntry) {
        if (targetEntry.getStatus() != WaitingListStatus.WAITING) {
            return 0;
        }

        List<WaitingListEntry> candidates = waitingListRepository.findEligibleWaitingEntries(
                targetEntry.getDoctor() != null ? targetEntry.getDoctor().getId() : null,
                targetEntry.getSpecialization(),
                targetEntry.getPreferredDate()
        );

        CustomBinaryMinHeap heap = new CustomBinaryMinHeap(candidates.size() + 1);
        for (WaitingListEntry c : candidates) {
            long ts = c.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            heap.insert(new CustomBinaryMinHeap.HeapNode(
                    c.getId(),
                    c.getPatient().getId(),
                    c.getPatient().getFullName(),
                    c.getDoctor() != null ? c.getDoctor().getId() : null,
                    c.getSpecialization().name(),
                    c.getPriorityLevel().getRank(),
                    ts,
                    c.getPreferredDate().toString(),
                    c.getPreferredTimeRange()
            ));
        }

        int pos = 1;
        while (!heap.isEmpty()) {
            CustomBinaryMinHeap.HeapNode top = heap.extractMin();
            if (top != null && top.getEntryId().equals(targetEntry.getId())) {
                return pos;
            }
            pos++;
        }
        return 1;
    }

    /**
     * Core Algorithm Integration:
     * When an appointment is cancelled, retrieves eligible waiting patients,
     * builds the Custom Binary Min Heap, pops the root (minimum key = highest priority),
     * and offers the slot to them with a 15-minute expiration countdown.
     */
    @Transactional
    public Optional<WaitingListEntry> offerSlotToHighestPriorityCandidate(
            DoctorProfile doctor, LocalDate date, int slotIndex, String startTime, String endTime) {

        List<WaitingListEntry> candidates = waitingListRepository.findEligibleWaitingEntries(
                doctor.getId(), doctor.getSpecialization(), date);

        if (candidates.isEmpty()) {
            return Optional.empty();
        }

        // Build Custom Binary Min Heap
        CustomBinaryMinHeap heap = new CustomBinaryMinHeap(candidates.size());
        for (WaitingListEntry entry : candidates) {
            long ts = entry.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            heap.insert(new CustomBinaryMinHeap.HeapNode(
                    entry.getId(),
                    entry.getPatient().getId(),
                    entry.getPatient().getFullName(),
                    entry.getDoctor() != null ? entry.getDoctor().getId() : null,
                    entry.getSpecialization().name(),
                    entry.getPriorityLevel().getRank(),
                    ts,
                    entry.getPreferredDate().toString(),
                    entry.getPreferredTimeRange()
            ));
        }

        // Extract Min = highest priority (Urgent first, then FCFS timestamp)
        CustomBinaryMinHeap.HeapNode winnerNode = heap.extractMin();
        if (winnerNode == null) {
            return Optional.empty();
        }

        WaitingListEntry winner = waitingListRepository.findById(winnerNode.getEntryId()).orElse(null);
        if (winner == null || winner.getStatus() != WaitingListStatus.WAITING) {
            return Optional.empty();
        }

        winner.setStatus(WaitingListStatus.OFFERED);
        winner.setOfferedDate(date);
        winner.setOfferedSlotIndex(slotIndex);
        winner.setOfferedStartTime(startTime);
        winner.setOfferedEndTime(endTime);
        winner.setOfferExpiresAt(LocalDateTime.now().plusSeconds(offerExpiryMs / 1000));

        waitingListRepository.save(winner);
        return Optional.of(winner);
    }

    /**
     * Patient accepts an offered slot.
     */
    @Transactional
    public Appointment acceptOffer(Long patientId, Long waitingListEntryId) {
        WaitingListEntry entry = waitingListRepository.findById(waitingListEntryId)
                .orElseThrow(() -> new IllegalArgumentException("Waiting list entry not found: " + waitingListEntryId));

        if (!entry.getPatient().getId().equals(patientId)) {
            throw new IllegalArgumentException("You are not authorized to accept this offer");
        }

        if (entry.getStatus() != WaitingListStatus.OFFERED) {
            throw new IllegalStateException("This offer is no longer valid (Status: " + entry.getStatus() + ")");
        }

        if (entry.getOfferExpiresAt() != null && LocalDateTime.now().isAfter(entry.getOfferExpiresAt())) {
            entry.setStatus(WaitingListStatus.EXPIRED);
            waitingListRepository.save(entry);
            // Re-offer to next eligible patient in Min Heap!
            offerNextInLine(entry);
            throw new IllegalStateException("This 15-minute offer window has expired. The slot has been offered to the next patient.");
        }

        DoctorProfile doctor = entry.getDoctor();
        if (doctor == null) {
            List<DoctorProfile> specDoctors = doctorProfileRepository.findBySpecializationAndActiveTrue(entry.getSpecialization());
            if (!specDoctors.isEmpty()) {
                doctor = specDoctors.get(0);
            }
        }

        // Create Confirmed Appointment
        Appointment appointment = new Appointment();
        appointment.setPatient(entry.getPatient());
        appointment.setDoctor(doctor);
        appointment.setAppointmentDate(entry.getOfferedDate());
        appointment.setSlotIndex(entry.getOfferedSlotIndex());
        appointment.setStartTime(entry.getOfferedStartTime());
        appointment.setEndTime(entry.getOfferedEndTime());
        appointment.setPriority(entry.getPriorityLevel());
        appointment.setStatus(AppointmentStatus.CONFIRMED);
        appointment.setReasonForVisit("Booked via Waiting-List Offer Acceptance");
        appointment = appointmentRepository.save(appointment);

        // Update Segment Tree
        doctorScheduleService.getOrBuildSegmentTree(doctor, entry.getOfferedDate())
                .updateSlot(entry.getOfferedSlotIndex(), false);

        entry.setStatus(WaitingListStatus.ACCEPTED);
        waitingListRepository.save(entry);

        return appointment;
    }

    /**
     * Patient declines an offered slot.
     */
    @Transactional
    public void declineOffer(Long patientId, Long waitingListEntryId) {
        WaitingListEntry entry = waitingListRepository.findById(waitingListEntryId)
                .orElseThrow(() -> new IllegalArgumentException("Waiting list entry not found: " + waitingListEntryId));

        if (!entry.getPatient().getId().equals(patientId)) {
            throw new IllegalArgumentException("You are not authorized to decline this offer");
        }

        if (entry.getStatus() != WaitingListStatus.OFFERED) {
            throw new IllegalStateException("Offer is not currently active");
        }

        entry.setStatus(WaitingListStatus.DECLINED);
        waitingListRepository.save(entry);

        // Re-offer slot to next patient in Binary Min Heap!
        offerNextInLine(entry);
    }

    private void offerNextInLine(WaitingListEntry previousEntry) {
        DoctorProfile doctor = previousEntry.getDoctor();
        if (doctor != null && previousEntry.getOfferedDate() != null && previousEntry.getOfferedSlotIndex() != null) {
            offerSlotToHighestPriorityCandidate(
                    doctor,
                    previousEntry.getOfferedDate(),
                    previousEntry.getOfferedSlotIndex(),
                    previousEntry.getOfferedStartTime(),
                    previousEntry.getOfferedEndTime()
            );
        }
    }

    /**
     * Periodic background task (every 30 seconds):
     * Sweeps expired 15-minute offers and offers them to the next eligible patient in the Min Heap.
     */
    @Scheduled(fixedRate = 30000)
    @Transactional
    public void processExpiredOffers() {
        LocalDateTime now = LocalDateTime.now();
        List<WaitingListEntry> expired = waitingListRepository.findByStatusAndOfferExpiresAtBefore(
                WaitingListStatus.OFFERED, now);

        for (WaitingListEntry entry : expired) {
            entry.setStatus(WaitingListStatus.EXPIRED);
            waitingListRepository.save(entry);
            offerNextInLine(entry);
        }
    }

    public List<WaitingListResponseDto> getPatientWaitingList(Long patientId) {
        List<WaitingListEntry> list = waitingListRepository.findByPatientIdOrderByCreatedAtDesc(patientId);
        List<WaitingListResponseDto> dtos = new ArrayList<>();
        for (WaitingListEntry e : list) {
            int pos = calculateLiveQueuePosition(e);
            dtos.add(WaitingListResponseDto.fromEntity(e, pos));
        }
        return dtos;
    }

    public List<WaitingListResponseDto> getAllWaitingEntries() {
        List<WaitingListEntry> list = waitingListRepository.findAll();
        List<WaitingListResponseDto> dtos = new ArrayList<>();
        for (WaitingListEntry e : list) {
            int pos = calculateLiveQueuePosition(e);
            dtos.add(WaitingListResponseDto.fromEntity(e, pos));
        }
        return dtos;
    }
}
