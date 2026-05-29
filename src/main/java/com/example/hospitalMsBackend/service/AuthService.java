package com.example.hospitalMsBackend.service;

import com.example.hospitalMsBackend.exception.BusinessException;
import com.example.hospitalMsBackend.model.dto.request.CreateStaffRequest;
import com.example.hospitalMsBackend.model.dto.request.LoginRequest;
import com.example.hospitalMsBackend.model.dto.request.SignupRequest;
import com.example.hospitalMsBackend.model.dto.response.AuthResponse;
import com.example.hospitalMsBackend.model.dto.response.UserResponse;
import com.example.hospitalMsBackend.model.entity.Patient;
import com.example.hospitalMsBackend.model.entity.User;
import com.example.hospitalMsBackend.model.enums.Gender;
import com.example.hospitalMsBackend.model.enums.Role;
import com.example.hospitalMsBackend.repository.PatientRepository;
import com.example.hospitalMsBackend.repository.UserRepository;
import com.example.hospitalMsBackend.security.JwtService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final EmailService emailService;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Transactional
    public AuthResponse signup(SignupRequest request) throws BusinessException {
        String username = request.getUsername().toLowerCase().trim();
        if (userRepository.existsByUsername(username))
            throw new BusinessException("Username already exists");

        User user = User.builder()
                .username(username) // Ensure saved as lowercase
                .fullName(request.getFullName())
                .email(request.getEmail().toLowerCase().trim())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .gender(Gender.valueOf(request.getGender().toUpperCase()))
                .role(Role.valueOf(request.getRole().toUpperCase()))
                .nationality(request.getNationality())
                .needsPasswordReset(false)
                .isActive(true)
                .build();

        User savedUser = userRepository.save(user);
        String token = jwtService.generateToken(savedUser);
        return buildAuthResponse(savedUser, token, "Signup successful");
    }

    public AuthResponse login(LoginRequest request) throws BusinessException {
        // 1. Normalize input
        String username = request.getUsername().toLowerCase().trim();

        try {
            // 2. Authenticate via Spring Security
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, request.getPassword())
            );
        } catch (AuthenticationException e) {
            log.error("Login failed for user {}: {}", username, e.getMessage());
            throw new BusinessException("Invalid Username or Password");
        }

        // 3. Fetch user
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("User record not found after authentication"));

        // 4. Generate Token
        String token = jwtService.generateToken(user);
        return buildAuthResponse(user, token, "Login Successful");
    }


    @Transactional
    public void forgotPassword(String email) throws BusinessException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("User not found"));

        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(1));
        userRepository.save(user);

        emailService.sendResetPasswordEmail(user.getEmail(), token);
    }

    @Transactional
    public void resetPassword(String token, String newPassword) throws BusinessException {
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new BusinessException("Invalid reset token"));

        if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Token expired");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        user.setNeedsPasswordReset(false); // User can now login normally
        userRepository.save(user);
    }

    @Transactional
    public UserResponse registerStaff(CreateStaffRequest request) {
        String username = request.getUsername().toLowerCase().trim();
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }

        String generatedPassword = UUID.randomUUID().toString().substring(0, 8);

        User staff = User.builder()
                .username(username) // Standardized lowercase
                .fullName(request.getFullName())
                .email(request.getEmail().toLowerCase().trim())
                .password(passwordEncoder.encode(generatedPassword))
                .role(request.getRole())
                .gender(request.getGender())
                .phone(request.getPhone())
                .nationality(request.getNationality())
                .dateOfBirth(request.getDateOfBirth())
                .needsPasswordReset(true)
                .isActive(true)
                .build();

        User savedUser = userRepository.save(staff);
        emailService.sendOnboardingEmail(savedUser.getEmail(), savedUser.getUsername(), generatedPassword);

        return buildUserResponse(savedUser);
    }

    private AuthResponse buildAuthResponse(User user, String token, String message) {
        return AuthResponse.builder()
                .message(message)
                .token(token)
                .user(buildUserResponse(user)) // Use the same builder for consistency
                .build();
    }

    // Helper method to convert Entity to Response
    private UserResponse buildUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole() != null ? user.getRole().name() : null)
                .gender(user.getGender() != null ? user.getGender().name() : null)
                .profileImage(user.getProfileImage())
                .nationality(user.getNationality())
                .address(user.getAddress())
                .city(user.getCity())
                .state(user.getState())
                .isActive(user.getIsActive() != null ? user.getIsActive() : false)
                .dateOfBirth(user.getDateOfBirth())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }


    @Transactional
    public void requestPatientOtp(String tokenNumber, String email) throws BusinessException {
        Patient patient = patientRepository.findByTokenNumber(tokenNumber)
                .orElseThrow(() -> new BusinessException("Patient token not found"));

        if (!patient.getEmail().equalsIgnoreCase(email)) {
            throw new BusinessException("Email does not match our records");
        }

        String otp = String.format("%06d", new Random().nextInt(999999));
        patient.setOtp(otp);
        patient.setOtpExpiry(LocalDateTime.now().plusMinutes(10));
        patientRepository.save(patient);

        emailService.sendOtpEmail(patient.getEmail(), otp); // Send via Brevo
    }

    public AuthResponse verifyPatientOtp(String tokenNumber, String otp) throws BusinessException {
        Patient patient = patientRepository.findByTokenNumber(tokenNumber)
                .orElseThrow(() -> new BusinessException("Invalid token"));

        if (patient.getOtp() == null || !patient.getOtp().equals(otp) ||
                patient.getOtpExpiry().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Invalid or expired OTP");
        }

        // Clear OTP after use
        patient.setOtp(null);
        patient.setOtpExpiry(null);
        patientRepository.save(patient);

        // Generate JWT with ROLE_PATIENT
        String token = jwtService.generatePatientToken(patient);

        return AuthResponse.builder()
                .token(token)
                .message("Patient login successful")
                .build();
    }

}