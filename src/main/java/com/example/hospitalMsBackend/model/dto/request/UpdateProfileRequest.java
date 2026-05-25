package com.example.hospitalMsBackend.model.dto.request;

import lombok.Data;
import java.time.LocalDate;

@Data
public class UpdateProfileRequest {
    private String fullName;
    private String email;
    private String phone;
    private String profileImage;
    private String address;
    private String city;
    private String state;
    private String nationality;
    private LocalDate dateOfBirth;
    private String gender;
}