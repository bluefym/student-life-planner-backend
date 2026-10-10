package com.studentlife.backend.note.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NoteRequest(

    @NotBlank(message = "Note title is required")
    @Size(max = 200, message = "Title cannot exceed 200 characters")
    String title,

    @Size(max = 20000, message = "Note content is too long")
    String content

) {}