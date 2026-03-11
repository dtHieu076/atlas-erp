package com.atlas_erp.ai_service.Service.Impl;

import com.atlas_erp.ai_service.Client.OcrClient;
import com.atlas_erp.ai_service.Client.OcrClientResponse;
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
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.util.Base64;

/**
 * OCR Service implementation
 * Primary: Python OCR Service (EasyOCR)
 * Fallback: OCR.space API
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OcrServiceImpl implements OcrService {

    private final ObjectMapper objectMapper;
    private final OcrClient ocrClient;

    @Value("${ocr.service.enabled:true}")
    private boolean ocrServiceEnabled;

    @Value("${ocr.space.api-key:}")
    private String ocrSpaceApiKey;

    @Value("${ocr.space.language:eng}")
    private String ocrSpaceLanguage;

    @Value("${ocr.space.url:https://api.ocr.space/parse/image}")
    private String ocrSpaceUrl;

    @Value("${ocr.fallback.enabled:true}")
    private boolean fallbackEnabled;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public String extractText(BufferedImage image) {
        log.info("Starting OCR extraction...");

        try {
            // Convert BufferedImage to byte array
            byte[] imageBytes = bufferedImageToBytes(image);
            return extractText(imageBytes);

        } catch (Exception e) {
            log.error("Error during OCR extraction: {}", e.getMessage(), e);
            throw new RuntimeException("OCR extraction failed: " + e.getMessage(), e);
        }
    }

    @Override
    public String extractText(byte[] imageBytes) {
        log.info("Starting OCR extraction from bytes...");

        // Try Python OCR Service first
        if (ocrServiceEnabled) {
            try {
                log.info("Attempting to use Python OCR Service...");
                String result = extractTextFromPythonService(imageBytes, "image.jpg");
                if (result != null && !result.isEmpty()) {
                    log.info("Python OCR Service succeeded, text length: {}", result.length());
                    return result;
                }
            } catch (Exception e) {
                log.warn("Python OCR Service failed: {}. Trying fallback...", e.getMessage());
            }
        }

        // Fallback to OCR.space if enabled
        if (fallbackEnabled) {
            try {
                log.info("Attempting to use OCR.space API as fallback...");
                String result = extractTextFromOcrSpace(imageBytes);
                if (result != null && !result.isEmpty()) {
                    log.info("OCR.space fallback succeeded, text length: {}", result.length());
                    return result;
                }
            } catch (Exception e) {
                log.error("OCR.space fallback also failed: {}", e.getMessage());
            }
        }

        throw new RuntimeException("All OCR methods failed. Please check OCR service availability.");
    }

    /**
     * Extract text using Python OCR Service
     */
    private String extractTextFromPythonService(byte[] imageBytes, String filename) {
        OcrClientResponse response = ocrClient.extractTextFromBytes(imageBytes, filename);

        if (response == null) {
            throw new RestClientException("OCR service returned null response");
        }

        if (!response.isSuccess()) {
            throw new RestClientException("OCR service returned error: " + response.getMessage());
        }

        return response.getText();
    }

    /**
     * Extract text from image bytes using OCR.space API (Fallback)
     */
    private String extractTextFromOcrSpace(byte[] imageBytes) {
        // Check if OCR.space API key is configured
        if (ocrSpaceApiKey == null || ocrSpaceApiKey.isBlank()) {
            log.warn("OCR.space API key not configured, skipping fallback");
            throw new RestClientException("OCR.space API key not configured");
        }

        try {
            // Convert image to base64
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);
            log.info("Base64 image length: {}", base64Image.length());

            // Prepare image data with proper prefix
            String imageData = "data:image/jpeg;base64," + base64Image;

            // Set up headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            // Use MultiValueMap for proper URL encoding
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("apikey", ocrSpaceApiKey.trim());
            params.add("language", ocrSpaceLanguage.trim());
            params.add("isOverlayRequired", "false");
            params.add("base64Image", imageData);

            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);

            // Send request
            log.info("Sending request to OCR.space API...");
            ResponseEntity<String> response = restTemplate.postForEntity(
                    ocrSpaceUrl,
                    requestEntity,
                    String.class);

            String responseBody = response.getBody();
            log.info("OCR.space response status: {}, body length: {}", response.getStatusCode(),
                    responseBody != null ? responseBody.length() : 0);

            // Parse response
            return parseOcrSpaceResponse(responseBody);

        } catch (Exception e) {
            log.error("Error calling OCR.space API: {}", e.getMessage(), e);
            throw new RestClientException("OCR.space API call failed: " + e.getMessage(), e);
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

    /**
     * Check if Python OCR service is available
     */
    public boolean isOcrServiceAvailable() {
        return ocrClient.isAvailable();
    }
}
