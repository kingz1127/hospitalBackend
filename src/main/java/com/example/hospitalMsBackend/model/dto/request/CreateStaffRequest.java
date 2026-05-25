package com.example.hospitalMsBackend.model.dto.request;

import com.example.hospitalMsBackend.model.enums.Gender;
import com.example.hospitalMsBackend.model.enums.Role;
import lombok.Data;
import java.time.LocalDate;

@Data
public class CreateStaffRequest {
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private Role role;
    private Gender gender;
    private String nationality;
    private LocalDate dateOfBirth;
}