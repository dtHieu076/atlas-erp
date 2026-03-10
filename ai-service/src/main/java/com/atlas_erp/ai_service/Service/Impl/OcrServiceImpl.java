package com.atlas_erp.ai_service.Service.Impl;

import com.atlas_erp.ai_service.Service.OcrService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.util.Base64;

/**
 * OCR Service implementation using OCR.space API
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OcrServiceImpl implements OcrService {

    private final ObjectMapper objectMapper;

    @Value("${ocr.space.api-key:}")
    private String ocrSpaceApiKey;

    @Value("${ocr.space.language:eng}")
    private String ocrSpaceLanguage;

    @Value("${ocr.space.url:https://api.ocr.space/parse/image}")
    private String ocrSpaceUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public String extractText(BufferedImage image) {
        log.info("Starting OCR extraction using OCR.space API...");

        try {
            // Convert BufferedImage to byte array
            byte[] imageBytes = bufferedImageToBytes(image);
            return extractTextFromBytes(imageBytes);

        } catch (Exception e) {
            log.error("Error during OCR extraction: {}", e.getMessage(), e);
            throw new RuntimeException("OCR extraction failed: " + e.getMessage(), e);
        }
    }

    @Override
    public String extractText(byte[] imageBytes) {
        log.info("Starting OCR extraction from bytes using OCR.space API...");
        return extractTextFromBytes(imageBytes);
    }

    /**
     * Extract text from image bytes using OCR.space API
     * Using base64 encoding for more reliable upload
     */
    private String extractTextFromBytes(byte[] imageBytes) {
        try {
            // Convert image to base64
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);
            log.info("Base64 image length: {}", base64Image.length());

            // Prepare image data with proper prefix
            String imageData = "data:image/jpeg;base64," + base64Image;

            // Set up headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            // Use MultiValueMap for proper URL encoding (Spring handles encoding
            // automatically)
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("apikey", ocrSpaceApiKey.trim());
            params.add("language", ocrSpaceLanguage.trim()); // .trim() to remove any whitespace issues
            params.add("isOverlayRequired", "false");
            params.add("base64Image", imageData);

            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);

            // Send request
            log.info("Sending request to OCR.space API using MultiValueMap...");
            ResponseEntity<String> response = restTemplate.postForEntity(
                    ocrSpaceUrl,
                    requestEntity,
                    String.class);

            String responseBody = response.getBody();
            log.info("OCR.space response status: {}, body length: {}", response.getStatusCode(),
                    responseBody != null ? responseBody.length() : 0);
            log.debug("OCR.space response: {}", responseBody);

            // Parse response
            return parseOcrSpaceResponse(responseBody);

        } catch (Exception e) {
            log.error("Error calling OCR.space API: {}", e.getMessage(), e);
            throw new RuntimeException("OCR.space API call failed: " + e.getMessage(), e);
        }
    }

    /**
     * Parse OCR.space response
     */
    private String parseOcrSpaceResponse(String responseBody) {
        try {
            JsonNode jsonResponse = objectMapper.readTree(responseBody);

            // Check for errors
            if (jsonResponse.has("OCRExitCode")) {
                int exitCode = jsonResponse.get("OCRExitCode").asInt();
                if (exitCode != 1) {
                    String errorMsg = jsonResponse.has("ErrorMessage")
                            ? jsonResponse.get("ErrorMessage").toString()
                            : "Unknown error";
                    log.error("OCR.space error: {}", errorMsg);
                    throw new RuntimeException("OCR.space error: " + errorMsg);
                }
            }

            // Extract text from ParsedResults
            JsonNode parsedResults = jsonResponse.get("ParsedResults");
            if (parsedResults == null || parsedResults.isEmpty()) {
                log.warn("No parsing results from OCR.space");
                return "";
            }

            StringBuilder text = new StringBuilder();
            for (JsonNode result : parsedResults) {
                String extractedText = result.get("ParsedText").asText();
                text.append(extractedText);
                text.append("\n");
            }

            String result = text.toString().trim();
            log.info("OCR extraction completed, text length: {}", result.length());
            return result;

        } catch (Exception e) {
            log.error("Error parsing OCR.space response: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to parse OCR.space response: " + e.getMessage(), e);
        }
    }

    /**
     * Convert BufferedImage to byte array
     */
    private byte[] bufferedImageToBytes(BufferedImage image) {
        try {
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            javax.imageio.ImageIO.write(image, "jpg", baos);
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Error converting BufferedImage to bytes: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to convert image to bytes", e);
        }
    }
}
