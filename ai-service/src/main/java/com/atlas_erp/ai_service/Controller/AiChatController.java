package com.atlas_erp.ai_service.Controller;

import com.atlas_erp.ai_service.Dto.Request.ChatRequest;
import com.atlas_erp.ai_service.Dto.Response.ApiResponse;
import com.atlas_erp.ai_service.Dto.Response.ChatResponse;
import com.atlas_erp.ai_service.Service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiChatController {

    private final ChatService chatService;

    @PostMapping("/chat")
    public ResponseEntity<ApiResponse<ChatResponse>> chat(@RequestBody ChatRequest request) {
        log.info("Received chat request: question={}", request.getQuestion());

        if (request.getQuestion() == null || request.getQuestion().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Question cannot be empty"));
        }

        ChatResponse response = chatService.chat(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Chat processed successfully"));
    }

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<Map<String, Object>>> health() {
        boolean isHealthy = chatService.isHealthy();

        Map<String, Object> healthData = Map.of(
                "status", isHealthy ? "UP" : "DOWN",
                "service", "ai-service");

        return ResponseEntity.ok(ApiResponse.success(healthData, "Health check completed"));
    }
}
