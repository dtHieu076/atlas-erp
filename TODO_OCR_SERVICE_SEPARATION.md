# TODO: OCR Service Separation Implementation

## Phase 1: Tạo Python OCR Service ✅

- [x] 1.1 Tạo thư mục `ocr-service/`
- [x] 1.2 Tạo `requirements.txt`
- [x] 1.3 Tạo FastAPI app (`app/main.py`)
- [x] 1.4 Tạo EasyOCR service (`app/services/easyocr_service.py`)
- [x] 1.5 Tạo OCR routes (`app/routes/ocr.py`)
- [x] 1.6 Tạo Response models (`app/models/response.py`)
- [x] 1.7 Tạo Dockerfile
- [x] 1.8 Tạo .dockerignore và .gitignore

## Phase 2: Cập nhật ai-service (Java) ✅

- [x] 2.1 Thêm WebClient dependency vào `pom.xml`
- [x] 2.2 Tạo `OcrClient.java`
- [x] 2.3 Cập nhật `OcrServiceImpl.java` để gọi Python service
- [x] 2.4 Thêm config vào `application.properties`

## Phase 3: Test & Validation

- [ ] 3.1 Test Python OCR service locally
- [ ] 3.2 Test integration với ai-service
- [ ] 3.3 Verify OCR results quality

