package com.medislot.repository;

import com.medislot.model.DoctorProfile;
import com.medislot.model.Specialization;
import com.medislot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorProfileRepository extends JpaRepository<DoctorProfile, Long> {
    Optional<DoctorProfile> findByUser(User user);
    Optional<DoctorProfile> findByUserId(Long userId);
    List<DoctorProfile> findBySpecializationAndActiveTrue(Specialization specialization);
    List<DoctorProfile> findByActiveTrue();

    @Query("SELECT d FROM DoctorProfile d JOIN d.user u " +
           "WHERE d.active = true " +
           "AND (:specialization IS NULL OR d.specialization = :specialization) " +
           "AND (:query IS NULL OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "     OR LOWER(d.department) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<DoctorProfile> searchDoctors(@Param("specialization") Specialization specialization,
                                     @Param("query") String query);
}
