"""
OCR Routes - API endpoints for text extraction
"""
from fastapi import APIRouter, UploadFile, File, HTTPException
from app.services import YoloService, VietOcrService
from app.models.response import CccdResponse

router = APIRouter()

# Global service instances (singleton via class)
yolo_service = YoloService()
vietocr_service = VietOcrService()

@router.post("/extract-cccd", response_model=CccdResponse)
async def extract_cccd(file: UploadFile = File(...)):
    """
    Extract CCCD information using YOLO detection + VietOCR recognition
    """
    try:
        # Validate file type
        if file.content_type not in ["image/jpeg", "image/jpg", "image/png"]:
            raise HTTPException(
                status_code=400,
                detail=f"Unsupported file type: {file.content_type}. Only JPEG and PNG are supported."
            )
        
        # Read file bytes
        image_bytes = await file.read()
        
        # Check file size (max 10MB)
        if len(image_bytes) > 10 * 1024 * 1024:
            raise HTTPException(
                status_code=400,
                detail="File too large. Maximum size is 10MB."
            )
        
        # YOLO detection and cropping (Tradu về Dict[str, List[Image]])
        crops = yolo_service.detect_and_crop(image_bytes)
        
        # VietOCR recognition - MAPPING CHUẨN XÁC TỪ ROBOFLOW
        field_mapping = {
            'id': 'id_number',
            'name': 'full_name',
            'date_of_birth': 'dob',               # Sửa 'dob' thành 'date_of_birth'
            'permanent_residence': 'address',
            'hometown': 'hometown',               # Sửa 'home' thành 'hometown'
            'nation': 'nationality',
            'date_of_expiry': 'expiry_date',       # (Lưu ý: Hạn sử dụng đang gán vào Ngày cấp)
            'gender': 'gender'                    # Bổ sung trường giới tính
        }
        
        cccd_data = {k: "" for k in field_mapping.values()}
        confidences = []
        
        # Lặp qua từng Class Name (VD: 'permanent_residence')
        for class_name, list_of_images in crops.items():
            mapped_field = field_mapping.get(class_name)
            
            if mapped_field:
                extracted_texts = []
                
                # Vì list_of_images là 1 danh sách, ta phải lặp qua từng ảnh con
                for img in list_of_images:
                    text = vietocr_service.recognize_text(img)
                    if text:
                        extracted_texts.append(text)
                
                # Nối các dòng lại với nhau bằng dấu cách (VD: Nối 2 dòng địa chỉ)
                final_text = " ".join(extracted_texts)
                cccd_data[mapped_field] = final_text
                
                # Tính nhẩm độ tự tin
                conf = min(1.0, len(final_text)/20.0) if final_text else 0.0
                confidences.append(conf)
        
        avg_conf = sum(confidences) / len(confidences) if confidences else 0.0
        
        return CccdResponse(
            success=True,
            **cccd_data,
            confidence=round(avg_conf, 4),
            message=f"Extracted CCCD from {len(crops)} detected fields: {list(crops.keys())}"
        )
        
    except HTTPException:
        raise
    except Exception as e:
        import traceback
        import logging
        logging.error(f"CCCD extraction failed: {traceback.format_exc()}")
        raise HTTPException(status_code=500, detail=f"CCCD extraction failed: {str(e)}")