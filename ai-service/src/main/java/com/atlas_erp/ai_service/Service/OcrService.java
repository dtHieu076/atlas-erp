package com.atlas_erp.ai_service.Service;

import java.awt.image.BufferedImage;

/**
 * Service interface for OCR processing
 */
public interface OcrService {

    /**
     * Extract text from image using OCR
     * 
     * @param image BufferedImage to process
     * @return Extracted text
     */
    String extractText(BufferedImage image);

    /**
     * Extract text from image bytes
     * 
     * @param imageBytes Image byte array
     * @return Extracted text
     */
    String extractText(byte[] imageBytes);
}
