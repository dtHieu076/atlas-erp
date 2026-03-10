package com.atlas_erp.ai_service.Service;

import com.atlas_erp.ai_service.Dto.Request.ChatRequest;
import com.atlas_erp.ai_service.Dto.Response.ChatResponse;

/**
 * Chat Service with RAG (Retrieval-Augmented Generation) support
 */
public interface ChatRagService {

    /**
     * Chat with database context
     */
    ChatResponse chatWithDatabase(ChatRequest chatRequest);

    /**
     * Chat with general LLM (no database)
     */
    ChatResponse chatGeneral(ChatRequest chatRequest);
}
