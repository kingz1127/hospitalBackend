package com.example.hospitalMsBackend.service;

import com.example.hospitalMsBackend.repository.AppointmentRepository;
import com.example.hospitalMsBackend.repository.PatientRepository;
import com.example.hospitalMsBackend.repository.QueueEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final QueueEntryRepository queueRepository;

    public Map<String, Object> getAdminStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalPatients", patientRepository.count());
        stats.put("totalAppointments", appointmentRepository.count());
        // Add more complex counts as needed
        return stats;
    }
}