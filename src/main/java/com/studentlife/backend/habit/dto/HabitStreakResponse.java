package com.studentlife.backend.habit.dto;

public record HabitStreakResponse(
    Long habitId,
    String habitName,
    int currentStreak,
    int longestStreak
) {}