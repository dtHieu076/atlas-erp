"""
VietOCR Service for Vietnamese text recognition
"""
import logging
from PIL import Image
from typing import Dict, Any
from vietocr.tool.predictor import Predictor
from vietocr.tool.config import Cfg

logger = logging.getLogger(__name__)

# --- THÊM ĐOẠN NÀY VÀO ĐỂ FIX LỖI ANTIALIAS CHO PILLOW 10+ ---
if not hasattr(Image, 'ANTIALIAS'):
    Image.ANTIALIAS = Image.Resampling.LANCZOS
# -----------------------------------------------------------

class VietOcrService:
    _instance = None
    _predictor = None
    
    def __new__(cls):
        if cls._instance is None:
            logger.info("Creating VietOcrService singleton")
            cls._instance = super(VietOcrService, cls).__new__(cls)
            cls._instance._initialized = False
        return cls._instance
    
    def __init__(self):
        if self._initialized:
            return
        try:
            config = Cfg.load_config_from_name('vgg_transformer')
            config['device'] = 'cpu'  # Use 'cuda:0' if GPU available
            logger.info("Loading VietOCR predictor")
            self._predictor = Predictor(config)
            logger.info("VietOCR predictor loaded successfully")
        except Exception as e:
            logger.error(f"Failed to load VietOCR: {e}")
            raise
        self._initialized = True
    
    def recognize_text(self, image: Image.Image) -> str:
        """
        Recognize text from PIL image using VietOCR
        
        Args:
            image: PIL Image
            
        Returns:
            Recognized text string
        """
        if not self._initialized:
            raise RuntimeError("VietOcrService not initialized")
        
        try:
            text = self._predictor.predict(image)
            logger.debug(f"Recognized text: {text[:50]}...")
            return text.strip()
        except Exception as e:
            logger.error(f"VietOCR prediction failed: {e}")
            return ""
