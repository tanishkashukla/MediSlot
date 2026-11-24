package com.medislot.repository;

import com.medislot.model.DoctorProfile;
import com.medislot.model.DoctorSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorScheduleRepository extends JpaRepository<DoctorSchedule, Long> {
    Optional<DoctorSchedule> findByDoctorAndScheduleDate(DoctorProfile doctor, LocalDate scheduleDate);
    Optional<DoctorSchedule> findByDoctorIdAndScheduleDate(Long doctorId, LocalDate scheduleDate);
    List<DoctorSchedule> findByDoctorIdAndScheduleDateBetween(Long doctorId, LocalDate start, LocalDate end);
}
