package com.atlas_erp.ai_service.Service;

import com.atlas_erp.ai_service.Dto.Request.IdCardLlmRequest;
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

    /**
     * Experimental: Process front ID card image directly with vision LLM
     * Convert MultipartFile to base64 → send to LLM vision model
     *
     * @param request Front image only
     * @return IdCardResponse with LLM extracted fields matching YOLO classes
     */
    IdCardResponse processIdCardLlm(IdCardLlmRequest request);
}
