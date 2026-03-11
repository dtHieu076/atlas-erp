package com.atlas_erp.ai_service.Client;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Client for communicating with Python OCR Service
 */
@Slf4j
@Component
public class OcrClient {

    private final WebClient webClient;

    @Value("${ocr.service.url:http://localhost:8000}")
    private String ocrServiceUrl;

    @Value("${ocr.service.timeout:120000}")
    private long timeout;

    @Value("${ocr.service.enabled:true}")
    private boolean enabled;

    public OcrClient() {
        this.webClient = WebClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
                .build();
    }

    /**
     * Extract text from image file using Python OCR Service
     *
     * @param file MultipartFile image
     * @return OcrClientResponse with extracted text
     */
    public OcrClientResponse extractText(MultipartFile file) {
        log.info("Calling Python OCR Service at {}", ocrServiceUrl);

        if (!enabled) {
            throw new RestClientException("OCR service is disabled");
        }

        try {
            // Create multipart request
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

            // Convert MultipartFile to ByteArrayResource
            ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };

            body.add("file", resource);

            Mono<OcrClientResponse> responseMono = webClient.post()
                    .uri(ocrServiceUrl + "/api/ocr/extract-text")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(OcrClientResponse.class);

            OcrClientResponse response = responseMono.block();

            log.info("OCR Service response: success={}", response != null ? response.isSuccess() : false);
            return response;

        } catch (Exception e) {
            log.error("Error calling OCR service: {}", e.getMessage(), e);
            throw new RestClientException("Failed to call OCR service: " + e.getMessage(), e);
        }
    }

    /**
     * Extract text from byte array
     *
     * @param imageBytes Image bytes
     * @param filename   Original filename
     * @return OcrClientResponse with extracted text
     */
    public OcrClientResponse extractTextFromBytes(byte[] imageBytes, String filename) {
        log.info("Calling Python OCR Service with bytes, filename: {}", filename);

        if (!enabled) {
            throw new RestClientException("OCR service is disabled");
        }

        try {
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

            ByteArrayResource resource = new ByteArrayResource(imageBytes) {
                @Override
                public String getFilename() {
                    return filename;
                }
            };

            body.add("file", resource);

            Mono<OcrClientResponse> responseMono = webClient.post()
                    .uri(ocrServiceUrl + "/api/ocr/extract-text")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(OcrClientResponse.class);

            return responseMono.block();

        } catch (Exception e) {
            log.error("Error calling OCR service: {}", e.getMessage(), e);
            throw new RestClientException("Failed to call OCR service: " + e.getMessage(), e);
        }
    }

    /**
     * Check if OCR service is available
     *
     * @return true if service is available
     */
    public boolean isAvailable() {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = webClient.get()
                    .uri(ocrServiceUrl + "/health")
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            return response != null && "UP".equals(response.get("status"));
        } catch (Exception e) {
            log.warn("OCR service health check failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Get OCR service URL
     */
    public String getOcrServiceUrl() {
        return ocrServiceUrl;
    }

    /**
     * Check if OCR service is enabled
     */
    public boolean isEnabled() {
        return enabled;
    }
}
