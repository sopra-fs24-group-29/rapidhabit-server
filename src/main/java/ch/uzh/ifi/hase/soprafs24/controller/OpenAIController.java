package ch.uzh.ifi.hase.soprafs24.controller;
import ch.uzh.ifi.hase.soprafs24.service.OpenAIService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class OpenAIController {
    private final OpenAIService openAIService;

    public OpenAIController(OpenAIService openAIService) {
        this.openAIService = openAIService;
    }

    @GetMapping("/coach")
    public ResponseEntity<String> chatWithCoach(@RequestHeader("Prompt") String prompt) {
        try {
            String response = openAIService.sendPrompt(prompt);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            // Log the exception details here or handle it according to your log strategy
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to get response from OpenAI: " + e.getMessage());
        }
    }

    @GetMapping("/pulsecheck")
    public ResponseEntity<String> requestPulseCheck() {
        try {
            String response = openAIService.generatePulseCheckContentChatCompletion(40, 1);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            // Log the exception details here or handle it according to your log strategy
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to get response from OpenAI: " + e.getMessage());
        }
    }
}

