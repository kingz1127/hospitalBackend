package com.example.hospitalMsBackend.service;

import com.example.hospitalMsBackend.model.dto.request.CreatePatientRequest;
import com.example.hospitalMsBackend.model.dto.response.PatientResponse;
import com.example.hospitalMsBackend.model.entity.Patient;
import com.example.hospitalMsBackend.repository.PatientRepository;
import com.example.hospitalMsBackend.util.TokenGenerator;
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
    private final EmailService emailService;

    /**
     * Receptionist registers a new patient.
     * Generates a unique Token (HOS-XXXXX) and emails it to the patient.
     */
    @Transactional
    public PatientResponse registerPatient(CreatePatientRequest request) {
        // 1. Prevent duplicate patients by phone
        if (patientRepository.findByPhone(request.getPhone()).isPresent()) {
            throw new RuntimeException("A patient with phone " + request.getPhone() + " is already registered.");
        }

        // 2. Create the patient entity
        Patient patient = new Patient();
        patient.setFirstName(request.getFirstName());
        patient.setLastName(request.getLastName());
        patient.setPhone(request.getPhone());
        patient.setEmail(request.getEmail() != null ? request.getEmail().toLowerCase().trim() : null);
        patient.setAddress(request.getAddress());
        patient.setEmergencyContact(request.getEmergencyContact());

        // 3. System generates the Token automatically
        String generatedToken = TokenGenerator.generatePatientToken();
        patient.setTokenNumber(generatedToken);

        Patient savedPatient = patientRepository.save(patient);

        // 4. Send Welcome Email with the Token via Brevo
        if (savedPatient.getEmail() != null) {
            emailService.sendPatientWelcomeEmail(
                    savedPatient.getEmail(),
                    savedPatient.getFirstName(),
                    generatedToken
            );
        }

        return convertToResponse(savedPatient);
    }

    /**
     * Recovers a forgotten Token Number using the patient's phone number.
     */
    public void recoverToken(String phone) {
        patientRepository.findByPhone(phone).ifPresent(patient -> {
            if (patient.getEmail() != null) {
                emailService.sendPatientWelcomeEmail(
                        patient.getEmail(),
                        patient.getFirstName(),
                        patient.getTokenNumber()
                );
            } else {
                throw new RuntimeException("Patient found, but no email is registered for recovery. Please contact the front desk.");
            }
        });
    }

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
        if (pDetails.getEmail() != null) patient.setEmail(pDetails.getEmail().toLowerCase().trim());
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
                .orElseThrow(() -> new RuntimeException("Patient profile not found for token: " + tokenNumber));
        return convertToResponse(patient);
    }

    public void recoverToken(String phone, String email) {
        // 1. Find the patient by phone
        Patient patient = patientRepository.findByPhone(phone)
                .orElseThrow(() -> new RuntimeException("No patient found with this phone number"));

        // 2. Verify the email matches to prevent people from harvesting tokens
        if (!patient.getEmail().equalsIgnoreCase(email.trim())) {
            throw new RuntimeException("The email provided does not match our records.");
        }

        // 3. Re-send the welcome email containing the Token
        emailService.sendPatientWelcomeEmail(patient.getEmail(), patient.getFirstName(), patient.getTokenNumber());
    }
}