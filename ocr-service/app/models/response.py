"""Response models for OCR service"""
from typing import List, Optional, Any
from pydantic import BaseModel, Field

class CccdResponse(BaseModel):
    """CCCD extraction response"""
    success: bool = Field(..., description="Whether extraction was successful")
    id_number: str = Field(default="", description="CCCD ID number (12 digits)")
    full_name: str = Field(default="", description="Full name")
    dob: str = Field(default="", description="Date of birth (DD/MM/YYYY)")
    address: str = Field(default="", description="Address")
    hometown: str = Field(default="", description="Hometown")
    expiry_date: str = Field(default="", description="expiry date")
    gender: str = Field(default="", description="Gender")
    nationality: str = Field(default="", description="Nationality")
    confidence: float = Field(0.0, description="Average confidence score")
    message: str = Field(default="", description="Processing message")
    
    class Config:
        json_schema_extra = {
            "example": {
                "success": True,
                "id_number": "001199990012",
                "full_name": "NGUYEN VAN A",
                "dob": "01/01/1990",
                "confidence": 0.95
            }
        }

class HealthResponse(BaseModel):
    """Health check response"""
    status: str
    service: str
    yolo: str = "YOLOv11"
    vietocr: str = "vgg_transformer"
    device: str = "cpu"

