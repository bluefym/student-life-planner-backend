package com.studentlife.backend.user;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserRepository userRepository;

    public AdminController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/users")
    public List<AdminUserResponse> listUsers() {
        return userRepository.findAllByOrderByCreatedAtDesc()
            .stream()
            .map(user -> new AdminUserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name(),
                user.getCreatedAt()
            ))
            .toList();
    }

    public record AdminUserResponse(
        Long id,
        String name,
        String email,
        String role,
        java.time.LocalDateTime createdAt
    ) {}
}