package com.atlas_erp.ai_service.Dto.Request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

/**
 * Request DTO for ID Card OCR processing
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdCardRequest {

    /**
     * Front side image of ID Card (CCCD mặt trước)
     */
    private MultipartFile frontImage;

    /**
     * Back side image of ID Card (CCCD mặt sau)
     */
    private MultipartFile backImage;
}
