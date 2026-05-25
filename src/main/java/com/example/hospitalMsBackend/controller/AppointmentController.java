package com.example.hospitalMsBackend.controller;

import com.example.hospitalMsBackend.model.dto.request.AppointmentRequest;
import com.example.hospitalMsBackend.model.dto.request.CancelRequest;
import com.example.hospitalMsBackend.model.entity.Appointment;
import com.example.hospitalMsBackend.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID; // Added UUID import

@RestController
@RequestMapping("/appointments")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'RECEPTIONIST', 'SUPER_ADMIN')")
public class AppointmentController {

    private final AppointmentService appointmentService;

    // GET /appointments – doctorId is now UUID
    @GetMapping
    public List<Appointment> getAppointments(
            @RequestParam(required = false) UUID doctorId, // Changed to UUID
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return appointmentService.getFilteredAppointments(doctorId, date);
    }

    // GET /appointments/{id} – Appointment ID remains Long
    @GetMapping("/{id}")
    public Appointment getAppointment(@PathVariable Long id) {
        return appointmentService.getById(id);
    }

    @PostMapping
    public ResponseEntity<Appointment> createByStaff(@RequestBody AppointmentRequest request) {
        return ResponseEntity.ok(appointmentService.createAppointmentByStaff(request));
    }

    @PutMapping("/{id}/cancel")
    public void cancelByStaff(@PathVariable Long id, @RequestBody CancelRequest request) {
        appointmentService.cancelAppointment(id, request.getReason());
    }

    // GET /appointments/available-slots – doctorId is now UUID
    @GetMapping("/available-slots")
    public List<LocalTime> getAvailableSlots(
            @RequestParam UUID doctorId, // Changed to UUID
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return appointmentService.getAvailableTimeSlots(doctorId, date);
    }
}