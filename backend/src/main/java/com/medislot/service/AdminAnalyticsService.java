package com.medislot.service;

import com.medislot.algorithm.RandomizedQuickselect;
import com.medislot.dto.AdminStatsDto;
import com.medislot.model.Appointment;
import com.medislot.model.AppointmentStatus;
import com.medislot.model.Role;
import com.medislot.model.WaitingListStatus;
import com.medislot.repository.AppointmentRepository;
import com.medislot.repository.DoctorProfileRepository;
import com.medislot.repository.UserRepository;
import com.medislot.repository.WaitingListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Hospital Admin Analytics Service.
 * 
 * Uses Randomized Quickselect (Unit 6 / CO5) to compute
 * Median, 90th percentile, and 95th percentile patient waiting times
 * from actual historical completed appointments in Expected O(N) time.
 */
@Service
public class AdminAnalyticsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DoctorProfileRepository doctorProfileRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private WaitingListRepository waitingListRepository;

    public AdminStatsDto getHospitalAnalytics() {
        AdminStatsDto stats = new AdminStatsDto();

        long totalDocs = doctorProfileRepository.count();
        long activeDocs = doctorProfileRepository.findByActiveTrue().size();
        long totalPatients = userRepository.countByRole(Role.PATIENT);

        stats.setTotalDoctors(totalDocs);
        stats.setActiveDoctors(activeDocs);
        stats.setTotalPatients(totalPatients);

        long completed = appointmentRepository.countByStatus(AppointmentStatus.COMPLETED);
        long cancelled = appointmentRepository.countByStatus(AppointmentStatus.CANCELLED);
        long confirmed = appointmentRepository.countByStatus(AppointmentStatus.CONFIRMED);
        long noShow = appointmentRepository.countByStatus(AppointmentStatus.NO_SHOW);
        long totalApp = appointmentRepository.count();

        stats.setTotalAppointments(totalApp);
        stats.setCompletedAppointments(completed);
        stats.setCancelledAppointments(cancelled);
        stats.setConfirmedAppointments(confirmed);
        stats.setNoShowAppointments(noShow);

        stats.setWaitingListTotal(waitingListRepository.count());
        long urgentWaiting = waitingListRepository.findByStatus(WaitingListStatus.WAITING).stream()
                .filter(w -> w.getPriorityLevel() != null && w.getPriorityLevel().getRank() == 1)
                .count();
        stats.setWaitingListUrgent(urgentWaiting);

        // Overall doctor utilization rate
        // Assuming 16 slots/day per doctor over 30 days = 480 slots capacity per doctor
        long capacity = Math.max(1, activeDocs * 16 * 30);
        double util = Math.min(100.0, ((double) completed / capacity) * 100.0);
        stats.setOverallDoctorUtilization(Math.round(util * 10.0) / 10.0);

        // Unit 6 / CO5: Randomized Quickselect for Waiting-Time Percentiles
        List<Double> waitTimesList = appointmentRepository.findAllCompletedWaitingTimes();
        if (waitTimesList != null && !waitTimesList.isEmpty()) {
            double[] arr = waitTimesList.stream().mapToDouble(Double::doubleValue).toArray();
            RandomizedQuickselect qs = new RandomizedQuickselect();

            double median = qs.calculateMedian(arr);
            double p90 = qs.calculatePercentile(arr, 90.0);
            double p95 = qs.calculatePercentile(arr, 95.0);

            stats.setMedianWaitingTimeMinutes(Math.round(median * 10.0) / 10.0);
            stats.setP90WaitingTimeMinutes(Math.round(p90 * 10.0) / 10.0);
            stats.setP95WaitingTimeMinutes(Math.round(p95 * 10.0) / 10.0);
            stats.setWaitingTimeSampleSize(arr.length);
        } else {
            stats.setMedianWaitingTimeMinutes(0.0);
            stats.setP90WaitingTimeMinutes(0.0);
            stats.setP95WaitingTimeMinutes(0.0);
            stats.setWaitingTimeSampleSize(0);
        }

        // Appointments by Specialization
        List<Appointment> allAppointments = appointmentRepository.findAll();
        Map<String, Long> bySpec = allAppointments.stream()
                .filter(a -> a.getDoctor() != null && a.getDoctor().getSpecialization() != null)
                .collect(Collectors.groupingBy(
                        a -> a.getDoctor().getSpecialization().getDisplayName(),
                        Collectors.counting()
                ));
        stats.setAppointmentsBySpecialization(bySpec);

        // Daily Trends (last 14 days)
        LocalDate today = LocalDate.now();
        List<AdminStatsDto.DailyTrendItem> trends = new ArrayList<>();
        for (int i = 13; i >= 0; i--) {
            LocalDate d = today.minusDays(i);
            long dayTotal = allAppointments.stream().filter(a -> d.equals(a.getAppointmentDate())).count();
            long dayComp = allAppointments.stream().filter(a -> d.equals(a.getAppointmentDate()) && a.getStatus() == AppointmentStatus.COMPLETED).count();
            long dayCanc = allAppointments.stream().filter(a -> d.equals(a.getAppointmentDate()) && a.getStatus() == AppointmentStatus.CANCELLED).count();
            trends.add(new AdminStatsDto.DailyTrendItem(d.toString(), dayTotal, dayComp, dayCanc));
        }
        stats.setAppointmentTrends(trends);

        return stats;
    }
}
