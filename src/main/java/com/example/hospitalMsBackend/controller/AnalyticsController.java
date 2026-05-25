package com.example.hospitalMsBackend.controller;

import com.example.hospitalMsBackend.service.AnalyticsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
@Tag(name = "Analytics Controller", description = "Admin and Super admin has access")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/admin")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public Map<String, Object> getAdminAnalytics() {
        return analyticsService.getAdminStats();
    }
}