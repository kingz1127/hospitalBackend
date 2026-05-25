package com.example.hospitalMsBackend.repository;

import com.example.hospitalMsBackend.model.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    Optional<Appointment> findByTokenNumber(String token);

    // Find all history for a patient
    List<Appointment> findByTokenNumberOrderByAppointmentDateDesc(String tokenNumber);

    // Filter for Staff Dashboard
    @Query("SELECT a FROM Appointment a WHERE " +
            "(:doctorId IS NULL OR a.doctor.id = :doctorId) AND " +
            "(:date IS NULL OR a.appointmentDate = :date) " +
            "ORDER BY a.startTime ASC")
    List<Appointment> findFilteredAppointments(
            @Param("doctorId") UUID doctorId,
            @Param("date") LocalDate date
    );

//    List<Appointment> findFilteredAppointments(UUID doctorId, LocalDate date);
    List<Appointment> findByDoctorIdAndAppointmentDate(UUID doctorId, LocalDate date);

    // Check availability for a specific slot
    boolean existsByDoctorIdAndAppointmentDateAndStartTime(UUID doctorId, LocalDate date, LocalTime startTime);
}
