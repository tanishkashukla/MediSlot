package com.medislot.repository;

import com.medislot.model.PatientProfile;
import com.medislot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientProfileRepository extends JpaRepository<PatientProfile, Long> {
    Optional<PatientProfile> findByUser(User user);
    Optional<PatientProfile> findByUserId(Long userId);
}
