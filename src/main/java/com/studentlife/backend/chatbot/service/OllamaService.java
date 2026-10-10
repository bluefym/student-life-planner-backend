package com.studentlife.backend.chatbot.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Map;

@Service
public class OllamaService {

    private final RestClient restClient;
    private final String model;

    public OllamaService(
            @Value("${ollama.base-url:http://localhost:11434}") String baseUrl,
            @Value("${ollama.model:llama3.2}") String model) {

        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();

        this.model = model;
    }

    public String generateReply(String systemPrompt, String userMessage) {

        Map<String, Object> requestBody = Map.of(
                "model", model,
                "stream", false,
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", userMessage)
                )
        );

        try {
            OllamaResponse response = restClient.post()
                    .uri("/api/chat")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(OllamaResponse.class);

            if (response == null
                    || response.message() == null
                    || response.message().content() == null
                    || response.message().content().isBlank()) {
                throw new ChatbotException(
                        "Ollama returned an empty response."
                );
            }

            return response.message().content().trim();

        } catch (RestClientException exception) {
            throw new ChatbotException(
                    "Unable to communicate with Ollama. "
                            + "Check that Ollama is running and the model "
                            + "is available.",
                    exception
            );
        }
    }

    private record OllamaResponse(OllamaMessage message) {
    }

    private record OllamaMessage(String role, String content) {
    }

    public static class ChatbotException extends RuntimeException {

        public ChatbotException(String message) {
            super(message);
        }

        public ChatbotException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}