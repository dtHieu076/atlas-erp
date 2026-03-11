package com.atlas_erp.ai_service.Client;

import lombok.Data;

import java.util.List;

/**
 * Response DTO from Python OCR Service
 */
@Data
public class OcrClientResponse {
    private boolean success;
    private String text;
    private List<OcrResultItem> results;
    private String message;
}
