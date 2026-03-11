"""
Main FastAPI application for OCR Service
"""
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from app.routes import ocr

app = FastAPI(
    title="OCR Service",
    description="Python OCR Service using EasyOCR for Vietnamese text extraction",
    version="1.0.0",
    docs_url="/docs",
    redoc_url="/redoc"
)

# CORS middleware
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Include routers
app.include_router(ocr.router, prefix="/api/ocr", tags=["OCR"])


@app.get("/")
async def root():
    """Root endpoint"""
    return {
        "service": "OCR Service",
        "version": "1.0.0",
        "description": "Python OCR Service using EasyOCR for Vietnamese text extraction"
    }


@app.get("/health")
async def health_check():
    """Health check endpoint"""
    return {
        "status": "UP",
        "service": "ocr-service",
        "ocr": "easyocr",
        "languages": ["vi", "en"]
    }

