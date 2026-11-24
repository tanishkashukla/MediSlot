package com.medislot.dto;

import java.util.List;
import java.util.Map;

public class AdminStatsDto {
    private long totalDoctors;
    private long activeDoctors;
    private long totalPatients;
    private long totalAppointments;
    private long completedAppointments;
    private long cancelledAppointments;
    private long confirmedAppointments;
    private long noShowAppointments;
    private long waitingListTotal;
    private long waitingListUrgent;
    private double overallDoctorUtilization; // percentage (e.g. 78.5%)

    // Quickselect-computed waiting time percentiles (Unit 6 / CO5)
    private double medianWaitingTimeMinutes;
    private double p90WaitingTimeMinutes;
    private double p95WaitingTimeMinutes;
    private int waitingTimeSampleSize;

    private Map<String, Long> appointmentsBySpecialization;
    private List<DailyTrendItem> appointmentTrends;

    public static class DailyTrendItem {
        private String date;
        private long total;
        private long completed;
        private long cancelled;

        public DailyTrendItem() {}

        public DailyTrendItem(String date, long total, long completed, long cancelled) {
            this.date = date;
            this.total = total;
            this.completed = completed;
            this.cancelled = cancelled;
        }

        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }

        public long getTotal() { return total; }
        public void setTotal(long total) { this.total = total; }

        public long getCompleted() { return completed; }
        public void setCompleted(long completed) { this.completed = completed; }

        public long getCancelled() { return cancelled; }
        public void setCancelled(long cancelled) { this.cancelled = cancelled; }
    }

    public AdminStatsDto() {}

    public long getTotalDoctors() { return totalDoctors; }
    public void setTotalDoctors(long totalDoctors) { this.totalDoctors = totalDoctors; }

    public long getActiveDoctors() { return activeDoctors; }
    public void setActiveDoctors(long activeDoctors) { this.activeDoctors = activeDoctors; }

    public long getTotalPatients() { return totalPatients; }
    public void setTotalPatients(long totalPatients) { this.totalPatients = totalPatients; }

    public long getTotalAppointments() { return totalAppointments; }
    public void setTotalAppointments(long totalAppointments) { this.totalAppointments = totalAppointments; }

    public long getCompletedAppointments() { return completedAppointments; }
    public void setCompletedAppointments(long completedAppointments) { this.completedAppointments = completedAppointments; }

    public long getCancelledAppointments() { return cancelledAppointments; }
    public void setCancelledAppointments(long cancelledAppointments) { this.cancelledAppointments = cancelledAppointments; }

    public long getConfirmedAppointments() { return confirmedAppointments; }
    public void setConfirmedAppointments(long confirmedAppointments) { this.confirmedAppointments = confirmedAppointments; }

    public long getNoShowAppointments() { return noShowAppointments; }
    public void setNoShowAppointments(long noShowAppointments) { this.noShowAppointments = noShowAppointments; }

    public long getWaitingListTotal() { return waitingListTotal; }
    public void setWaitingListTotal(long waitingListTotal) { this.waitingListTotal = waitingListTotal; }

    public long getWaitingListUrgent() { return waitingListUrgent; }
    public void setWaitingListUrgent(long waitingListUrgent) { this.waitingListUrgent = waitingListUrgent; }

    public double getOverallDoctorUtilization() { return overallDoctorUtilization; }
    public void setOverallDoctorUtilization(double overallDoctorUtilization) { this.overallDoctorUtilization = overallDoctorUtilization; }

    public double getMedianWaitingTimeMinutes() { return medianWaitingTimeMinutes; }
    public void setMedianWaitingTimeMinutes(double medianWaitingTimeMinutes) { this.medianWaitingTimeMinutes = medianWaitingTimeMinutes; }

    public double getP90WaitingTimeMinutes() { return p90WaitingTimeMinutes; }
    public void setP90WaitingTimeMinutes(double p90WaitingTimeMinutes) { this.p90WaitingTimeMinutes = p90WaitingTimeMinutes; }

    public double getP95WaitingTimeMinutes() { return p95WaitingTimeMinutes; }
    public void setP95WaitingTimeMinutes(double p95WaitingTimeMinutes) { this.p95WaitingTimeMinutes = p95WaitingTimeMinutes; }

    public int getWaitingTimeSampleSize() { return waitingTimeSampleSize; }
    public void setWaitingTimeSampleSize(int waitingTimeSampleSize) { this.waitingTimeSampleSize = waitingTimeSampleSize; }

    public Map<String, Long> getAppointmentsBySpecialization() { return appointmentsBySpecialization; }
    public void setAppointmentsBySpecialization(Map<String, Long> appointmentsBySpecialization) { this.appointmentsBySpecialization = appointmentsBySpecialization; }

    public List<DailyTrendItem> getAppointmentTrends() { return appointmentTrends; }
    public void setAppointmentTrends(List<DailyTrendItem> appointmentTrends) { this.appointmentTrends = appointmentTrends; }
}
