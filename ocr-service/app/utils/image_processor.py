"""
Image processing utilities for OCR preprocessing
"""
import io
import numpy as np
from PIL import Image, ImageEnhance, ImageFilter
from typing import Tuple, Optional


def preprocess_image(
    image_bytes: bytes,
    resize: Optional[Tuple[int, int]] = None,
    enhance_contrast: bool = False,
    denoise: bool = False
) -> bytes:
    """
    Preprocess image before OCR
    
    Args:
        image_bytes: Input image bytes
        resize: Optional (width, height) to resize
        enhance_contrast: Whether to enhance contrast
        denoise: Whether to apply denoising
        
    Returns:
        Processed image bytes
    """
    # Open image
    image = Image.open(io.BytesIO(image_bytes))
    
    # Convert to RGB if needed
    if image.mode != 'RGB':
        image = image.convert('RGB')
    
    # Resize if specified
    if resize:
        image = image.resize(resize, Image.Resampling.LANCZOS)
    
    # Enhance contrast
    if enhance_contrast:
        enhancer = ImageEnhance.Contrast(image)
        image = enhancer.enhance(1.5)
    
    # Denoise
    if denoise:
        image = image.filter(ImageFilter.MedianFilter(size=3))
    
    # Save to bytes
    output = io.BytesIO()
    image.save(output, format='JPEG', quality=95)
    return output.getbytes()


def get_image_info(image_bytes: bytes) -> dict:
    """
    Get basic image information
    
    Args:
        image_bytes: Input image bytes
        
    Returns:
        Dict with width, height, format, mode
    """
    image = Image.open(io.BytesIO(image_bytes))
    return {
        "width": image.width,
        "height": image.height,
        "format": image.format,
        "mode": image.mode,
        "size_bytes": len(image_bytes)
    }


def validate_image(image_bytes: bytes, max_size_mb: float = 10) -> Tuple[bool, str]:
    """
    Validate image
    
    Args:
        image_bytes: Input image bytes
        max_size_mb: Maximum file size in MB
        
    Returns:
        Tuple of (is_valid, message)
    """
    # Check size
    size_mb = len(image_bytes) / (1024 * 1024)
    if size_mb > max_size_mb:
        return False, f"Image too large: {size_mb:.2f}MB (max: {max_size_mb}MB)"
    
    # Check format
    try:
        image = Image.open(io.BytesIO(image_bytes))
        if image.format not in ['JPEG', 'PNG', 'JPG', 'BMP']:
            return False, f"Unsupported format: {image.format}"
    except Exception as e:
        return False, f"Invalid image: {str(e)}"
    
    return True, "Valid image"

