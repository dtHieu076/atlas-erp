package com.atlas_erp.ai_service.ContextBuilder;

import com.atlas_erp.ai_service.DataReader.DatabaseReader;
import com.atlas_erp.ai_service.QueryProcessor.IntentClassifier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Builds context for LLM from database data
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PromptContextBuilder {

    private final DatabaseReader databaseReader;
    private final IntentClassifier intentClassifier;

    /**
     * Build context string for the given question
     */
    public String buildContext(String question) {
        IntentClassifier.IntentResult intent = intentClassifier.classify(question);
        StringBuilder context = new StringBuilder();

        context.append("=== HR DATA CONTEXT ===\n\n");

        // Add relevant data based on intent
        switch (intent.intent()) {
            case "EMPLOYEE" -> {
                context.append("Active Employee Count: ");
                context.append(databaseReader.getActiveEmployeeCount());
                context.append("\n");
            }
            case "ATTENDANCE" -> {
                context.append("Today's Attendance:\n");
                context.append(databaseReader.getTodayAttendanceSummary());
                context.append("\n");
            }
            default -> {
                // General question - provide overview
                context.append("Active Employee Count: ");
                context.append(databaseReader.getActiveEmployeeCount());
                context.append("\n");
                context.append("Today's Attendance:\n");
                context.append(databaseReader.getTodayAttendanceSummary());
            }
        }

        context.append("\n=== END CONTEXT ===");
        log.info("Built context for intent: {}", intent.intent());

        return context.toString();
    }

    /**
     * Build full prompt with context
     */
    public String buildPrompt(String question, String context) {
        return """
                You are an HR assistant for Atlas ERP system.
                Answer the user's question based on the provided data context.
                If the context doesn't contain enough information, say so and provide a helpful general answer.
                Be concise and professional.

                Context:
                %s

                Question: %s

                Answer:""".formatted(context, question);
    }
}
