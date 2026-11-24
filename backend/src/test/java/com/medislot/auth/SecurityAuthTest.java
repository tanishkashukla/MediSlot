package com.medislot.auth;

import com.medislot.dto.AuthRequest;
import com.medislot.dto.AuthResponse;
import com.medislot.dto.RegisterRequest;
import com.medislot.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class SecurityAuthTest {

    @Autowired
    private AuthService authService;

    @Test
    @DisplayName("Verify Admin and Doctor Authentication with Valid Credentials")
    public void testValidLogins() {
        // Admin Login
        AuthResponse adminAuth = authService.login(new AuthRequest("admin@medislot.com", "Admin@123"));
        assertNotNull(adminAuth);
        assertNotNull(adminAuth.getToken());
        assertEquals("admin@medislot.com", adminAuth.getEmail());
        assertEquals("ADMIN", adminAuth.getRole().name());

        // Doctor Login
        AuthResponse docAuth = authService.login(new AuthRequest("doctor.cardio@medislot.com", "Doctor@123"));
        assertNotNull(docAuth);
        assertNotNull(docAuth.getToken());
        assertEquals("DOCTOR", docAuth.getRole().name());
    }

    @Test
    @DisplayName("Verify Rejection of Invalid Passwords")
    public void testInvalidPasswordRejection() {
        assertThrows(BadCredentialsException.class, () -> {
            authService.login(new AuthRequest("admin@medislot.com", "WrongPassword!#"));
        });
    }

    @Test
    @DisplayName("Verify Public Patient Registration & Automatic Token Issuance")
    public void testPatientRegistration() {
        RegisterRequest req = new RegisterRequest();
        String uniqueEmail = "test.patient." + System.currentTimeMillis() + "@example.com";
        req.setEmail(uniqueEmail);
        req.setPassword("SecurePass@123");
        req.setFullName("Test Patient Alice");
        req.setPhone("+1-555-9876");

        AuthResponse resp = authService.registerPatient(req);
        assertNotNull(resp);
        assertNotNull(resp.getToken());
        assertEquals("PATIENT", resp.getRole().name());
        assertEquals(uniqueEmail, resp.getEmail());
    }
}
