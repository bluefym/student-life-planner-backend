package com.studentlife.backend.dashboard.controller;

import com.studentlife.backend.dashboard.dto.DashboardResponse;
import com.studentlife.backend.dashboard.service.DashboardService;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public DashboardResponse getDashboard(
            Authentication authentication) {
        return dashboardService.getDashboard(
            authentication.getName()
        );
    }
}