# OCR Service

Python OCR Service sử dụng EasyOCR để trích xuất văn bản từ hình ảnh, được tối ưu cho tiếng Việt.

## 🚀 Quick Start

### Local Development

1. **Tạo virtual environment:**
```bash
python -m venv venv
source venv/bin/activate  # Linux/Mac
# hoặc
venv\Scripts\activate  # Windows
```

2. **Cài đặt dependencies:**
```bash
pip install -r requirements.txt
```

3. **Chạy service:**
```bash
uvicorn app.main:app --reload --port 8000
```

4. **Test:**
- API Docs: http://localhost:8000/docs
- Health: http://localhost:8000/health

### Docker

```bash
# Build
docker build -t ocr-service .

# Run
docker run -p 8000:8000 ocr-service
```

## 📡 API Endpoints

### POST /api/ocr/extract-text

Trích xuất văn bản từ file ảnh.

```bash
curl -X POST "http://localhost:8000/api/ocr/extract-text" \
  -F "file=@test.jpg"
```

**Response:**
```json
{
  "success": true,
  "text": "Căn cước công dân Họ tên: Nguyễn Văn A",
  "results": [
    {
      "text": "Căn cước công dân",
      "confidence": 0.99,
      "bbox": [[100, 50], [300, 50], [300, 80], [100, 80]]
    }
  ],
  "message": "OCR completed successfully. Extracted 5 text blocks."
}
```

### POST /api/ocr/extract-text-base64

Trích xuất văn bản từ ảnh mã hóa base64.

```bash
curl -X POST "http://localhost:8000/api/ocr/extract-text-base64" \
  -H "Content-Type: application/json" \
  -d '{"image": "base64_encoded_image_string"}'
```

### GET /health

Health check endpoint.

```json
{
  "status": "UP",
  "service": "ocr-service",
  "ocr": "easyocr",
  "languages": ["vi", "en"]
}
```

## ⚙️ Configuration

Các tham số có thể được cấu hình qua environment variables:

| Variable | Default | Description |
|----------|---------|-------------|
| `PORT` | 8000 | Port chạy service |
| `LANGUAGES` | vi,en | Ngôn ngữ cho EasyOCR |
| `GPU` | false | Sử dụng GPU hay không |

## 🐳 Docker Compose

```yaml
version: '3.8'

services:
  ocr-service:
    build: ./ocr-service
    ports:
      - "8000:8000"
    environment:
      - LANGUAGES=vi,en
      - GPU=false
    deploy:
      resources:
        limits:
          memory: 4G
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8000/health"]
      interval: 30s
      timeout: 10s
      retries: 3
```

## 📝 Notes

- **EasyOCR lần đầu**: Lần chạy đầu tiên sẽ tải model (khoảng 10-20 giây)
- **Memory**: EasyOCR yêu cầu ~2-4GB RAM
- **GPU**: Để tăng tốc độ, có thể bật GPU (cần CUDA)

## 📁 Project Structure

```
ocr-service/
├── app/
│   ├── main.py              # FastAPI app
│   ├── routes/
│   │   └── ocr.py          # OCR endpoints
│   ├── services/
│   │   └── easyocr_service.py
│   ├── models/
│   │   └── response.py
│   └── utils/
│       └── image_processor.py
├── requirements.txt
├── Dockerfile
└── README.md
```

## 🔧 Integration với ai-service

Xem chi tiết trong `ai-service/OCR_SERVICE_SEPARATION_PLAN.md`

