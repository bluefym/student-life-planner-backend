package com.studentlife.backend.habit.controller;

import com.studentlife.backend.habit.dto.CheckInRequest;
import com.studentlife.backend.habit.service.HabitService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/habits/{habitId}/check-ins")
public class HabitCheckInController {

    private final HabitService habitService;

    public HabitCheckInController(HabitService habitService) {
        this.habitService = habitService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String checkIn(
        Authentication authentication,
        @PathVariable Long habitId,
        @Valid @RequestBody CheckInRequest request
    ) {
        return habitService.checkIn(
            authentication.getName(),
            habitId,
            request
        );
    }
}