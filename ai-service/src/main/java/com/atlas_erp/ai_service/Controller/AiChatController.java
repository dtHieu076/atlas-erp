package com.atlas_erp.ai_service.Controller;

import com.atlas_erp.ai_service.Dto.Request.ChatRequest;
import com.atlas_erp.ai_service.Dto.Response.ApiResponse;
import com.atlas_erp.ai_service.Dto.Response.ChatResponse;
import com.atlas_erp.ai_service.Service.ToolCallChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * AI Chat Controller - Chỉ sử dụng Tool Calling endpoint
 */
@Slf4j
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiChatController {

    private final ToolCallChatService toolCallChatService;

    /**
     * Chat với Tool Calling - LLM tự quyết định gọi tool nào
     * Đây là endpoint duy nhất để trả lời câu hỏi với dữ liệu database
     */
    @PostMapping("/chat")
    public ResponseEntity<ApiResponse<ChatResponse>> chat(@RequestBody ChatRequest request) {
        log.info("Received chat request: question={}", request.getQuestion());

        if (request.getQuestion() == null || request.getQuestion().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Question cannot be empty"));
        }

        try {
            ChatResponse response = toolCallChatService.chatWithTools(request);
            return ResponseEntity.ok(ApiResponse.success(response, "Chat processed successfully"));
        } catch (Exception e) {
            log.error("Chat failed: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Chat failed: " + e.getMessage()));
        }
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<Map<String, Object>>> health() {
        Map<String, Object> healthData = Map.of(
                "status", "UP",
                "service", "ai-service",
                "mode", "tool-calling");

        return ResponseEntity.ok(ApiResponse.success(healthData, "Health check completed"));
    }
}
