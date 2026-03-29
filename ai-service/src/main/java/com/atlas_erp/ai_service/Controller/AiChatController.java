package com.atlas_erp.ai_service.Controller;

import com.atlas_erp.ai_service.Dto.Request.ChatRequest;
import com.atlas_erp.ai_service.Dto.Response.ApiResponse;
import com.atlas_erp.ai_service.Service.ToolCallChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.Map;

/**
 * AI Chat Controller - Streaming SSE endpoint với Tool Calling
 */
@Slf4j
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiChatController {

    private final ToolCallChatService toolCallChatService;

    /**
     * Streaming Chat với Tool Calling - Server Sent Events for real-time typing
     * effect
     * Frontend subscribe to SSE stream để nhận response chunks theo thời gian thực
     */
    @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> chatStream(@RequestBody ChatRequest request) {
        log.info("Received streaming chat request: question={}", request.getQuestion());

        if (request.getQuestion() == null || request.getQuestion().trim().isEmpty()) {
            return Flux.just(ServerSentEvent.<String>builder()
                    .event("error")
                    .data("Question cannot be empty")
                    .build());
        }

        return toolCallChatService.chatWithToolsStream(request)
                .map(content -> ServerSentEvent.<String>builder()
                        .event("message")
                        .data(content)
                        .build())
                .doOnNext(sse -> log.debug("SSE event sent: {}",
                        sse.data().substring(0, Math.min(50, sse.data().length()))))
                .doOnComplete(() -> log.info("Streaming chat completed"));
    }

    /**
     * Health check endpoint (sync)
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<Map<String, Object>>> health() {
        Map<String, Object> healthData = Map.of(
                "status", "UP",
                "service", "ai-service",
                "mode", "streaming-tool-calling");

        return ResponseEntity.ok(ApiResponse.success(healthData, "Health check completed"));
    }
}
