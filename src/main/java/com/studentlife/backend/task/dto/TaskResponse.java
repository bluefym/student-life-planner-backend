package com.studentlife.backend.task.dto;

import com.studentlife.backend.task.entity.Task;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record TaskResponse(
    Long id,
    String title,
    String description,
    LocalDate dueDate,
    boolean completed,
    boolean overdue,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static TaskResponse from(Task task) {
        boolean overdue = !task.isCompleted()
            && task.getDueDate() != null
            && task.getDueDate().isBefore(LocalDate.now());

        return new TaskResponse(
            task.getId(),
            task.getTitle(),
            task.getDescription(),
            task.getDueDate(),
            task.isCompleted(),
            overdue,
            task.getCreatedAt(),
            task.getUpdatedAt()
        );
    }
}