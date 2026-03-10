package com.atlas_erp.ai_service.Service.Impl;

import com.atlas_erp.ai_service.Dto.Request.ChatRequest;
import com.atlas_erp.ai_service.Dto.Response.ChatResponse;
import com.atlas_erp.ai_service.Service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class ChatServiceImpl implements ChatService {

    private final ChatClient chatClient;
    private final String defaultModel;

    public ChatServiceImpl(ChatModel chatModel,
            @Value("${spring.ai.openai.chat.options.model:stepfun/step-3.5-flash:free}") String defaultModel) {
        this.chatClient = ChatClient.builder(chatModel).build();
        this.defaultModel = defaultModel;
        log.info("ChatService initialized with model: {}", defaultModel);
    }

    @Override
    public ChatResponse chat(ChatRequest chatRequest) {
        log.info("Processing chat request: question={}, sessionId={}",
                chatRequest.getQuestion(), chatRequest.getSessionId());

        try {
            // Build the prompt with context if provided
            String userPrompt = buildUserPrompt(chatRequest);

            // Call AI and get response
            String aiResponse = chatClient.prompt()
                    .user(userPrompt)
                    .call()
                    .content();

            log.info("AI response received successfully");

            return ChatResponse.builder()
                    .answer(aiResponse)
                    .sessionId(chatRequest.getSessionId() != null ? chatRequest.getSessionId()
                            : UUID.randomUUID().toString())
                    .timestamp(System.currentTimeMillis())
                    .model(defaultModel)
                    .build();

        } catch (Exception e) {
            log.error("Error processing chat request: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to process chat request: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean isHealthy() {
        try {
            // Simple health check by making a minimal request
            String response = chatClient.prompt()
                    .user("ping")
                    .call()
                    .content();
            return response != null && !response.isEmpty();
        } catch (Exception e) {
            log.warn("Health check failed: {}", e.getMessage());
            return false;
        }
    }

    private String buildUserPrompt(ChatRequest chatRequest) {
        StringBuilder prompt = new StringBuilder();

        if (chatRequest.getContext() != null && !chatRequest.getContext().isEmpty()) {
            prompt.append("Context: ").append(chatRequest.getContext()).append("\n\n");
        }

        prompt.append("Question: ").append(chatRequest.getQuestion());

        return prompt.toString();
    }
}
