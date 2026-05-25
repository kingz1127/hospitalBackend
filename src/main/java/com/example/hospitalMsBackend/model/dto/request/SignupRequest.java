package com.example.hospitalMsBackend.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequest {
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String phone;
    private String gender; // Received as String to handle .toUpperCase()
    private String role;   // Received as String to handle .toUpperCase()
    private String nationality;
}