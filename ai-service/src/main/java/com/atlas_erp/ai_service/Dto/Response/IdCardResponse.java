package com.atlas_erp.ai_service.Dto.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for ID Card OCR API
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdCardResponse {

    private boolean success;
    private String message;
    private IdCardData data;
}
