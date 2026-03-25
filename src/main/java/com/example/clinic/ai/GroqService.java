package com.example.clinic.ai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GroqService {

    private static final double DEFAULT_TEXT_TEMPERATURE = 0.3;  // ładniejsze, bardziej naturalne odpowiedzi
    private static final double DEFAULT_JSON_TEMPERATURE = 0.0;  // stabilniejsze, deterministyczne JSON-y
    private static final int DEFAULT_MAX_TOKENS = 650;

    private final WebClient client;
    private final String model;

    public GroqService(
            @Value("${GROQ_API_KEY:${groq.api.key:}}") String apiKey,
            @Value("${groq.model:llama-3.1-8b-instant}") String model
    ) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalArgumentException("Brak klucza API Groq. Ustaw GROQ_API_KEY lub groq.api.key");
        }
        this.model = model;
        this.client = WebClient.builder()
                .baseUrl("https://api.groq.com/openai/v1")
                .defaultHeader("Authorization", "Bearer " + apiKey.trim())
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    public String chatJson(String system, String user) {
        return chatInternal(system, user, /*jsonMode*/ true, DEFAULT_JSON_TEMPERATURE);
    }

    //Zwraca odpowiedź w tekście dla streszczeń i notatek

    public String chatText(String system, String user) {
        return chatInternal(system, user, /*jsonMode*/ false, DEFAULT_TEXT_TEMPERATURE);
    }

    public String chat(String system, String user) {
        return chatJson(system, user);
    }

    private String chatInternal(String system, String user, boolean jsonMode, double temperature) {
        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        body.put("temperature", temperature);
        body.put("max_tokens", GroqService.DEFAULT_MAX_TOKENS);

        if (jsonMode) {
            Map<String, Object> responseFormat = new HashMap<>();
            responseFormat.put("type", "json_object");
            body.put("response_format", responseFormat);
        }

        String sysContent = (system == null ? "" : system);
        if (jsonMode) {
            sysContent += "\n\nIMPORTANT: Respond in JSON only. Output must be valid JSON.";
        }

        Map<String, String> sys = Map.of("role", "system", "content", sysContent);
        Map<String, String> usr = Map.of("role", "user", "content", user == null ? "" : user);
        body.put("messages", List.of(sys, usr));

        return client.post()
                .uri("/chat/completions")
                .bodyValue(body)
                .retrieve()
                .onStatus(org.springframework.http.HttpStatusCode::isError, this::readErrorBody)
                .bodyToMono(Map.class)
                .map(this::extractContent)
                .onErrorMap(e -> new RuntimeException("Błąd Groq API: " + e.getMessage(), e))
                .block();
    }

    private Mono<? extends Throwable> readErrorBody(ClientResponse resp) {
        return resp.bodyToMono(String.class)
                .defaultIfEmpty("(brak treści)")
                .map(msg -> new RuntimeException("Błąd Groq API: " + resp.statusCode() + " - " + msg));
    }

    @SuppressWarnings("unchecked")
    private String extractContent(Map<?, ?> resp) {
        var choices = (List<Map<String, Object>>) resp.get("choices");
        if (choices == null || choices.isEmpty()) {
            throw new RuntimeException("Brak odpowiedzi (choices) z Groq");
        }
        var message = (Map<String, Object>) choices.get(0).get("message");
        Object content = message != null ? message.get("content") : null;
        return content == null ? "" : content.toString();
    }
}