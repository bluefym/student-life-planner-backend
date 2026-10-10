package com.studentlife.backend.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthRequest(

    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email address")
    @Size(max = 254)
    String email,

    @NotBlank(message = "Password is required")
    @Size(max = 72)
    String password

) {}