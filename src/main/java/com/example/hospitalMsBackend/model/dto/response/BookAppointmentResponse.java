package com.example.hospitalMsBackend.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookAppointmentResponse {
    private String message;
    private String tokenNumber;   // HOS-XXXXX
    private String doctorName;
    private LocalDate appointmentDate;
    private LocalTime startTime;
}