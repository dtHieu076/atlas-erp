"""
EasyOCR Service - Vietnamese text extraction using EasyOCR
"""
import io
import numpy as np
from PIL import Image
from typing import List, Dict, Any, Optional
import easyocr


class EasyOCRService:
    """Service for extracting text from images using EasyOCR"""
    
    def __init__(self, languages: List[str] = None, gpu: bool = False):
        """
        Initialize EasyOCR reader
        
        Args:
            languages: List of language codes (default: ['vi', 'en'])
            gpu: Whether to use GPU (default: False)
        """
        if languages is None:
            languages = ['vi', 'en']
        
        self.languages = languages
        self.gpu = gpu
        self.reader = None
        self._initialize_reader()
    
    def _initialize_reader(self):
        """Initialize EasyOCR reader"""
        print(f"Initializing EasyOCR Reader with languages: {self.languages}")
        try:
            self.reader = easyocr.Reader(self.languages, gpu=self.gpu)
            print("EasyOCR Reader initialized successfully")
        except Exception as e:
            print(f"Error initializing EasyOCR: {e}")
            raise
    
    def extract_text(self, image_bytes: bytes) -> List[Dict[str, Any]]:
        """
        Extract text from image bytes
        
        Args:
            image_bytes: Raw image bytes
            
        Returns:
            List of dict with bbox, text, confidence
        """
        # Convert bytes to PIL Image
        image = Image.open(io.BytesIO(image_bytes))
        
        # Convert to numpy array
        image_np = np.array(image)
        
        # Run OCR
        results = self.reader.readtext(image_np)
        
        # Format results
        extracted = []
        for (bbox, text, prob) in results:
            extracted.append({
                "text": text,
                "confidence": round(prob, 4),
                "bbox": bbox
            })
        
        return extracted
    
    def extract_text_full(self, image_bytes: bytes) -> str:
        """
        Extract all text as a single string
        
        Args:
            image_bytes: Raw image bytes
            
        Returns:
            All extracted text concatenated
        """
        results = self.extract_text(image_bytes)
        return " ".join([r["text"] for r in results])
    
    def extract_with_details(self, image_bytes: bytes) -> Dict[str, Any]:
        """
        Extract text with detailed information
        
        Args:
            image_bytes: Raw image bytes
            
        Returns:
            Dict with full text and detailed results
        """
        results = self.extract_text(image_bytes)
        
        return {
            "full_text": " ".join([r["text"] for r in results]),
            "results": results,
            "total_words": len(results),
            "average_confidence": sum(r["confidence"] for r in results) / len(results) if results else 0
        }
    
    def is_ready(self) -> bool:
        """Check if the OCR reader is ready"""
        return self.reader is not None

