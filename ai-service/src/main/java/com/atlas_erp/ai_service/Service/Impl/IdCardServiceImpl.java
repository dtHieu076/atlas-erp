package com.atlas_erp.ai_service.Service.Impl;

import com.atlas_erp.ai_service.Dto.Request.IdCardRequest;
import com.atlas_erp.ai_service.Dto.Response.IdCardData;
import com.atlas_erp.ai_service.Dto.Response.IdCardResponse;
import com.atlas_erp.ai_service.Service.IdCardService;
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
            // NEW: Use LLM to parse RAW OCR text directly and get all fields
            IdCardData parsedData = parseWithLLMFromRawOcr(frontText, backText);
            frontData.setFormattedText(parsedData.getFormattedText());

            // Also update other fields if LLM parsed successfully
            if (parsedData.getFullName() != null)
                frontData.setFullName(parsedData.getFullName());
            if (parsedData.getPlaceOfOrigin() != null)
                frontData.setPlaceOfOrigin(parsedData.getPlaceOfOrigin());
            if (parsedData.getHometown() != null)
                frontData.setHometown(parsedData.getHometown());
            if (parsedData.getAddress() != null)
                frontData.setAddress(parsedData.getAddress());
            if (parsedData.getPermanentAddress() != null)
                frontData.setPermanentAddress(parsedData.getPermanentAddress());

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

    /**
     * Use LLM to parse RAW OCR text directly
     * This extracts all fields from raw OCR text and cleans them
     */
    private IdCardData parseWithLLMFromRawOcr(String frontText, String backText) {
        log.info("Parsing ID card data using LLM from RAW OCR...");

        try {
            List<Map<String, Object>> messages = new ArrayList<>();

            // System message - instruct LLM to parse and return JSON
            Map<String, Object> systemMsg = new HashMap<>();
            systemMsg.put("role", "system");
            systemMsg.put("content",
                    "Ban la AI chuyen xu ly du lieu OCR tu Can Cuoc Cong Dan Viet Nam.\n" +
                            "Nhiem vu cua ban:\n" +
                            "1. Doc du lieu OCR ben duoi (ca mat truoc va mat sau).\n" +
                            "2. Trich xuat va lam sach cac truong thong tin:\n" +
                            "   - Loai bo nhan song ngu (Full name, Date of birth, Place of origin...)\n" +
                            "   - Loai bo text thua (SOCIALIST REPUBLIC, CAN CUOC CONG DAN...)\n" +
                            "   - Sua loi OCR pho bien\n" +
                            "3. Chuan hoa:\n" +
                            "   - Ho ten: viet hoa chu cai dau moi tu\n" +
                            "   - Gioi tinh: chi 'Nam' hoac 'Nu'\n" +
                            "   - Dia chi: dung dia danh Viet Nam\n" +
                            "4. Tra ve JSON chuan (bao gom formattedText):\n" +
                            "{\"fullName\":\"\",\"dateOfBirth\":\"\",\"gender\":\"\",\"idNumber\":\"\",\"placeOfOrigin\":\"\",\"address\":\"\",\"issueDate\":\"\",\"issuePlace\":\"\",\"ethnicity\":\"\",\"religion\":\"\",\"formattedText\":\"\"}\n"
                            +
                            "formattedText co dang: 'Thong tin cua nhan vien nhu sau: <fullName>, sinh ngay <dateOfBirth>, gioi tinh <gender>, so CCCD <idNumber>, que quan <placeOfOrigin>, thuong tru tai <address>.'\n"
                            +
                            "Chi tra ve JSON, khong giai thich.");
            messages.add(systemMsg);

            // User message with RAW OCR text
            Map<String, Object> userMsg = new HashMap<>();
            userMsg.put("role", "user");
            userMsg.put("content", "MAT TRUOC CCCD:\n" + frontText + "\n\nMAT SAU CCCD:\n" + backText);
            messages.add(userMsg);

            // Build request
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", defaultModel);
            requestBody.put("messages", messages);
            requestBody.put("temperature", 0.3);

            // Send request
            String response = webClient.post()
                    .uri("/v1/chat/completions")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            // Parse JSON response from LLM
            JsonNode jsonResponse = objectMapper.readTree(response);
            String content = jsonResponse.get("choices").get(0).get("message").get("content").asText();

            log.debug("LLM raw response: {}", content);

            // Clean and parse JSON response
            String jsonStr = cleanJsonResponse(content);
            JsonNode dataNode = objectMapper.readTree(jsonStr);

            // Map to IdCardData
            IdCardData data = IdCardData.builder()
                    .fullName(getTextOrNull(dataNode, "fullName"))
                    .dateOfBirth(getTextOrNull(dataNode, "dateOfBirth"))
                    .gender(getTextOrNull(dataNode, "gender"))
                    .idNumber(getTextOrNull(dataNode, "idNumber"))
                    .placeOfOrigin(getTextOrNull(dataNode, "placeOfOrigin"))
                    .hometown(getTextOrNull(dataNode, "placeOfOrigin")) // Map from placeOfOrigin
                    .address(getTextOrNull(dataNode, "address"))
                    .permanentAddress(getTextOrNull(dataNode, "address")) // Map from address
                    .issueDate(getTextOrNull(dataNode, "issueDate"))
                    .issuePlace(getTextOrNull(dataNode, "issuePlace"))
                    .ethnicity(getTextOrNull(dataNode, "ethnicity"))
                    .religion(getTextOrNull(dataNode, "religion"))
                    .formattedText(getTextOrNull(dataNode, "formattedText"))
                    .build();

            log.info("Parsed data from LLM: {}", data);
            return data;

        } catch (Exception e) {
            log.error("Error parsing with LLM: {}", e.getMessage());
            // Return empty data with nulls
            return IdCardData.builder().build();
        }
    }

    /**
     * Clean JSON response from LLM
     */
    private String cleanJsonResponse(String content) {
        content = content.replaceAll("```json", "").replaceAll("```", "");
        return content.trim();
    }

    /**
     * Get text value or null from JSON node
     */
    private String getTextOrNull(JsonNode node, String field) {
        JsonNode fieldNode = node.get(field);
        if (fieldNode == null || fieldNode.isNull() || fieldNode.asText().isEmpty()) {
            return null;
        }
        return fieldNode.asText();
    }

    /**
     * Convert null to empty string
     */
    private String nullToEmpty(String value) {
        return value != null ? value : "";
    }
}
