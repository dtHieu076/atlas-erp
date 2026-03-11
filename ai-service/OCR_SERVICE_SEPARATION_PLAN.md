# Kế Hoạch Tách Service Phân Tích Hình Ảnh (OCR)

## 1. Tổng Quan Kiến Trúc Hiện Tại

### 1.1 Luồng xử lý OCR hiện tại (Java)
```
Client → IdCardController (/api/ai/ocr/id-card)
           ↓
        IdCardServiceImpl (Orchestrator)
           ↓
        ImageProcessor (Java - tiền xử lý ảnh)
           ↓
        OcrServiceImpl (OCR.space API - external)
           ↓
        ParserServiceImpl (Regex parsing)
           ↓
        LLM (OpenRouter - format text)
           ↓
        Response
```

### 1.2 Các thành phần liên quan đến OCR:
- **Controller**: `IdCardController.java`
- **Service**: `IdCardServiceImpl.java`, `OcrServiceImpl.java`, `ParserServiceImpl.java`, `ImageProcessorImpl.java`
- **DTO**: `IdCardRequest.java`, `IdCardResponse.java`, `IdCardData.java`
- **Config**: `application.properties` (OCR.space keys)

### 1.3 Nhược điểm của giải pháp hiện tại:
- OCR.space API có giới hạn request miễn phí
- Độ chính xác cho tiếng Việt chưa tối ưu
- Phụ thuộc vào external API (latency, downtime)
- Chi phí khi vượt quota miễn phí

---

## 2. Kiến Trúc Đề Xuất

### 2.1 Architecture Overview
```
┌─────────────────────────────────────────────────────────────────┐
│                        CLIENT                                    │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│              ai-service (Java - Spring Boot)                    │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │  IdCardController / ChatController                        │  │
│  │  IdCardService (giữ nguyên orchestration logic)           │  │
│  │  ParserService (giữ nguyên - regex parsing)               │  │
│  │  LLM Service (giữ nguyên - OpenRouter)                     │  │
│  └──────────────────────────────────────────────────────────┘  │
│                              │                                   │
│                   HTTP/REST call                                │
│                              ▼                                   │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│              ocr-service (Python - FastAPI)                      │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │  /api/ocr/extract-text  (POST)                           │  │
│  │  EasyOCR Reader (Vietnamese)                              │  │
│  │  Image preprocessing (OpenCV)                            │  │
│  └──────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```

### 2.2 So sánh hai approach:

| Khía cạnh | Hiện tại (OCR.space) | Đề xuất (Python EasyOCR) |
|-----------|----------------------|---------------------------|
| Ngôn ngữ | Java | Python |
| OCR Engine | OCR.space API | EasyOCR (local) |
| Tiếng Việt | Tốt | Rất tốt |
| Latency | Phụ thuộc network | Local processing |
| Chi phí | Có (sau quota) | Không (sau setup) |
| Offline | Không | Có |
| Maintenance | OCR.space | EasyOCR |

---

## 3. Chi Tiết Triển Khai

### 3.1 Tạo Python OCR Service (`ocr-service/`)

#### Cấu trúc thư mục:
```
ocr-service/
├── app/
│   ├── __init__.py
│   ├── main.py              # FastAPI app
│   ├── routes/
│   │   ├── __init__.py
│   │   └── ocr.py          # OCR endpoints
│   ├── services/
│   │   ├── __init__.py
│   │   └── easyocr_service.py
│   ├── models/
│   │   ├── __init__.py
│   │   └── response.py
│   └── utils/
│       ├── __init__.py
│       └── image_processor.py
├── requirements.txt
├── Dockerfile
├── .dockerignore
├── .gitignore
└── README.md
```

#### File: `requirements.txt`
```txt
fastapi==0.115.0
uvicorn[standard]==0.32.0
easyocr==1.7.0
python-multipart==0.0.12
Pillow==10.4.0
numpy==1.26.4
opencv-python-headless==4.10.0.84
pydantic==2.9.0
python-dotenv==1.0.1
```

#### File: `app/main.py`
```python
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from app.routes import ocr

app = FastAPI(
    title="OCR Service",
    description="Python OCR Service using EasyOCR for Vietnamese text",
    version="1.0.0"
)

# CORS
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Include routers
app.include_router(ocr.router, prefix="/api/ocr", tags=["OCR"])

@app.get("/health")
async def health_check():
    return {"status": "UP", "service": "ocr-service", "ocr": "easyocr"}
```

#### File: `app/services/easyocr_service.py`
```python
import easyocr
import numpy as np
from PIL import Image
import io
from typing import List, Dict, Any

class EasyOCRService:
    def __init__(self, languages: List[str] = ['vi', 'en']):
        print("Initializing EasyOCR Reader...")
        self.reader = easyocr.Reader(languages, gpu=False)
        print("EasyOCR Reader initialized successfully")
    
    def extract_text(self, image_bytes: bytes) -> List[Dict[str, Any]]:
        """
        Extract text from image bytes
        Returns list of dict with bbox, text, confidence
        """
        # Convert bytes to image
        image = Image.open(io.BytesIO(image_bytes))
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
        """Extract all text as single string"""
        results = self.extract_text(image_bytes)
        return " ".join([r["text"]"] for r in results)
```

#### File: `app/routes/ocr.py`
```python
from fastapi import APIRouter, UploadFile, File, HTTPException
from app.services.easyocr_service import EasyOCRService
from app.models.response import OcrResponse

router = APIRouter()
ocr_service = EasyOCRService()

@router.post("/extract-text", response_model=OcrResponse)
async def extract_text(file: UploadFile = File(...)):
    """
    Extract text from image using EasyOCR
    """
    try:
        # Validate file type
        if file.content_type not in ["image/jpeg", "image/png", "image/jpg"]:
            raise HTTPException(
                status_code=400,
                detail="Only JPEG and PNG images are supported"
            )
        
        # Read file
        image_bytes = await file.read()
        
        # Extract text
        results = ocr_service.extract_text(image_bytes)
        
        # Combine all text
        full_text = " ".join([r["text"] for r in results])
        
        return OcrResponse(
            success=True,
            text=full_text,
            results=results,
            message="OCR completed successfully"
        )
        
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@router.post("/extract-text-base64")
async def extract_text_base64(body: dict):
    """
    Extract text from base64 encoded image
    """
    try:
        import base64
        
        if "image" not in body:
            raise HTTPException(status_code=400, detail="Missing 'image' field")
        
        # Decode base64
        image_data = body["image"]
        if "," in image_data:
            # Handle data URL format
            image_data = image_data.split(",")[1]
        
        image_bytes = base64.b64decode(image_data)
        
        # Extract text
        results = ocr_service.extract_text(image_bytes)
        full_text = " ".join([r["text"] for r in results])
        
        return OcrResponse(
            success=True,
            text=full_text,
            results=results,
            message="OCR completed successfully"
        )
        
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
```

### 3.2 Cập nhật ai-service (Java)

#### Thêm dependency vào `pom.xml`:
```xml
<!-- OCR Service Client -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>
```

#### Cập nhật `application.properties`:
```properties
# OCR Service (Python) Configuration
ocr.service.url=http://localhost:8000
ocr.service.timeout=60000
```

#### Tạo `OcrClient.java`:
```java
package com.atlas_erp.ai_service.Client;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Component
public class OcrClient {

    private final WebClient webClient;

    @Value("${ocr.service.url:http://localhost:8000}")
    private String ocrServiceUrl;

    public OcrClient() {
        this.webClient = WebClient.builder()
                .baseUrl("http://localhost:8000")
                .defaultHeader("Content-Type", MediaType.MULTIPART_FORM_DATA_VALUE)
                .build();
    }

    public Mono<OcrClientResponse> extractText(MultipartFile file) {
        return webClient.post()
                .uri("/api/ocr/extract-text")
                .bodyValue(file)
                .retrieve()
                .bodyToMono(OcrClientResponse.class);
    }
}

@Data
class OcrClientResponse {
    private boolean success;
    private String text;
    private List<Map<String, Object>> results;
    private String message;
}
```

#### Cập nhật `OcrServiceImpl.java`:
```java
@Slf4j
@Service
@RequiredArgsConstructor
public class OcrServiceImpl implements OcrService {

    private final ObjectMapper objectMapper;
    private final OcrClient ocrClient;

    @Override
    public String extractText(BufferedImage image) {
        try {
            byte[] imageBytes = bufferedImageToBytes(image);
            return extractText(imageBytes);
        } catch (Exception e) {
            log.error("Error during OCR extraction: {}", e.getMessage(), e);
            throw new RuntimeException("OCR extraction failed: " + e.getMessage(), e);
        }
    }

    @Override
    public String extractText(byte[] imageBytes) {
        log.info("Starting OCR extraction using Python OCR Service...");
        
        try {
            // Convert byte[] to MultipartFile
            ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
            MultipartFileImpl file = new MultipartFileImpl(
                inputStream, 
                "image.jpg", 
                "image/jpeg", 
                imageBytes.length
            );
            
            // Call Python OCR Service
            OcrClientResponse response = ocrClient.extractText(file).block();
            
            if (response == null || !response.isSuccess()) {
                throw new RuntimeException("OCR service returned error: " + 
                    (response != null ? response.getMessage() : "null response"));
            }
            
            log.info("OCR extraction completed, text length: {}", response.getText().length());
            return response.getText();
            
        } catch (Exception e) {
            log.error("Error calling OCR service: {}", e.getMessage(), e);
            throw new RuntimeException("OCR extraction failed: " + e.getMessage(), e);
        }
    }
    
    private byte[] bufferedImageToBytes(BufferedImage image) {
        try {
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            javax.imageio.ImageIO.write(image, "jpg", baos);
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Error converting BufferedImage to bytes: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to convert image to bytes", e);
        }
    }
}

// Simple MultipartFile implementation
class MultipartFileImpl implements MultipartFile {
    private final InputStream inputStream;
    private final String filename;
    private final String contentType;
    private final long size;

    public MultipartFileImpl(InputStream inputStream, String filename, String contentType, long size) {
        this.inputStream = inputStream;
        this.filename = filename;
        this.contentType = contentType;
        this.size = size;
    }

    @Override
    public String getName() { return "file"; }

    @Override
    public String getOriginalFilename() { return filename; }

    @Override
    public String getContentType() { return contentType; }

    @Override
    public boolean isEmpty() { return size == 0; }

    @Override
    public long getSize() { return size; }

    @Override
    public byte[] getBytes() throws IOException {
        return inputStream.readAllBytes();
    }

    @Override
    public InputStream getInputStream() { return inputStream; }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        Files.copy(inputStream, dest.toPath());
    }
}
```

---

## 4. Docker Deployment

### 4.1 Dockerfile cho OCR Service
```dockerfile
FROM python:3.11-slim

WORKDIR /app

# Install system dependencies
RUN apt-get update && apt-get install -y \
    libgl1-mesa-glx \
    libglib2.0-0 \
    libsm6 \
    libxext6 \
    libxrender-dev \
    && rm -rf /var/lib/apt/lists/*

# Copy requirements
COPY requirements.txt .

# Install Python dependencies
RUN pip install --no-cache-dir -r requirements.txt

# Copy application
COPY app/ ./app/

# Expose port
EXPOSE 8000

# Run application
CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8000"]
```

### 4.2 Docker Compose (tùy chọn)
```yaml
version: '3.8'

services:
  ocr-service:
    build: ./ocr-service
    ports:
      - "8000:8000"
    deploy:
      resources:
        limits:
          memory: 4G
        reservations:
          devices:
            - driver: nvidia
              count: 1
              capabilities: [gpu]
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8000/health"]
      interval: 30s
      timeout: 10s
      retries: 3

  ai-service:
    build: ./ai-service
    ports:
      - "8080:8080"
    environment:
      - OCR_SERVICE_URL=http://ocr-service:8000
    depends_on:
      - ocr-service
```

---

## 5. Kế Hoạch Triển Khai (TODO)

### Phase 1: Tạo Python OCR Service ⏳
- [ ] Tạo thư mục `ocr-service/`
- [ ] Tạo `requirements.txt`
- [ ] Tạo FastAPI app với `/health` endpoint
- [ ] Tạo EasyOCR service
- [ ] Tạo OCR endpoint (`/api/ocr/extract-text`)
- [ ] Tạo Dockerfile
- [ ] Test locally

### Phase 2: Cập nhật ai-service ⏳
- [ ] Thêm WebClient dependency vào `pom.xml`
- [ ] Tạo `OcrClient.java`
- [ ] Cập nhật `OcrServiceImpl.java` để gọi Python service
- [ ] Thêm config vào `application.properties`
- [ ] Test integration

### Phase 3: Production ⏳
- [ ] Tạo Docker Compose
- [ ] Cập nhật deployment scripts
- [ ] Monitoring & Logging
- [ ] Fallback mechanism (nếu OCR service down)

---

## 6. Fallback Strategy

Trong trường hợp Python OCR service không khả dụng:

```java
@Service
public class OcrServiceFallbackImpl implements OcrService {
    
    private final OcrServiceImpl primaryService;
    private final OcrSpaceServiceImpl fallbackService;
    
    @Override
    public String extractText(BufferedImage image) {
        try {
            return primaryService.extractText(image);
        } catch (Exception e) {
            log.warn("Primary OCR failed, using fallback: {}", e.getMessage());
            return fallbackService.extractText(image);
        }
    }
}
```

---

## 7. Ước tính Thời Gian

| Task | Thời gian ước tính |
|------|-------------------|
| Phase 1: Python OCR Service | 2-3 giờ |
| Phase 2: Java Integration | 1-2 giờ |
| Phase 3: Docker & Deployment | 1 giờ |
| **Total** | **4-6 giờ** |

---

## 8. Lưu Ý Quan Trọng

1. **GPU Support**: EasyOCR có thể sử dụng GPU để tăng tốc độ xử lý (cài đặt CUDA)
2. **Memory**: EasyOCR yêu cầu ~2-4GB RAM cho model Vietnamese
3. **Cold Start**: Lần đầu load model sẽ chậm (khoảng 10-20 giây)
4. **Image Size**: Nên resize ảnh lớn trước khi OCR để tăng tốc
5. **Preprocessing**: Có thể thêm image preprocessing (contrast, denoise) trong Python service

---

## 9. API Contract

### Request (Python OCR Service)
```
POST /api/ocr/extract-text
Content-Type: multipart/form-data

Parameters:
- file: Binary image file (required)

 hoặc

POST /api/ocr/extract-text-base64
Content-Type: application/json

{
  "image": "base64_encoded_image_string"
}
```

### Response
```json
{
  "success": true,
  "text": "extracted text from image",
  "results": [
    {
      "text": "Căn cước",
      "confidence": 0.99,
      "bbox": [[x1,y1], [x2,y2], [x3,y3], [x4,y4]]
    }
  ],
  "message": "OCR completed successfully"
}
```

---

## Kết Luận

Việc tách OCR service sang Python mang lại:
- ✅ Độ chính xác cao hơn cho tiếng Việt (EasyOCR)
- ✅ Chi phí thấp hơn (không phụ thuộc API bên thứ 3)
- ✅ Kiểm soát tốt hơn (local deployment)
- ✅ Dễ dàng mở rộng với các tính năng OCR khác

Hãy xem xét và cho tôi biết nếu cần điều chỉnh hoặc bổ sung thêm chi tiết nào!

