package com.atlas_erp.ai_service.Dto.Request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

/**
 * Request for LLM direct processing (single front image only)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdCardLlmRequest {
    private MultipartFile frontImage;
    private String imageBase64;
}
