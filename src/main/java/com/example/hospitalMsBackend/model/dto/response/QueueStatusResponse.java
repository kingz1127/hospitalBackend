package com.example.hospitalMsBackend.model.dto.response;


import com.example.hospitalMsBackend.model.enums.QueueStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QueueStatusResponse {
    private String patientName;
    private String doctorName;
    private Integer position;
    private Integer peopleAhead;
    private QueueStatus status;
    private String estimatedWaitTime; // e.g., "30 mins"
}