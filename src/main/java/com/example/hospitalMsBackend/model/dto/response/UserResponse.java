package com.example.hospitalMsBackend.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private UUID id;
    private String username;      // Added
    private String fullName;
    private String email;
    private String phone;
    private String role;
    private String gender;
    private String profileImage;  // Added for the Base64 image
    private String nationality;
    private String address;
    private String city;
    private String state;
    private boolean isActive;     // Added
    private LocalDate dateOfBirth;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}