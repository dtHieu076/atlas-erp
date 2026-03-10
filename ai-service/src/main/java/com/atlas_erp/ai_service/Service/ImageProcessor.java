package com.atlas_erp.ai_service.Service;

import java.awt.image.BufferedImage;

/**
 * Service interface for image preprocessing
 */
public interface ImageProcessor {

    /**
     * Preprocess image for better OCR results
     * 
     * @param inputImage Input BufferedImage
     * @return Preprocessed BufferedImage
     */
    BufferedImage preprocess(BufferedImage inputImage);

    /**
     * Convert multipart file to BufferedImage
     * 
     * @param imageBytes Image byte array
     * @return BufferedImage
     */
    BufferedImage bytesToBufferedImage(byte[] imageBytes);
}
