package com.atlas_erp.ai_service.Service;

import com.atlas_erp.ai_service.Dto.Response.IdCardData;

/**
 * Service interface for parsing ID Card data using regex patterns
 */
public interface ParserService {

    /**
     * Parse front side of ID Card
     * 
     * @param rawText Raw OCR text from front image
     * @return Parsed IdCardData
     */
    IdCardData parseFrontCard(String rawText);

    /**
     * Parse back side of ID Card
     * 
     * @param rawText Raw OCR text from back image
     * @return Parsed IdCardData with additional fields
     */
    IdCardData parseBackCard(String rawText);
}
