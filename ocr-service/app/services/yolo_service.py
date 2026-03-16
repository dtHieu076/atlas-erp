"""
YOLO Service for text/field detection and cropping
"""
import logging
import io
import cv2
import numpy as np
from PIL import Image
from typing import Dict, List
from ultralytics import YOLO
from pathlib import Path

logger = logging.getLogger(__name__)

class YoloService:
    _instance = None
    _model = None
    
    def __new__(cls):
        if cls._instance is None:
            logger.info("Creating YoloService singleton")
            cls._instance = super(YoloService, cls).__new__(cls)
            cls._instance._initialized = False
        return cls._instance
    
    def __init__(self):
        if self._initialized:
            return
        model_path = Path('./best.pt')
        if not model_path.exists():
            raise FileNotFoundError(f"YOLO model not found at {model_path}")
        logger.info(f"Loading YOLO model from {model_path}")
        try:
            self._model = YOLO(model_path)
            logger.info("YOLO model loaded successfully")
        except Exception as e:
            logger.error(f"Failed to load YOLO model: {e}")
            raise
        self._initialized = True
    
    def detect_and_crop(self, image_bytes: bytes) -> Dict[str, List[Image.Image]]:
        """
        Detect text fields with YOLO and crop them. 
        Returns a dictionary where each key holds a LIST of PIL Images (sorted top-to-bottom).
        """
        if not self._initialized:
            raise RuntimeError("YoloService not initialized")
        
        # Decode image bytes to numpy array
        nparr = np.frombuffer(image_bytes, np.uint8)
        image = cv2.imdecode(nparr, cv2.IMREAD_COLOR)
        if image is None:
            raise ValueError("Failed to decode image")
        
        # Run YOLO prediction
        results = self._model(image)[0]
        
        # Khởi tạo Dictionary chứa List thay vì chứa 1 ảnh duy nhất
        crops = {}
        
        # Extract boxes with their coordinates for sorting
        boxes_data = []
        for r in results.boxes:
            cls_id = int(r.cls)
            class_name = self._model.names[cls_id]
            x1, y1, x2, y2 = map(int, r.xyxy[0])
            boxes_data.append({"name": class_name, "y1": y1, "y2": y2, "x1": x1, "x2": x2})
            
        # Sắp xếp các box từ trên xuống dưới (để dòng 1 đứng trước dòng 2)
        boxes_data = sorted(boxes_data, key=lambda b: b['y1'])

        for b in boxes_data:
            class_name = b['name']
            
            # Crop the region
            cropped = image[b['y1']:b['y2'], b['x1']:b['x2']]
            if cropped.size == 0:
                continue
            
            # Convert BGR to RGB and to PIL
            cropped_rgb = cv2.cvtColor(cropped, cv2.COLOR_BGR2RGB)
            pil_image = Image.fromarray(cropped_rgb)
            
            # Nếu class_name chưa có trong dict thì tạo List mới
            if class_name not in crops:
                crops[class_name] = []
                
            # Thêm ảnh vào List
            crops[class_name].append(pil_image)
            logger.debug(f"Cropped {class_name}: {pil_image.size}")
        
        logger.info(f"Detected classes: {list(crops.keys())}")
        return crops