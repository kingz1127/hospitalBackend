package com.example.hospitalMsBackend.service;

import com.example.hospitalMsBackend.model.dto.response.QueueStatusResponse;
import com.example.hospitalMsBackend.model.entity.Patient;
import com.example.hospitalMsBackend.model.entity.QueueEntry;
import com.example.hospitalMsBackend.model.entity.User;
import com.example.hospitalMsBackend.model.enums.PriorityLevel;
import com.example.hospitalMsBackend.model.enums.QueueStatus;
import com.example.hospitalMsBackend.repository.PatientRepository;
import com.example.hospitalMsBackend.repository.QueueEntryRepository;
import com.example.hospitalMsBackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QueueService {

    private final QueueEntryRepository queueRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    @Transactional
    public void addToQueue(UUID patientId, UUID doctorId, PriorityLevel priority) {
        Patient patient = patientRepository.findById(patientId).orElseThrow();
        User doctor = userRepository.findById(doctorId).orElseThrow();

        int lastPos = queueRepository.findLastPosition(doctorId);  // Changed from findLastPositionByDoctor

        QueueEntry entry = new QueueEntry();
        entry.setPatient(patient);
        entry.setDoctor(doctor);
        entry.setTokenNumber(patient.getTokenNumber());
        entry.setPriorityLevel(priority);
        entry.setStatus(QueueStatus.WAITING);
        entry.setPosition(lastPos + 1);
        entry.setEnteredAt(LocalDateTime.now());

        queueRepository.save(entry);
    }

    @Transactional
    public void callNext(UUID doctorId) {
        // Complete current
        queueRepository.findByDoctorIdAndStatusInOrderByPositionAsc(doctorId, List.of(QueueStatus.WITH_DOCTOR))
                .forEach(entry -> {
                    entry.setStatus(QueueStatus.COMPLETED);
                    entry.setCompletedAt(LocalDateTime.now());
                });

        // Call next
        queueRepository.findByDoctorIdAndStatusInOrderByPositionAsc(doctorId, List.of(QueueStatus.WAITING))
                .stream().findFirst().ifPresent(next -> {
                    next.setStatus(QueueStatus.WITH_DOCTOR);
                    next.setCalledAt(LocalDateTime.now());
                });
    }

    public QueueStatusResponse getPatientQueueStatus(String tokenNumber) {
        QueueEntry entry = queueRepository.findActiveQueueByToken(tokenNumber)
                .orElseThrow(() -> new RuntimeException("No active queue entry found"));

        int ahead = queueRepository.countPeopleAhead(entry.getDoctor().getId(), entry.getPosition());

        return QueueStatusResponse.builder()
                .patientName(entry.getPatient().getFirstName() + " " + entry.getPatient().getLastName())
                .doctorName(entry.getDoctor().getFullName())
                .position(entry.getPosition())
                .peopleAhead(ahead)
                .status(entry.getStatus())
                .estimatedWaitTime((ahead * 15) + " mins")
                .build();
    }

    @Transactional
    public void updatePosition(UUID id, Integer newPosition) {  // Changed from Long to UUID
        QueueEntry entry = queueRepository.findById(id).orElseThrow();
        entry.setPosition(newPosition);
    }
}