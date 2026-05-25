package com.example.hospitalMsBackend.model.dto.request;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
public class BookAppointmentRequest {
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private UUID doctorId;
    private LocalDate appointmentDate;
    private LocalTime startTime;
    private String symptoms;
}