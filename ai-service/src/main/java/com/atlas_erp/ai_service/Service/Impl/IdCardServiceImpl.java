package com.atlas_erp.ai_service.Service.Impl;

import com.atlas_erp.ai_service.Dto.Request.IdCardRequest;
import com.atlas_erp.ai_service.Dto.Response.IdCardData;
import com.atlas_erp.ai_service.Dto.Response.IdCardResponse;
import com.atlas_erp.ai_service.Service.IdCardService;
import com.atlas_erp.ai_service.Service.ImageProcessor;
import com.atlas_erp.ai_service.Service.OcrService;
import com.atlas_erp.ai_service.Service.ParserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

/**
 * ID Card Service Implementation
 * Orchestrates: ImageProcessor -> OcrService -> ParserService -> LLM
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IdCardServiceImpl implements IdCardService {

    private final ImageProcessor imageProcessor;
    private final OcrService ocrService;
    private final ParserService parserService;
    private final ObjectMapper objectMapper;

    @Value("${spring.ai.openai.chat.options.model:stepfun/step-3.5-flash:free}")
    private String defaultModel;

    @Value("${spring.ai.openai.base-url:https://openrouter.ai/api}")
    private String baseUrl;

    @Value("${spring.ai.openai.api-key:}")
    private String apiKey;

    private final WebClient webClient = WebClient.builder()
            .baseUrl(baseUrl)
            .defaultHeader("Authorization", "Bearer " + apiKey)
            .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader("HTTP-Referer", "http://localhost:8080")
            .defaultHeader("X-Title", "AI-Service")
            .build();

    @Override
    public IdCardResponse processIdCard(IdCardRequest request) {
        log.info("Starting ID card processing...");

        try {
            // Validate input
            if (request.getFrontImage() == null || request.getFrontImage().isEmpty()) {
                return IdCardResponse.builder()
                        .success(false)
                        .message("Vui lòng tải lên ảnh mặt trước CCCD")
                        .build();
            }

            if (request.getBackImage() == null || request.getBackImage().isEmpty()) {
                return IdCardResponse.builder()
                        .success(false)
                        .message("Vui lòng tải lên ảnh mặt sau CCCD")
                        .build();
            }

            // Step 1: Process front image
            log.info("Processing front image...");
            byte[] frontBytes = request.getFrontImage().getBytes();
            String frontText = ocrService.extractText(frontBytes);
            log.debug("Front OCR text: {}", frontText);

            // Step 2: Process back image
            log.info("Processing back image...");
            byte[] backBytes = request.getBackImage().getBytes();
            String backText = ocrService.extractText(backBytes);
            log.debug("Back OCR text: {}", backText);

            // Step 3: Parse front card data
            IdCardData frontData = parserService.parseFrontCard(frontText);

            // Step 4: Parse back card data and merge
            IdCardData backData = parserService.parseBackCard(backText);
            frontData.setIssueDate(backData.getIssueDate());
            frontData.setIssuePlace(backData.getIssuePlace());
            frontData.setEthnicity(backData.getEthnicity());
            frontData.setReligion(backData.getReligion());
            frontData.setRawOcrBack(backData.getRawOcrBack());

            // Step 5: Generate formatted text using LLM
            String formattedText = generateFormattedText(frontData);
            frontData.setFormattedText(formattedText);

            log.info("ID card processing completed successfully");

            return IdCardResponse.builder()
                    .success(true)
                    .message("Xử lý thành công")
                    .data(frontData)
                    .build();

        } catch (Exception e) {
            log.error("Error processing ID card: {}", e.getMessage(), e);
            return IdCardResponse.builder()
                    .success(false)
                    .message("Lỗi xử lý: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public String generateFormattedText(IdCardData data) {
        log.info("Generating formatted text using LLM...");

        try {
            String prompt = buildPrompt(data);

            List<Map<String, Object>> messages = new ArrayList<>();

            // System message
            Map<String, Object> systemMsg = new HashMap<>();
            systemMsg.put("role", "system");
            systemMsg.put("content",
                    "Bạn là trợ lý AI chuyên format thông tin căn cước công dân Việt Nam. Hãy tạo câu văn hoàn chỉnh bằng tiếng Việt, bắt đầu bằng 'Thông tin của nhân viên như sau: ...' Chỉ generate câu văn, không thêm giải thích.");
            messages.add(systemMsg);

            // User message
            Map<String, Object> userMsg = new HashMap<>();
            userMsg.put("role", "user");
            userMsg.put("content", prompt);
            messages.add(userMsg);

            // Build request
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", defaultModel);
            requestBody.put("messages", messages);
            requestBody.put("temperature", 0.7);

            // Send request
            String response = webClient.post()
                    .uri("/v1/chat/completions")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            // Parse response
            JsonNode jsonResponse = objectMapper.readTree(response);
            String result = jsonResponse.get("choices").get(0).get("message").get("content").asText();

            log.info("Formatted text generated: {}", result);
            return result;

        } catch (Exception e) {
            log.error("Error generating formatted text: {}", e.getMessage(), e);
            // Fallback to manual formatting
            return buildManualFormattedText(data);
        }
    }

    /**
     * Build prompt for LLM
     */
    private String buildPrompt(IdCardData data) {
        return String.format("""
                Dưới đây là dữ liệu đã được parse từ CCCD:

                - Họ tên: %s
                - Ngày sinh: %s
                - Giới tính: %s
                - Số CCCD: %s
                - Quê quán: %s
                - Nơi thường trú: %s
                - Ngày cấp: %s
                - Nơi cấp: %s
                - Dân tộc: %s
                - Tôn giáo: %s

                Hãy tạo câu văn hoàn chỉnh bằng tiếng Việt, bắt đầu bằng:
                "Thông tin của nhân viên như sau: ..."

                Chỉ generate câu văn, không thêm giải thích.
                """,
                nullToEmpty(data.getFullName()),
                nullToEmpty(data.getDateOfBirth()),
                nullToEmpty(data.getGender()),
                nullToEmpty(data.getIdNumber()),
                nullToEmpty(data.getPlaceOfOrigin()),
                nullToEmpty(data.getAddress()),
                nullToEmpty(data.getIssueDate()),
                nullToEmpty(data.getIssuePlace()),
                nullToEmpty(data.getEthnicity()),
                nullToEmpty(data.getReligion()));
    }

    /**
     * Fallback manual formatting if LLM fails
     */
    private String buildManualFormattedText(IdCardData data) {
        StringBuilder sb = new StringBuilder();
        sb.append("Thông tin của nhân viên như sau: ");

        List<String> fields = new ArrayList<>();
        if (data.getFullName() != null)
            fields.add("Họ tên: " + data.getFullName());
        if (data.getDateOfBirth() != null)
            fields.add("sinh ngày " + data.getDateOfBirth());
        if (data.getGender() != null)
            fields.add("giới tính " + data.getGender());
        if (data.getIdNumber() != null)
            fields.add("số CCCD " + data.getIdNumber());
        if (data.getPlaceOfOrigin() != null)
            fields.add("quê quán " + data.getPlaceOfOrigin());
        if (data.getAddress() != null)
            fields.add("nơi thường trú " + data.getAddress());
        if (data.getIssueDate() != null)
            fields.add("ngày cấp " + data.getIssueDate());
        if (data.getIssuePlace() != null)
            fields.add("nơi cấp " + data.getIssuePlace());
        if (data.getEthnicity() != null)
            fields.add("dân tộc " + data.getEthnicity());
        if (data.getReligion() != null)
            fields.add("tôn giáo " + data.getReligion());

        sb.append(String.join(", ", fields));
        sb.append(".");

        return sb.toString();
    }

    private String nullToEmpty(String value) {
        return value != null ? value : "Không xác định";
    }
}
