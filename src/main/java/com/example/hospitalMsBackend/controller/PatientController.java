package com.example.hospitalMsBackend.controller;

import com.example.hospitalMsBackend.model.dto.request.CreatePatientRequest;
import com.example.hospitalMsBackend.model.dto.response.PatientResponse;
import com.example.hospitalMsBackend.model.entity.Patient;
import com.example.hospitalMsBackend.repository.PatientRepository;
import com.example.hospitalMsBackend.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('RECEPTIONIST', 'ADMIN', 'SUPER_ADMIN')")
@Tag(name = "Patient Controller", description = "Operations for staff to manage patients")
public class PatientController {

    private final PatientRepository patientRepository;
    private final PatientService patientService;

    @PostMapping
    @Operation(summary = "Receptionist registers a new patient (Fills the form)")
    public ResponseEntity<PatientResponse> createPatient(@RequestBody CreatePatientRequest request) {
        return ResponseEntity.ok(patientService.registerPatient(request));
    }

    @GetMapping
    @Operation(summary = "View all patients with pagination (Prevents database lag)")
    public ResponseEntity<Page<PatientResponse>> getAllPatients(
            @ParameterObject @PageableDefault(size = 10) Pageable pageable) {
        // Map entity to Response DTO to hide OTP fields
        Page<PatientResponse> patients = patientRepository.findAll(pageable)
                .map(patientService::convertToResponse);
        return ResponseEntity.ok(patients);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a single patient's details by UUID")
    public ResponseEntity<PatientResponse> getPatient(@PathVariable UUID id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        return ResponseEntity.ok(patientService.convertToResponse(patient));
    }

    @GetMapping("/search")
    @Operation(summary = "Search patients by name or phone")
    public List<PatientResponse> searchPatients(@RequestParam String query) {
        // Uses the service to ensure logic is consistent and returns DTOs
        return patientService.searchPatients(query);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Full update of a patient record")
    public ResponseEntity<PatientResponse> updatePatient(@PathVariable UUID id, @RequestBody Patient patientDetails) {
        // It is better to use the Service for this logic
        return ResponseEntity.ok(patientService.patchPatient(id, patientDetails));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partial update of patient records (e.g., just phone or address)")
    public ResponseEntity<PatientResponse> patchPatient(@PathVariable UUID id, @RequestBody Patient patientDetails) {
        return ResponseEntity.ok(patientService.patchPatient(id, patientDetails));
    }
}