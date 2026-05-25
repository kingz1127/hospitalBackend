package com.example.hospitalMsBackend.controller;

import com.example.hospitalMsBackend.model.dto.response.PatientResponse;
import com.example.hospitalMsBackend.model.entity.Patient;
import com.example.hospitalMsBackend.repository.PatientRepository;
import com.example.hospitalMsBackend.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
@Tag(name = "Patient controller", description = "Admin and Receptionist has access")
public class PatientController {

    private final PatientRepository patientRepository;
    private final PatientService patientService;

    @GetMapping
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    @GetMapping("/{id}")
    public Patient getPatient(@PathVariable UUID id) { // CHANGED TO UUID
        return patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
    }

    @GetMapping("/search")
    public List<Patient> searchPatients(@RequestParam String query) {
        return patientRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(query, query);
    }

    @PutMapping("/{id}")
    public Patient updatePatient(@PathVariable UUID id, @RequestBody Patient patientDetails) { // CHANGED TO UUID
        Patient patient = patientRepository.findById(id).orElseThrow();
        patient.setFirstName(patientDetails.getFirstName());
        patient.setLastName(patientDetails.getLastName());
        patient.setEmail(patientDetails.getEmail());
        patient.setAddress(patientDetails.getAddress());
        patient.setEmergencyContact(patientDetails.getEmergencyContact());
        return patientRepository.save(patient);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partial update of patient records")
    public ResponseEntity<PatientResponse> patchPatient(@PathVariable UUID id, @RequestBody Patient patientDetails) {
        return ResponseEntity.ok(patientService.patchPatient(id, patientDetails));
    }
}