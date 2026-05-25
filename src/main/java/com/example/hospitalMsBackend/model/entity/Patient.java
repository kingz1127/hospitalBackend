package com.example.hospitalMsBackend.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "patients")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String tokenNumber; // Format: HOS-XXXXX

    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String address;
    private String emergencyContact;
    private LocalDateTime registrationDate = LocalDateTime.now();

    private String otp;
    private LocalDateTime otpExpiry;

    @PrePersist
    protected void onCreate() {
        registrationDate = LocalDateTime.now();
    }
}
