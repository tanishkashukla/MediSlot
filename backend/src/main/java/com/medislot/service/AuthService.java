package com.medislot.service;

import com.medislot.config.JwtTokenProvider;
import com.medislot.dto.AuthRequest;
import com.medislot.dto.AuthResponse;
import com.medislot.dto.RegisterRequest;
import com.medislot.model.DoctorProfile;
import com.medislot.model.PatientProfile;
import com.medislot.model.Role;
import com.medislot.model.User;
import com.medislot.repository.DoctorProfileRepository;
import com.medislot.repository.PatientProfileRepository;
import com.medislot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PatientProfileRepository patientProfileRepository;

    @Autowired
    private DoctorProfileRepository doctorProfileRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByEmail(request.getEmail().trim().toLowerCase())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        if (!user.isActive()) {
            throw new BadCredentialsException("This account has been deactivated. Please contact administration.");
        }

        String token = tokenProvider.generateToken(user);

        Long profileId = null;
        if (user.getRole() == Role.PATIENT) {
            profileId = patientProfileRepository.findByUser(user)
                    .map(PatientProfile::getId)
                    .orElse(null);
        } else if (user.getRole() == Role.DOCTOR) {
            profileId = doctorProfileRepository.findByUser(user)
                    .map(DoctorProfile::getId)
                    .orElse(null);
        }

        return new AuthResponse(token, user.getId(), user.getEmail(), user.getFullName(), user.getRole(), profileId);
    }

    @Transactional
    public AuthResponse registerPatient(RegisterRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("An account with this email already exists");
        }

        // Only patients can register publicly per requirement #2
        User user = new User(
                email,
                passwordEncoder.encode(request.getPassword()),
                request.getFullName().trim(),
                request.getPhone(),
                Role.PATIENT
        );
        user = userRepository.save(user);

        PatientProfile profile = new PatientProfile();
        profile.setUser(user);
        profile.setDateOfBirth(request.getDateOfBirth());
        profile.setGender(request.getGender());
        profile.setBloodGroup(request.getBloodGroup());
        profile.setEmergencyContact(request.getEmergencyContact());
        profile.setAddress(request.getAddress());
        profile = patientProfileRepository.save(profile);

        String token = tokenProvider.generateToken(user);
        return new AuthResponse(token, user.getId(), user.getEmail(), user.getFullName(), user.getRole(), profile.getId());
    }
}
