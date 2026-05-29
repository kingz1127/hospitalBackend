package com.example.hospitalMsBackend.model.dto.request;

import lombok.Data;

@Data
public class CreatePatientRequest {
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String address;
    private String emergencyContact;
}