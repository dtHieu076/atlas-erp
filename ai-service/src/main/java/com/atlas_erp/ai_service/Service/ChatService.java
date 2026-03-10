package com.atlas_erp.ai_service.Service;

import com.atlas_erp.ai_service.Dto.Request.ChatRequest;
import com.atlas_erp.ai_service.Dto.Response.ChatResponse;

public interface ChatService {

    /**
     * Process a chat request and return AI response
     * 
     * @param chatRequest the incoming chat request
     * @return ChatResponse containing the AI's answer
     */
    ChatResponse chat(ChatRequest chatRequest);

    /**
     * Check if the AI service is healthy
     * 
     * @return true if service is available
     */
    boolean isHealthy();
}
