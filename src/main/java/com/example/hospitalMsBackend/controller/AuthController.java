package com.example.hospitalMsBackend.controller;

import com.example.hospitalMsBackend.exception.BusinessException;
import com.example.hospitalMsBackend.model.dto.request.*;
import com.example.hospitalMsBackend.model.dto.response.AuthResponse;
import com.example.hospitalMsBackend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth controller", description = "Authentication for both Staff and Patients")
public class AuthController {

    private final AuthService authService;

    // --- STAFF AUTH ---

    @PostMapping("/login")
    @Operation(summary = "Login staff (Username/Password)")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) throws BusinessException {
        return ResponseEntity.ok(authService.login(request));
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping("/signup")
    @Operation(summary = "Register new staff (Super Admin Only)")
    public ResponseEntity<AuthResponse> signup(@RequestBody SignupRequest request) throws BusinessException {
        return ResponseEntity.ok(authService.registerStaff(request));
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Staff forgot password - sends email")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) throws BusinessException {
        authService.forgotPassword(request.getEmail());
        return ResponseEntity.ok(Map.of("message", "Password reset email sent"));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Staff reset password using email token")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) throws BusinessException {
        authService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok(Map.of("message", "Password reset successful"));
    }

    @PostMapping("/patient/request-otp")
    @Operation(summary = "Patient requests OTP via email to log in")
    public ResponseEntity<?> requestOtp(@RequestParam String tokenNumber, @RequestParam String email) throws BusinessException {
        authService.requestPatientOtp(tokenNumber, email);
        return ResponseEntity.ok(Map.of("message", "OTP sent to your email"));
    }

    @PostMapping("/patient/verify-otp")
    @Operation(summary = "Patient verifies OTP to get a JWT token")
    public ResponseEntity<AuthResponse> verifyOtp(@RequestParam String tokenNumber, @RequestParam String otp) throws BusinessException {
        return ResponseEntity.ok(authService.verifyPatientOtp(tokenNumber, otp));
    }
}