package com.medislot.repository;

import com.medislot.model.Appointment;
import com.medislot.model.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByPatientIdOrderByAppointmentDateDescStartTimeDesc(Long patientId);

    List<Appointment> findByDoctorIdOrderByAppointmentDateDescStartTimeDesc(Long doctorId);

    List<Appointment> findByDoctorIdAndAppointmentDateOrderBySlotIndexAsc(Long doctorId, LocalDate date);

    List<Appointment> findByDoctorIdAndAppointmentDateAndStatusIn(Long doctorId, LocalDate date, List<AppointmentStatus> statuses);

    Optional<Appointment> findByDoctorIdAndAppointmentDateAndSlotIndexAndStatusIn(
            Long doctorId, LocalDate date, int slotIndex, List<AppointmentStatus> activeStatuses);

    boolean existsByDoctorIdAndAppointmentDateAndSlotIndexAndStatusIn(
            Long doctorId, LocalDate date, int slotIndex, List<AppointmentStatus> activeStatuses);

    long countByStatus(AppointmentStatus status);

    @Query("SELECT a.waitingTimeMinutes FROM Appointment a WHERE a.status = 'COMPLETED' AND a.waitingTimeMinutes > 0")
    List<Double> findAllCompletedWaitingTimes();

    @Query("SELECT a FROM Appointment a WHERE a.patient.id = :patientId " +
           "AND (a.appointmentDate > :today OR (a.appointmentDate = :today AND a.status IN :activeStatuses)) " +
           "ORDER BY a.appointmentDate ASC, a.slotIndex ASC")
    List<Appointment> findUpcomingByPatient(@Param("patientId") Long patientId,
                                           @Param("today") LocalDate today,
                                           @Param("activeStatuses") List<AppointmentStatus> activeStatuses);

    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId " +
           "AND a.appointmentDate = :date " +
           "ORDER BY a.slotIndex ASC")
    List<Appointment> findDailyScheduleForDoctor(@Param("doctorId") Long doctorId,
                                                @Param("date") LocalDate date);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.doctor.id = :doctorId AND a.status = 'COMPLETED'")
    long countCompletedByDoctor(@Param("doctorId") Long doctorId);
}
