package org.example.services.ai;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

@Service
public class AIPivotService {

    private final WebClient webClient;

    @Value("${gigachat.auth-url}")
    private String authUrl;

    @Value("${gigachat.credentials}")
    private String credentials;

    @Value("${gigachat.scope}")
    private String scope;

    // Токен кешируем в памяти (для простоты)
    private String cachedToken = null;
    private long tokenExpiresAt = 0;

    public AIPivotService(@Qualifier("gigaChatWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    // --- Получение токена ---
    private String getToken() {
        if (cachedToken != null && System.currentTimeMillis() < tokenExpiresAt) {
            return cachedToken;
        }

        // WebClient для auth (другой URL)
        WebClient authClient = WebClient.builder().build();

        Map<?, ?> response = authClient.post()
                .uri(authUrl)
                .header(HttpHeaders.AUTHORIZATION, "Basic " + credentials)
                .header("RqUID", UUID.randomUUID().toString())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue("scope=" + scope)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (response == null || !response.containsKey("access_token")) {
            throw new RuntimeException("Не удалось получить токен GigaChat");
        }

        cachedToken = (String) response.get("access_token");
        // expires_at приходит в миллисекундах
        tokenExpiresAt = ((Number) response.get("expires_at")).longValue() - 10_000;

        return cachedToken;
    }

    // --- Основной метод: отправить сообщение ---
    public String ask(String userMessage) {
        String token = getToken();

        Map<String, Object> body = new HashMap<>();
        body.put("model", "GigaChat");
        body.put("messages", List.of(
                Map.of("role", "user", "content", userMessage)
        ));

        Map<?, ?> response = webClient.post()
                .uri("/chat/completions")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        // Извлекаем текст из ответа
        List<?> choices = (List<?>) response.get("choices");
        Map<?, ?> firstChoice = (Map<?, ?>) choices.get(0);
        Map<?, ?> message = (Map<?, ?>) firstChoice.get("message");

        return (String) message.get("content");
    }
}