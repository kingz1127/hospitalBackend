package com.example.hospitalMsBackend.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientResponse {
    private UUID id;
    private String tokenNumber;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String address;
    private String emergencyContact;
    private LocalDateTime registrationDate;
}