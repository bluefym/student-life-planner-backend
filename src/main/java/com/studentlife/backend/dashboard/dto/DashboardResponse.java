package com.studentlife.backend.dashboard.dto;

import java.time.LocalDate;
import java.util.List;

public record DashboardResponse(
    LocalDate date,
    SpendingSummary spending,
    List<HabitSummary> habits,
    List<TaskSummary> upcomingTasks,
    List<TaskSummary> overdueTasks
) {}