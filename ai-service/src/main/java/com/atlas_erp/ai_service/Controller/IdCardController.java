package com.atlas_erp.ai_service.Controller;

import com.atlas_erp.ai_service.Dto.Request.IdCardRequest;
import com.atlas_erp.ai_service.Dto.Response.ApiResponse;
import com.atlas_erp.ai_service.Dto.Response.IdCardResponse;
import com.atlas_erp.ai_service.Service.IdCardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * Controller for ID Card OCR processing
 */
@Slf4j
@RestController
@RequestMapping("/api/ai/ocr")
@RequiredArgsConstructor
public class IdCardController {

    private final IdCardService idCardService;

    /**
     * Process ID card images (front and back) and extract information
     * 
     * @param frontImage Front side image of ID Card (CCCD mặt trước)
     * @param backImage  Back side image of ID Card (CCCD mặt sau)
     * @return IdCardResponse with extracted data and formatted text
     */
    @PostMapping("/id-card")
    public ResponseEntity<ApiResponse<IdCardResponse>> processIdCard(
            @RequestParam("frontImage") MultipartFile frontImage,
            @RequestParam("backImage") MultipartFile backImage) {

        log.info("Received ID card processing request - front: {}, back: {}",
                frontImage.getOriginalFilename(), backImage.getOriginalFilename());

        // Validate file type
        if (!isValidImage(frontImage)) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Ảnh mặt trước phải là file ảnh (jpg, png, jpeg)"));
        }

        if (!isValidImage(backImage)) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Ảnh mặt sau phải là file ảnh (jpg, png, jpeg)"));
        }

        try {
            IdCardRequest request = IdCardRequest.builder()
                    .frontImage(frontImage)
                    .backImage(backImage)
                    .build();

            IdCardResponse response = idCardService.processIdCard(request);

            if (response.isSuccess()) {
                return ResponseEntity.ok(ApiResponse.success(response, "Xử lý thành công"));
            } else {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error(response.getMessage()));
            }

        } catch (Exception e) {
            log.error("Error processing ID card: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Lỗi xử lý: " + e.getMessage()));
        }
    }

    /**
     * Health check endpoint for OCR service
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<Map<String, Object>>> health() {
        Map<String, Object> healthData = Map.of(
                "status", "UP",
                "service", "ai-service-ocr",
                "features", "ID Card OCR, Image Preprocessing, Regex Parsing, LLM Formatting");

        return ResponseEntity.ok(ApiResponse.success(healthData, "Health check completed"));
    }

    /**
     * Check if file is a valid image
     */
    private boolean isValidImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        String contentType = file.getContentType();
        return contentType != null && (contentType.equals("image/jpeg") ||
                contentType.equals("image/jpg") ||
                contentType.equals("image/png") ||
                contentType.equals("image/bmp"));
    }
}
