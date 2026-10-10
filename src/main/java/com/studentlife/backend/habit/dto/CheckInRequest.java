package com.studentlife.backend.habit.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

public record CheckInRequest(
    @NotNull(message = "Check-in date is required")
    @PastOrPresent(message = "Cannot check in for a future date")
    LocalDate date
) {}