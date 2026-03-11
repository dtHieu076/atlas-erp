"""
OCR Routes - API endpoints for text extraction
"""
from fastapi import APIRouter, UploadFile, File, HTTPException
from app.services.easyocr_service import EasyOCRService
from app.models.response import OcrResponse

router = APIRouter()

# Initialize OCR service (lazy loading)
ocr_service: EasyOCRService = None


def get_ocr_service() -> EasyOCRService:
    """Get or initialize OCR service"""
    global ocr_service
    if ocr_service is None:
        ocr_service = EasyOCRService(languages=['vi', 'en'], gpu=False)
    return ocr_service


@router.post("/extract-text", response_model=OcrResponse)
async def extract_text(file: UploadFile = File(...)):
    """
    Extract text from image using EasyOCR
    
    Args:
        file: Image file (JPEG, PNG)
        
    Returns:
        OcrResponse with extracted text and details
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
        
        # Get OCR service and extract text
        service = get_ocr_service()
        results = service.extract_text(image_bytes)
        
        # Combine all text
        full_text = " ".join([r["text"] for r in results])
        
        return OcrResponse(
            success=True,
            text=full_text,
            results=results,
            message=f"OCR completed successfully. Extracted {len(results)} text blocks."
        )
        
    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"OCR processing failed: {str(e)}")


@router.post("/extract-text-base64", response_model=OcrResponse)
async def extract_text_base64(body: dict):
    """
    Extract text from base64 encoded image
    
    Args:
        body: JSON with base64 encoded image
        
    Request body:
        {
            "image": "base64_encoded_image_string"
        }
        
    Returns:
        OcrResponse with extracted text and details
    """
    try:
        import base64
        
        if "image" not in body:
            raise HTTPException(status_code=400, detail="Missing 'image' field in request body")
        
        # Decode base64
        image_data = body["image"]
        
        # Handle data URL format (e.g., "data:image/jpeg;base64,...")
        if "," in image_data:
            image_data = image_data.split(",")[1]
        
        try:
            image_bytes = base64.b64decode(image_data)
        except Exception as e:
            raise HTTPException(status_code=400, detail=f"Invalid base64 encoding: {str(e)}")
        
        # Get OCR service and extract text
        service = get_ocr_service()
        results = service.extract_text(image_bytes)
        
        # Combine all text
        full_text = " ".join([r["text"] for r in results])
        
        return OcrResponse(
            success=True,
            text=full_text,
            results=results,
            message=f"OCR completed successfully. Extracted {len(results)} text blocks."
        )
        
    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"OCR processing failed: {str(e)}")


@router.get("/extract-text/available")
async def check_available():
    """
    Check if OCR service is available and ready
    """
    try:
        service = get_ocr_service()
        return {
            "available": service.is_ready(),
            "languages": service.languages,
            "gpu": service.gpu
        }
    except Exception as e:
        raise HTTPException(status_code=503, detail=f"OCR service not available: {str(e)}")

