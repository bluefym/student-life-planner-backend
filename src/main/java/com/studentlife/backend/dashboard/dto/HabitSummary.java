package com.studentlife.backend.dashboard.dto;

public record HabitSummary(
    Long id,
    String name,
    boolean completedToday,
    int currentStreak,
    int longestStreak
) {}