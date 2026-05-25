package com.example.hospitalMsBackend.service;

import com.example.hospitalMsBackend.model.dto.response.PatientResponse;
import com.example.hospitalMsBackend.model.entity.Patient;
import com.example.hospitalMsBackend.repository.PatientRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;

    public List<PatientResponse> getAllPatients() {
        return patientRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<PatientResponse> searchPatients(String query) {
        return patientRepository.searchPatients(query).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public PatientResponse patchPatient(UUID id, Patient pDetails) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        if (pDetails.getFirstName() != null) patient.setFirstName(pDetails.getFirstName());
        if (pDetails.getLastName() != null) patient.setLastName(pDetails.getLastName());
        if (pDetails.getPhone() != null) patient.setPhone(pDetails.getPhone());
        if (pDetails.getEmail() != null) patient.setEmail(pDetails.getEmail());
        if (pDetails.getAddress() != null) patient.setAddress(pDetails.getAddress());
        if (pDetails.getEmergencyContact() != null) patient.setEmergencyContact(pDetails.getEmergencyContact());

        return convertToResponse(patientRepository.save(patient));
    }

    public PatientResponse convertToResponse(Patient p) {
        return PatientResponse.builder()
                .id(p.getId())
                .tokenNumber(p.getTokenNumber())
                .firstName(p.getFirstName())
                .lastName(p.getLastName())
                .phone(p.getPhone())
                .email(p.getEmail())
                .address(p.getAddress())
                .emergencyContact(p.getEmergencyContact())
                .registrationDate(p.getRegistrationDate())
                .build();
    }

    public PatientResponse getPatientByToken(String tokenNumber) {
        Patient patient = patientRepository.findByTokenNumber(tokenNumber)
                .orElseThrow(() -> new RuntimeException("Patient profile not found"));
        return convertToResponse(patient);
    }
}