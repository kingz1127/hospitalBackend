package com.example.hospitalMsBackend.controller;

import com.example.hospitalMsBackend.model.dto.response.AppointmentResponse;
import com.example.hospitalMsBackend.model.dto.response.PatientResponse;
import com.example.hospitalMsBackend.model.dto.response.QueueStatusResponse;
import com.example.hospitalMsBackend.service.AppointmentService;
import com.example.hospitalMsBackend.service.PatientService;
import com.example.hospitalMsBackend.service.QueueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/portal")
@RequiredArgsConstructor
@PreAuthorize("hasRole('PATIENT')") // Only users with ROLE_PATIENT can access this entire class
@Tag(name = "Patient Portal", description = "Endpoints for patients to access their own records")
public class PatientPortalController {

    private final AppointmentService appointmentService;
    private final PatientService patientService;
    private final QueueService queueService;

    @GetMapping("/profile")
    @Operation(summary = "Patient views their own profile")
    public PatientResponse getMyProfile(Authentication authentication) {
        // authentication.getName() returns the HOS-XXXXX token number from the JWT
        return patientService.getPatientByToken(authentication.getName());
    }

    @GetMapping("/appointments")
    @Operation(summary = "Patient views their own appointment history")
    public List<AppointmentResponse> getMyAppointments(Authentication authentication) {
        return appointmentService.getPatientHistory(authentication.getName());
    }

    @GetMapping("/queue-status")
    @Operation(summary = "Patient tracks their live queue position")
    public QueueStatusResponse getMyQueueStatus(Authentication authentication) {
        return queueService.getPatientQueueStatus(authentication.getName());
    }
}