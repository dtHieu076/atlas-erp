package com.atlas_erp.ai_service.Service;

import com.atlas_erp.ai_service.Dto.Request.ChatRequest;
import com.atlas_erp.ai_service.Dto.Response.ChatResponse;

/**
 * Service xử lý chat với Tool Calling
 * Sử dụng Spring AI @Tool để LLM tự quyết định gọi tool nào
 */
public interface ToolCallChatService {

    /**
     * Chat với Tool Calling - LLM sẽ tự quyết định gọi tool
     * 
     * @param chatRequest câu hỏi từ user
     * @return câu trả lời từ AI
     */
    ChatResponse chatWithTools(ChatRequest chatRequest);
}
