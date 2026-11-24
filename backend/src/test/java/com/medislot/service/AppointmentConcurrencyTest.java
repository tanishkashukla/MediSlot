package com.medislot.service;

import com.medislot.dto.AppointmentRequestDto;
import com.medislot.dto.AppointmentResponseDto;
import com.medislot.model.*;
import com.medislot.repository.AppointmentRepository;
import com.medislot.repository.DoctorProfileRepository;
import com.medislot.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AppointmentConcurrencyTest {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private DoctorProfileRepository doctorProfileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Test
    @DisplayName("Verify Double-Booking Prevention Under Concurrent Simultaneous Requests")
    public void testConcurrentDoubleBookingPrevention() throws InterruptedException {
        // Find an active doctor
        List<DoctorProfile> doctors = doctorProfileRepository.findByActiveTrue();
        assertFalse(doctors.isEmpty(), "Active doctors must exist");
        DoctorProfile targetDoctor = doctors.get(0);

        // Pick a test date 15 days in future
        LocalDate testDate = LocalDate.now().plusDays(15);
        int targetSlot = 7; // slot 7

        // Get two distinct patients
        List<User> patients = userRepository.findByRole(Role.PATIENT);
        assertTrue(patients.size() >= 2, "At least two patients must exist");
        User patient1 = patients.get(0);
        User patient2 = patients.get(1);

        int numThreads = 10;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(numThreads);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        for (int i = 0; i < numThreads; i++) {
            final Long patientId = (i % 2 == 0) ? patient1.getId() : patient2.getId();
            executor.submit(() -> {
                try {
                    startLatch.await(); // ensure simultaneous execution
                    AppointmentRequestDto req = new AppointmentRequestDto();
                    req.setDoctorId(targetDoctor.getId());
                    req.setAppointmentDate(testDate);
                    req.setSlotIndex(targetSlot);
                    req.setPriority(PriorityLevel.NORMAL);
                    req.setReasonForVisit("Concurrent booking stress test");

                    appointmentService.bookAppointment(patientId, req);
                    successCount.incrementAndGet();
                } catch (Exception ex) {
                    failureCount.incrementAndGet();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        // Fire all threads at once
        startLatch.countDown();
        doneLatch.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        // Exactly ONE patient must succeed; all other concurrent attempts must fail
        assertEquals(1, successCount.get(),
                "Exactly one concurrent booking attempt must succeed to prevent double-booking");
        assertEquals(numThreads - 1, failureCount.get(),
                "All concurrent conflicting requests must be rejected safely");
    }
}
