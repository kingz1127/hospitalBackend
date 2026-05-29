package com.example.hospitalMsBackend.controller;

import com.example.hospitalMsBackend.exception.BusinessException;
import com.example.hospitalMsBackend.model.dto.request.*;
import com.example.hospitalMsBackend.model.dto.response.AuthResponse;
import com.example.hospitalMsBackend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth controller", description = "Login, Password Recovery, and Patient OTP access")
public class AuthController {

    private final AuthService authService;

    // --- STAFF AUTH ---

    @PostMapping("/login")
    @Operation(summary = "Login staff (Username/Password)")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) throws BusinessException {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Staff forgot password - sends recovery link via Brevo")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) throws BusinessException {
        authService.forgotPassword(request.getEmail());
        return ResponseEntity.ok(Map.of("message", "Password reset email sent"));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Staff reset password using the token received in email")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) throws BusinessException {
        authService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok(Map.of("message", "Password reset successful"));
    }

    // --- PATIENT AUTH (OTP Flow) ---

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

    @PostMapping("/patient/forgot-token")
    @Operation(summary = "Patient retrieves lost Token ID via Phone Number")
    public ResponseEntity<?> forgotToken(@RequestParam String phone) throws BusinessException {
        authService.forgotToken(phone);
        return ResponseEntity.ok(Map.of("message", "Your Token ID has been sent to your registered email address."));
    }
}