package com.atlas_erp.ai_service.Service.Impl;

import com.atlas_erp.ai_service.Service.ImageProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Image preprocessing implementation using Java built-in APIs
 * 
 * Pipeline:
 * 1. Convert to grayscale
 * 2. Apply contrast enhancement
 * 3. Binarize (threshold)
 */
@Slf4j
@Service
public class ImageProcessorImpl implements ImageProcessor {

    @Override
    public BufferedImage preprocess(BufferedImage inputImage) {
        log.info("Starting image preprocessing...");

        try {
            // Step 1: Convert to grayscale
            BufferedImage grayImage = toGrayscale(inputImage);
            log.debug("Converted to grayscale");

            // Step 2: Enhance contrast
            BufferedImage contrastImage = enhanceContrast(grayImage);
            log.debug("Enhanced contrast");

            // Step 3: Binarize (threshold)
            BufferedImage binaryImage = binarize(contrastImage);
            log.debug("Applied binarization");

            log.info("Image preprocessing completed");
            return binaryImage;

        } catch (Exception e) {
            log.error("Error during image preprocessing: {}", e.getMessage(), e);
            // Return original image if preprocessing fails
            return inputImage;
        }
    }

    @Override
    public BufferedImage bytesToBufferedImage(byte[] imageBytes) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);
            return ImageIO.read(bais);
        } catch (IOException e) {
            log.error("Error converting bytes to BufferedImage: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to convert image bytes to BufferedImage", e);
        }
    }

    /**
     * Convert image to grayscale
     */
    private BufferedImage toGrayscale(BufferedImage source) {
        int width = source.getWidth();
        int height = source.getHeight();

        BufferedImage gray = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g2d = gray.createGraphics();
        g2d.drawImage(source, 0, 0, null);
        g2d.dispose();

        return gray;
    }

    /**
     * Enhance image contrast using linear contrast stretching
     */
    private BufferedImage enhanceContrast(BufferedImage source) {
        int width = source.getWidth();
        int height = source.getHeight();

        BufferedImage enhanced = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

        // Get min and max pixel values
        int min = 255;
        int max = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = source.getRaster().getSample(x, y, 0);
                if (pixel < min)
                    min = pixel;
                if (pixel > max)
                    max = pixel;
            }
        }

        // Apply linear contrast stretching
        int range = max - min;
        if (range == 0)
            range = 1;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = source.getRaster().getSample(x, y, 0);
                // Stretch to 0-255 range
                int newPixel = ((pixel - min) * 255) / range;
                enhanced.getRaster().setSample(x, y, 0, newPixel);
            }
        }

        return enhanced;
    }

    /**
     * Binarize image using Otsu's threshold method
     */
    private BufferedImage binarize(BufferedImage source) {
        int width = source.getWidth();
        int height = source.getHeight();

        // Calculate histogram
        int[] histogram = new int[256];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = source.getRaster().getSample(x, y, 0);
                histogram[pixel]++;
            }
        }

        // Otsu's thresholding
        int totalPixels = width * height;
        float sum = 0;
        for (int i = 0; i < 256; i++) {
            sum += i * histogram[i];
        }

        float sumB = 0;
        int wB = 0;
        float maxVariance = 0;
        int threshold = 0;

        for (int i = 0; i < 256; i++) {
            wB += histogram[i];
            if (wB == 0)
                continue;

            int wF = totalPixels - wB;
            if (wF == 0)
                break;

            sumB += i * histogram[i];
            float mB = sumB / wB;
            float mF = (sum - sumB) / wF;

            float variance = wB * wF * (mB - mF) * (mB - mF);

            if (variance > maxVariance) {
                maxVariance = variance;
                threshold = i;
            }
        }

        log.debug("Otsu's threshold: {}", threshold);

        // Apply threshold using direct pixel manipulation
        BufferedImage binary = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = source.getRaster().getSample(x, y, 0);
                if (pixel > threshold) {
                    binary.setRGB(x, y, Color.WHITE.getRGB());
                } else {
                    binary.setRGB(x, y, Color.BLACK.getRGB());
                }
            }
        }

        return binary;
    }
}
