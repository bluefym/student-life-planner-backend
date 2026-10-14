package com.studentlife.backend.chatbot.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
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
    private final String apiKey;

    public OllamaService(
            @Value("${ollama.base-url:https://ollama.com}")
            String baseUrl,
            @Value("${ollama.model:gpt-oss:20b-cloud}")
            String model,
            @Value("${ollama.api-key:}")
            String apiKey) {

        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();

        this.model = model;
        this.apiKey = apiKey;
    }

    public String generateReply(
            String systemPrompt,
            String userMessage) {

        if (apiKey == null || apiKey.isBlank()) {
            throw new ChatbotException(
                    "Ollama Cloud API key is not configured.");
        }

        Map<String, Object> requestBody = Map.of(
                "model", model,
                "stream", false,
                "messages", List.of(
                        Map.of(
                                "role", "system",
                                "content", systemPrompt),
                        Map.of(
                                "role", "user",
                                "content", userMessage)
                )
        );

        try {
            OllamaResponse response = restClient.post()
                    .uri("/api/chat")
                    .header(
                            HttpHeaders.AUTHORIZATION,
                            "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(OllamaResponse.class);

            if (response == null
                    || response.message() == null
                    || response.message().content() == null
                    || response.message().content().isBlank()) {
                throw new ChatbotException(
                        "Ollama returned an empty response.");
            }

            return response.message().content().trim();

        } catch (RestClientException exception) {
            throw new ChatbotException(
                    "Ollama Cloud request failed. Check the API key, "
                            + "model availability, usage limits, "
                            + "and backend logs.",
                    exception);
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

        public ChatbotException(
                String message,
                Throwable cause) {
            super(message, cause);
        }
    }
}
