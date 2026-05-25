package com.example.hospitalMsBackend.model.dto.request;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class PatientRequest {
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