package com.atlas_erp.ai_service.Dto.Request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for direct LLM vision processing of ID card image (base64)
 * Experimental endpoint - sends base64 image directly to vision LLM
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdCardBase64Request {

    /**
     * Base64 encoded image (full data URL format)
     * Example: data:image/jpeg;base64,/9j/4AAQSkZJRgABAQ...
     */
    private String imageBase64;
}
