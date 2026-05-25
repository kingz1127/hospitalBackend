package com.example.hospitalMsBackend.model.dto.request;

import lombok.Data;

@Data
public class LookupRequest {
        private String tokenNumber;
        private String phone;
}
