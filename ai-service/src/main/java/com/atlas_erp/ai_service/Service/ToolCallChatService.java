package com.atlas_erp.ai_service.Service;

import reactor.core.publisher.Flux;

import com.atlas_erp.ai_service.Dto.Request.ChatRequest;

/**
 * Service xử lý chat với Tool Calling (Streaming)
 * Sử dụng Spring AI Tool Calling với Gemini và Flux streaming
 */
public interface ToolCallChatService {

    /**
     * Chat với Tool Calling - Streaming response for real-time UI
     * 
     * @param chatRequest câu hỏi từ user
     * @return stream of response chunks
     */
    Flux<String> chatWithToolsStream(ChatRequest chatRequest);
}
