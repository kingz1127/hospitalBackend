package com.example.hospitalMsBackend.repository;

import com.example.hospitalMsBackend.model.entity.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, Integer> {
    List<TimeSlot> findAllByOrderByIdAsc();
}
