package com.studentlife.backend.habit.dto;

import com.studentlife.backend.habit.entity.Habit;
import java.time.LocalDateTime;

public record HabitResponse(
    Long id,
    String name,
    String description,
    boolean active,
    LocalDateTime createdAt
) {
    public static HabitResponse from(Habit habit) {
        return new HabitResponse(
            habit.getId(),
            habit.getName(),
            habit.getDescription(),
            habit.isActive(),
            habit.getCreatedAt()
        );
    }
}