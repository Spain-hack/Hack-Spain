package org.example.controllers;

import org.example.services.ai.AIPivotService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/pivot")
public class PivotController {

    private final AIPivotService aiService;

    public PivotController(AIPivotService aiService) {
        this.aiService = aiService;
    }

    // POST /api/pivot/ask
    // Body: { "message": "Привет, как дела?" }
    @PostMapping("/ask")
    public ResponseEntity<?> ask(@RequestBody Map<String, String> body) {
        String message = body.get("message");
        if (message == null || message.isBlank()) {
            return ResponseEntity.badRequest().body("Поле 'message' обязательно");
        }

        String answer = aiService.ask(message);
        return ResponseEntity.ok(Map.of("answer", answer));
    }
}