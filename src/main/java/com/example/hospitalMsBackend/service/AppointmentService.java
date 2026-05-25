package com.example.hospitalMsBackend.service;

import com.example.hospitalMsBackend.model.dto.request.AppointmentRequest;
import com.example.hospitalMsBackend.model.dto.request.BookAppointmentRequest;
import com.example.hospitalMsBackend.model.dto.request.LookupRequest;
import com.example.hospitalMsBackend.model.dto.response.AppointmentResponse;
import com.example.hospitalMsBackend.model.entity.Appointment;
import com.example.hospitalMsBackend.model.entity.Patient;
import com.example.hospitalMsBackend.model.entity.User;
import com.example.hospitalMsBackend.model.enums.AppointmentStatus;
import com.example.hospitalMsBackend.repository.AppointmentRepository;
import com.example.hospitalMsBackend.repository.PatientRepository;
import com.example.hospitalMsBackend.repository.UserRepository;
import com.example.hospitalMsBackend.util.TokenGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID; // Added

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    @Transactional
    public String publicBooking(BookAppointmentRequest request) {
        Patient patient = patientRepository.findByPhone(request.getPhone())
                .orElseGet(() -> {
                    Patient p = new Patient();
                    p.setFirstName(request.getFirstName());
                    p.setLastName(request.getLastName());
                    p.setPhone(request.getPhone());
                    p.setEmail(request.getEmail());
                    p.setTokenNumber(TokenGenerator.generatePatientToken());
                    return patientRepository.save(p);
                });

        // Changed Long to UUID for Doctor lookup
        User doctor = userRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        Appointment appointment = new Appointment();
        appointment.setTokenNumber(patient.getTokenNumber());
        appointment.setDoctor(doctor);
        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setStartTime(request.getStartTime());
        appointment.setEndTime(request.getStartTime().plusMinutes(30));
        appointment.setSymptoms(request.getSymptoms());
        appointment.setStatus(AppointmentStatus.SCHEDULED);

        appointmentRepository.save(appointment);
        return patient.getTokenNumber();
    }

    public List<Appointment> lookupByTokenAndPhone(LookupRequest request) {
        Patient patient = patientRepository.findByTokenNumber(request.getTokenNumber())
                .orElseThrow(() -> new RuntimeException("Patient token not found"));

        if (!patient.getPhone().equals(request.getPhone())) {
            throw new RuntimeException("Phone number does not match token");
        }
        return appointmentRepository.findByTokenNumberOrderByAppointmentDateDesc(request.getTokenNumber());
    }

    @Transactional
    public void cancelAppointment(Long appointmentId, String reason) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setCancellationReason(reason);
        appointmentRepository.save(appointment);
    }

    public List<Appointment> getFilteredAppointments(UUID doctorId, LocalDate date) {
        return appointmentRepository.findFilteredAppointments(doctorId, date);
    }

    public Appointment getById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
    }

    public List<LocalTime> getAvailableTimeSlots(UUID doctorId, LocalDate date) {
        List<LocalTime> allSlots = List.of(
                LocalTime.of(9,0), LocalTime.of(9,30), LocalTime.of(10,0), LocalTime.of(10,30),
                LocalTime.of(11,0), LocalTime.of(11,30), LocalTime.of(12,0), LocalTime.of(12,30),
                LocalTime.of(14,0), LocalTime.of(14,30), LocalTime.of(15,0), LocalTime.of(15,30),
                LocalTime.of(16,0), LocalTime.of(16,30)
        );

        List<LocalTime> bookedSlots = appointmentRepository.findByDoctorIdAndAppointmentDate(doctorId, date)
                .stream()
                .filter(a -> a.getStatus() != AppointmentStatus.CANCELLED)
                .map(Appointment::getStartTime)
                .toList();

        return allSlots.stream()
                .filter(slot -> !bookedSlots.contains(slot))
                .toList();
    }

    @Transactional
    public Appointment createAppointmentByStaff(AppointmentRequest request) {
        Patient patient = patientRepository.findByTokenNumber(request.getTokenNumber())
                .orElseThrow(() -> new RuntimeException("Patient token not found"));

        User doctor = userRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        Appointment appointment = new Appointment();
        appointment.setTokenNumber(patient.getTokenNumber());
        appointment.setDoctor(doctor);
        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setStartTime(request.getStartTime());
        appointment.setEndTime(request.getStartTime().plusMinutes(30));
        appointment.setSymptoms(request.getSymptoms());
        appointment.setStatus(AppointmentStatus.SCHEDULED);

        return appointmentRepository.save(appointment);
    }

    public List<AppointmentResponse> getPatientHistory(String tokenNumber) {
        return appointmentRepository.findByTokenNumberOrderByAppointmentDateDesc(tokenNumber)
                .stream()
                .map(this::mapToResponse) // Using helper method
                .toList();
    }

    private AppointmentResponse mapToResponse(Appointment a) {
        return AppointmentResponse.builder()
                .id(a.getId())
                .tokenNumber(a.getTokenNumber())
                .doctorId(a.getDoctor().getId())
                .doctorName(a.getDoctor().getFullName())
                .appointmentDate(a.getAppointmentDate())
                .startTime(a.getStartTime())
                .endTime(a.getEndTime())
                .status(a.getStatus())
                .medicalNotes(a.getDoctorNotes())
                .cancellationReason(a.getCancellationReason())
                .createdAt(a.getCreatedAt())
                .updatedAt(a.getUpdatedAt())
                .build();
    }
}