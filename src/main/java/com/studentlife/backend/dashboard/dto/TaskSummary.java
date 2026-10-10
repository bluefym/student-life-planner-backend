package com.studentlife.backend.dashboard.dto;

import java.time.LocalDate;

public record TaskSummary(
    Long id,
    String title,
    LocalDate dueDate,
    boolean completed,
    boolean overdue
) {}