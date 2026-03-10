package com.atlas_erp.ai_service.Controller;

import com.atlas_erp.ai_service.Dto.Request.ChatRequest;
import com.atlas_erp.ai_service.Dto.Response.ApiResponse;
import com.atlas_erp.ai_service.Dto.Response.ChatResponse;
import com.atlas_erp.ai_service.Service.ChatRagService;
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
    private final ChatRagService chatRagService;

    /**
     * General chat - LLM only (no database)
     */
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

    /**
     * Chat with database context (RAG)
     */
    @PostMapping("/chat/database")
    public ResponseEntity<ApiResponse<ChatResponse>> chatWithDatabase(@RequestBody ChatRequest request) {
        log.info("Received database chat request: question={}", request.getQuestion());

        if (request.getQuestion() == null || request.getQuestion().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Question cannot be empty"));
        }

        ChatResponse response = chatRagService.chatWithDatabase(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Database chat processed successfully"));
    }

    /**
     * Toggle between database mode and general mode
     */
    @PostMapping("/chat/auto")
    public ResponseEntity<ApiResponse<ChatResponse>> chatAuto(@RequestBody ChatRequest request) {
        log.info("Received auto chat request: question={}", request.getQuestion());

        if (request.getQuestion() == null || request.getQuestion().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Question cannot be empty"));
        }

        // Try database mode first
        try {
            ChatResponse response = chatRagService.chatWithDatabase(request);
            return ResponseEntity.ok(ApiResponse.success(response, "Chat processed with database context"));
        } catch (Exception e) {
            log.warn("Database mode failed, falling back to general mode: {}", e.getMessage());
            ChatResponse response = chatRagService.chatGeneral(request);
            return ResponseEntity.ok(ApiResponse.success(response, "Chat processed with general mode"));
        }
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
