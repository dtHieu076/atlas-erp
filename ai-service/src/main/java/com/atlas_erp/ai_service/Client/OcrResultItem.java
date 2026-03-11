package com.atlas_erp.ai_service.Client;

import lombok.Data;

import java.util.List;

/**
 * Individual OCR result item from Python OCR Service
 */
@Data
public class OcrResultItem {
    private String text;
    private Double confidence;
    private List<List<Double>> bbox;
}
