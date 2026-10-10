package com.studentlife.backend.auth;

public record AuthResponse(
    String token,
    String tokenType,
    long expiresIn,
    UserProfile user
) {
    public record UserProfile(
        Long id,
        String name,
        String email,
        String role
    ) {}
}