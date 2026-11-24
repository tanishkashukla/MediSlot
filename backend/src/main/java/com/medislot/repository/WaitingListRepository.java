package com.medislot.repository;

import com.medislot.model.Specialization;
import com.medislot.model.WaitingListEntry;
import com.medislot.model.WaitingListStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WaitingListRepository extends JpaRepository<WaitingListEntry, Long> {

    List<WaitingListEntry> findByPatientIdOrderByCreatedAtDesc(Long patientId);

    List<WaitingListEntry> findByStatus(WaitingListStatus status);

    List<WaitingListEntry> findBySpecializationAndPreferredDateAndStatus(
            Specialization specialization, LocalDate preferredDate, WaitingListStatus status);

    List<WaitingListEntry> findByDoctorIdAndPreferredDateAndStatus(
            Long doctorId, LocalDate preferredDate, WaitingListStatus status);

    @Query("SELECT w FROM WaitingListEntry w " +
           "WHERE w.status = 'WAITING' " +
           "AND w.preferredDate = :date " +
           "AND (w.doctor.id = :doctorId OR (w.doctor IS NULL AND w.specialization = :specialization)) " +
           "ORDER BY w.priorityLevel ASC, w.createdAt ASC")
    List<WaitingListEntry> findEligibleWaitingEntries(@Param("doctorId") Long doctorId,
                                                     @Param("specialization") Specialization specialization,
                                                     @Param("date") LocalDate date);

    List<WaitingListEntry> findByStatusAndOfferExpiresAtBefore(WaitingListStatus status, LocalDateTime now);

    Optional<WaitingListEntry> findByPatientIdAndStatus(Long patientId, WaitingListStatus status);

    long countByStatus(WaitingListStatus status);
}
