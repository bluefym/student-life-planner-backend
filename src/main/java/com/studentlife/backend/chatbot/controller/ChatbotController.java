package com.studentlife.backend.chatbot.controller;

import com.studentlife.backend.chatbot.dto.ChatRequest;
import com.studentlife.backend.chatbot.dto.ChatResponse;
import com.studentlife.backend.chatbot.service.ChatbotService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
public class ChatbotController {

    private final ChatbotService chatbotService;

    public ChatbotController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @PostMapping
    public ChatResponse chat(
            @Valid @RequestBody ChatRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        return chatbotService.chat(email, request.message());
    }
}