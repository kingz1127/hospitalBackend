package com.example.hospitalMsBackend.model.dto.request;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
public class AppointmentRequest {
    private String tokenNumber;
    private UUID doctorId; // Changed to UUID
    private LocalDate appointmentDate;
    private LocalTime startTime;
    private String symptoms;
}