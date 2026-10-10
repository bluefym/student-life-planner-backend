package com.studentlife.backend.habit.controller;

import com.studentlife.backend.habit.dto.HabitRequest;
import com.studentlife.backend.habit.dto.HabitResponse;
import com.studentlife.backend.habit.service.HabitService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/habits")
public class HabitController {

    private final HabitService habitService;

    public HabitController(HabitService habitService) {
        this.habitService = habitService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HabitResponse create(
        Authentication authentication,
        @Valid @RequestBody HabitRequest request
    ) {
        return habitService.create(
            authentication.getName(), request
        );
    }

    @GetMapping
    public List<HabitResponse> getAll(
        Authentication authentication
    ) {
        return habitService.getAll(authentication.getName());
    }

    @PutMapping("/{id}")
    public HabitResponse update(
        Authentication authentication,
        @PathVariable Long id,
        @Valid @RequestBody HabitRequest request
    ) {
        return habitService.update(
            authentication.getName(), id, request
        );
    }

   @DeleteMapping("/{id}")
@ResponseStatus(HttpStatus.NO_CONTENT)
public void delete(
        Authentication authentication,
        @PathVariable Long id
) {
    System.out.println("DELETE reached controller");
    System.out.println("Authenticated user: " + authentication.getName());

    habitService.delete(authentication.getName(), id);
}
    @PatchMapping("/{id}/active")
    public HabitResponse setActive(
        Authentication authentication,
        @PathVariable Long id,
        @RequestParam boolean active
    ) {
        return habitService.setActive(
            authentication.getName(), id, active
        );
    }

    @GetMapping("/{id}/streak")
    public com.studentlife.backend.habit.dto.HabitStreakResponse getStreak(
        Authentication authentication,
        @PathVariable Long id
    ) {
        return habitService.getStreak(
            authentication.getName(), id
        );
    }
}