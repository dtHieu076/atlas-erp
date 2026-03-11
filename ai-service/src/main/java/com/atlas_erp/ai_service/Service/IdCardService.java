package com.atlas_erp.ai_service.Service;

import com.atlas_erp.ai_service.Dto.Request.IdCardRequest;
import com.atlas_erp.ai_service.Dto.Response.IdCardData;
import com.atlas_erp.ai_service.Dto.Response.IdCardResponse;

/**
 * Service interface for ID Card OCR processing
 * Orchestrates: OcrService (Python) -> ParserService -> LLM
 */
public interface IdCardService {

    /**
     * Process ID card images and extract information
     * 
     * @param request Contains front and back images
     * @return IdCardResponse with extracted data and formatted text
     */
    IdCardResponse processIdCard(IdCardRequest request);

    /**
     * Generate formatted text from structured data using LLM
     * 
     * @param data Parsed ID card data
     * @return Formatted text sentence
     */
    String generateFormattedText(IdCardData data);
}
