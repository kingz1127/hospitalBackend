package com.example.hospitalMsBackend.controller;

import com.example.hospitalMsBackend.model.dto.request.QueuePositionRequest;
import com.example.hospitalMsBackend.model.enums.PriorityLevel;
import com.example.hospitalMsBackend.service.QueueService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/queue")
@RequiredArgsConstructor
@Tag(name = "Queue Controller", description = "Receptionist and doctor only has access")
public class QueueController {

    private final QueueService queueService;

    @PostMapping
    @PreAuthorize("hasRole('RECEPTIONIST')")
    public void addToQueue(@RequestParam UUID patientId,      // Changed from Long to UUID
                           @RequestParam UUID doctorId,      // Changed from Long to UUID
                           @RequestParam PriorityLevel priority) {
        queueService.addToQueue(patientId, doctorId, priority);
    }

    @PostMapping("/doctor/{doctorId}/call-next")
    @PreAuthorize("hasRole('DOCTOR')")
    public void callNext(@PathVariable UUID doctorId) {      // Changed from Long to UUID
        queueService.callNext(doctorId);
    }

    @PutMapping("/{id}/position")
    @PreAuthorize("hasRole('RECEPTIONIST')")
    public ResponseEntity<?> updatePosition(@PathVariable UUID id,           // Changed from Long to UUID
                                            @RequestBody QueuePositionRequest request) {
        queueService.updatePosition(id, request.getNewPosition());
        return ResponseEntity.ok(Map.of("message", "Queue reordered successfully"));
    }
}