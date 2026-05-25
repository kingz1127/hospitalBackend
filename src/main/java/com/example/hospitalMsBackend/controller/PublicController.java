package com.example.hospitalMsBackend.controller;

import com.example.hospitalMsBackend.model.dto.request.BookAppointmentRequest;
import com.example.hospitalMsBackend.model.dto.request.LookupRequest;
import com.example.hospitalMsBackend.service.AppointmentService;
import com.example.hospitalMsBackend.service.QueueService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
@Tag(name = "Public controller ", description = "All users have access")
public class PublicController {

    private final AppointmentService appointmentService;
    private final QueueService queueService;

    // POST /public/appointments/book – Book appointment (returns token)
    @PostMapping("/appointments/book")
    public ResponseEntity<?> bookAppointment(@RequestBody BookAppointmentRequest request) {
        String token = appointmentService.publicBooking(request);
        return ResponseEntity.ok(Map.of("tokenNumber", token, "message", "Appointment booked successfully"));
    }

    // GET /public/queue/track/{tokenNumber} – Get queue position & wait time
    @GetMapping("/queue/track/{tokenNumber}")
    public ResponseEntity<?> trackQueue(@PathVariable String tokenNumber) {
        return ResponseEntity.ok(queueService.getPatientQueueStatus(tokenNumber));
    }

    // POST /public/patients/lookup – Get history by token + phone
    @PostMapping("/patients/lookup")
    public ResponseEntity<?> lookupHistory(@RequestBody LookupRequest request) {
        return ResponseEntity.ok(appointmentService.lookupByTokenAndPhone(request));
    }

    // PUT /public/appointments/{appointmentId}/cancel – Patient facing cancellation
    @PutMapping("/appointments/{appointmentId}/cancel")
    public ResponseEntity<?> cancelByPatient(@PathVariable Long appointmentId) {
        appointmentService.cancelAppointment(appointmentId, "Cancelled by Patient");
        return ResponseEntity.ok(Map.of("message", "Appointment cancelled"));
    }
}