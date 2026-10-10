package com.studentlife.backend.note.controller;

import com.studentlife.backend.note.dto.NoteRequest;
import com.studentlife.backend.note.dto.NoteResponse;
import com.studentlife.backend.note.service.NoteService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @PostMapping
    public ResponseEntity<NoteResponse> create(
            Authentication authentication,
            @Valid @RequestBody NoteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(noteService.create(
                        authentication.getName(), request));
    }

    @GetMapping
    public List<NoteResponse> getAll(
            Authentication authentication) {
        return noteService.getAll(authentication.getName());
    }

    @GetMapping("/{id}")
    public NoteResponse getById(
            Authentication authentication,
            @PathVariable Long id) {
        return noteService.getById(
                authentication.getName(), id);
    }

    @PutMapping("/{id}")
    public NoteResponse update(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody NoteRequest request) {
        return noteService.update(
                authentication.getName(), id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            Authentication authentication,
            @PathVariable Long id) {
        noteService.delete(authentication.getName(), id);
        return ResponseEntity.noContent().build();
    }
}