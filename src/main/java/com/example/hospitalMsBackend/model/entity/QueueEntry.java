package com.example.hospitalMsBackend.model.entity;

import com.example.hospitalMsBackend.model.enums.PriorityLevel;
import com.example.hospitalMsBackend.model.enums.QueueStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "queue_entries")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueueEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private Long appointmentId;
    private String tokenNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    private User doctor;

    @Enumerated(EnumType.STRING)
    private PriorityLevel priorityLevel = PriorityLevel.NORMAL;

    @Enumerated(EnumType.STRING)
    private QueueStatus status = QueueStatus.WAITING;

    private Integer position;
    private LocalDateTime enteredAt = LocalDateTime.now();
    private LocalDateTime calledAt;
    private LocalDateTime completedAt;
    private String notes;
}
