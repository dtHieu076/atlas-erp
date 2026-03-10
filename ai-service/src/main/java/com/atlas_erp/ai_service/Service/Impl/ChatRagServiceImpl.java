package com.atlas_erp.ai_service.Service.Impl;

import com.atlas_erp.ai_service.ContextBuilder.PromptContextBuilder;
import com.atlas_erp.ai_service.Dto.Request.ChatRequest;
import com.atlas_erp.ai_service.Dto.Response.ChatResponse;
import com.atlas_erp.ai_service.QueryProcessor.IntentClassifier;
import com.atlas_erp.ai_service.Service.ChatRagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Chat Service Implementation with RAG support
 */
@Slf4j
@Service
public class ChatRagServiceImpl implements ChatRagService {

    private final ChatClient chatClient;
    private final PromptContextBuilder promptContextBuilder;
    private final IntentClassifier intentClassifier;
    private final String defaultModel;

    public ChatRagServiceImpl(
            ChatModel chatModel,
            PromptContextBuilder promptContextBuilder,
            IntentClassifier intentClassifier,
            @Value("${spring.ai.openai.chat.options.model:stepfun/step-3.5-flash:free}") String defaultModel) {
        this.chatClient = ChatClient.builder(chatModel).build();
        this.promptContextBuilder = promptContextBuilder;
        this.intentClassifier = intentClassifier;
        this.defaultModel = defaultModel;
        log.info("ChatRagService initialized with model: {}", defaultModel);
    }

    @Override
    public ChatResponse chatWithDatabase(ChatRequest chatRequest) {
        log.info("Processing RAG chat request: {}", chatRequest.getQuestion());

        try {
            // Classify intent
            IntentClassifier.IntentResult intent = intentClassifier.classify(chatRequest.getQuestion());

            // Build context from database
            String context = promptContextBuilder.buildContext(chatRequest.getQuestion());

            // Build full prompt
            String fullPrompt = promptContextBuilder.buildPrompt(chatRequest.getQuestion(), context);

            // Call LLM
            String aiResponse = chatClient.prompt()
                    .user(fullPrompt)
                    .call()
                    .content();

            log.info("RAG chat response generated successfully for intent: {}", intent.intent());

            return ChatResponse.builder()
                    .answer(aiResponse)
                    .sessionId(chatRequest.getSessionId() != null ? chatRequest.getSessionId()
                            : UUID.randomUUID().toString())
                    .timestamp(System.currentTimeMillis())
                    .model(defaultModel)
                    .build();

        } catch (Exception e) {
            log.error("Error processing RAG chat request: {}", e.getMessage(), e);
            // Fallback to general chat
            return chatGeneral(chatRequest);
        }
    }

    @Override
    public ChatResponse chatGeneral(ChatRequest chatRequest) {
        log.info("Processing general chat request: {}", chatRequest.getQuestion());

        try {
            String userPrompt = promptContextBuilder.buildPrompt(
                    chatRequest.getQuestion(),
                    "You are a helpful HR assistant for Atlas ERP system.");

            String aiResponse = chatClient.prompt()
                    .user(userPrompt)
                    .call()
                    .content();

            return ChatResponse.builder()
                    .answer(aiResponse)
                    .sessionId(chatRequest.getSessionId() != null ? chatRequest.getSessionId()
                            : UUID.randomUUID().toString())
                    .timestamp(System.currentTimeMillis())
                    .model(defaultModel)
                    .build();

        } catch (Exception e) {
            log.error("Error processing general chat request: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to process chat request: " + e.getMessage(), e);
        }
    }
}
