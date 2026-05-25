package com.example.hospitalMsBackend.repository;

import com.example.hospitalMsBackend.model.entity.QueueEntry;
import com.example.hospitalMsBackend.model.enums.QueueStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface QueueEntryRepository extends JpaRepository<QueueEntry, UUID> {  // Changed from Long to UUID

    Optional<QueueEntry> findByTokenNumberAndStatusNot(String token, QueueStatus status);

    List<QueueEntry> findByDoctorIdAndStatusInOrderByPositionAsc(UUID doctorId, List<QueueStatus> statuses);  // Changed Long to UUID

    @Query("SELECT q FROM QueueEntry q WHERE q.tokenNumber = :token AND q.status != 'COMPLETED'")
    Optional<QueueEntry> findActiveQueueByToken(@Param("token") String token);

    @Query("SELECT COUNT(q) FROM QueueEntry q WHERE " +
            "q.doctor.id = :doctorId AND " +
            "q.status = 'WAITING' AND " +
            "q.position < :currentPosition")
    int countPeopleAhead(@Param("doctorId") UUID doctorId, @Param("currentPosition") int currentPosition);  // Changed Long to UUID

    @Query("SELECT COALESCE(MAX(q.position), 0) FROM QueueEntry q WHERE q.doctor.id = :doctorId AND q.status = 'WAITING'")
    int findLastPosition(@Param("doctorId") UUID doctorId);  // Changed Long to UUID

    // Remove this duplicate method - you already have findLastPosition above
    // @Query("SELECT COALESCE(MAX(q.position), 0) FROM QueueEntry q WHERE q.doctor.id = :doctorId AND q.status = 'WAITING'")
    // int findLastPositionByDoctor(@Param("doctorId") UUID doctorId);
}