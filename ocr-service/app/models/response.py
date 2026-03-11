"""Response models for OCR service"""
from typing import List, Optional, Any
from pydantic import BaseModel, Field


class OcrResult(BaseModel):
    """Individual OCR result with bounding box"""
    text: str = Field(..., description="Extracted text")
    confidence: float = Field(..., description="Confidence score (0-1)")
    bbox: List[List[float]] = Field(..., description="Bounding box coordinates")


class OcrResponse(BaseModel):
    """OCR API response"""
    success: bool = Field(..., description="Whether OCR was successful")
    text: str = Field(..., description="All extracted text concatenated")
    results: List[OcrResult] = Field(default_factory=list, description="Detailed OCR results")
    message: str = Field(default="", description="Optional message")
    
    class Config:
        json_schema_extra = {
            "example": {
                "success": True,
                "text": "Căn cước công dân Họ tên: Nguyễn Văn A",
                "results": [
                    {
                        "text": "Căn cước công dân",
                        "confidence": 0.99,
                        "bbox": [[100, 50], [300, 50], [300, 80], [100, 80]]
                    }
                ],
                "message": "OCR completed successfully"
            }
        }


class HealthResponse(BaseModel):
    """Health check response"""
    status: str
    service: str
    ocr: str
    languages: List[str]

