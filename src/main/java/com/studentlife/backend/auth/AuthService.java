package com.studentlife.backend.auth;

import com.studentlife.backend.security.JwtService;
import com.studentlife.backend.user.Role;
import com.studentlife.backend.user.User;
import com.studentlife.backend.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = normalizeEmail(request.email());

        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "An account with this email already exists"
            );
        }

        User user = new User();
        user.setName(request.name().trim());
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.password()));

        // Public registration must never grant administrator access.
        user.setRole(Role.USER);

        User savedUser = userRepository.saveAndFlush(user);

        return createResponse(savedUser);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(AuthRequest request) {
        String email = normalizeEmail(request.email());

        User user = userRepository.findByEmail(email)
            .orElseThrow(AuthService::invalidCredentials);

        if (!passwordEncoder.matches(
                request.password(), user.getPasswordHash())) {
            throw invalidCredentials();
        }

        return createResponse(user);
    }

    private AuthResponse createResponse(User user) {
        return new AuthResponse(
            jwtService.generateToken(user),
            "Bearer",
            jwtService.getExpirationMs() / 1000,
            new AuthResponse.UserProfile(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name()
            )
        );
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private static ResponseStatusException invalidCredentials() {
        return new ResponseStatusException(
            HttpStatus.UNAUTHORIZED,
            "Invalid email or password"
        );
    }
}