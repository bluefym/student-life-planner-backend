package com.studentlife.backend.chatbot.service;

import com.studentlife.backend.dashboard.dto.DashboardResponse;
import com.studentlife.backend.dashboard.service.DashboardService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserContextService {

    private final DashboardService dashboardService;

    public UserContextService(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @Transactional(readOnly = true)
    public String getUserContext(String email) {
        DashboardResponse dashboard =
                dashboardService.getDashboard(email);

        return """
                Current date: %s

                User's dashboard data:
                %s
                """.formatted(
                dashboard.date(),
                dashboard
        );
    }
} 