package com.studentlife.backend.task.controller;

import com.studentlife.backend.task.dto.TaskRequest;
import com.studentlife.backend.task.dto.TaskResponse;
import com.studentlife.backend.task.service.TaskService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<TaskResponse> create(
            Authentication authentication,
            @Valid @RequestBody TaskRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskService.create(
                        authentication.getName(), request));
    }

    @GetMapping
    public List<TaskResponse> getAll(
            Authentication authentication) {
        return taskService.getAll(authentication.getName());
    }

    @PutMapping("/{id}")
    public TaskResponse update(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody TaskRequest request) {
        return taskService.update(
                authentication.getName(), id, request);
    }

    @PatchMapping("/{id}/completion")
    public TaskResponse setCompleted(
            Authentication authentication,
            @PathVariable Long id,
            @RequestParam boolean completed) {
        return taskService.setCompleted(
                authentication.getName(), id, completed);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            Authentication authentication,
            @PathVariable Long id) {
        taskService.delete(authentication.getName(), id);
        return ResponseEntity.noContent().build();
    }
}
