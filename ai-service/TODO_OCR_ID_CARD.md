# TODO: Triển Khai API OCR Căn Cước Công Dân

## ✅ Hoàn Thành

### Công nghệ sử dụng:
- **OCR**: OCR.space API (thay thế Tesseract local)
- **Image Processing**: Java built-in
- **Parser**: Regex patterns cho CCCD VN
- **LLM**: OpenRouter (StepFun)

### Các File Đã Tạo:
- [x] pom.xml - Dependencies
- [x] IdCardRequest.java - Request DTO
- [x] IdCardData.java - Structured data
- [x] IdCardResponse.java - Response DTO
- [x] ImageProcessor.java - Interface
- [x] ImageProcessorImpl.java - Image processing
- [x] OcrService.java - Interface
- [x] OcrServiceImpl.java - OCR.space API
- [x] ParserService.java - Interface
- [x] ParserServiceImpl.java - Regex patterns
- [x] IdCardService.java - Interface
- [x] IdCardServiceImpl.java - Orchestrator
- [x] IdCardController.java - REST endpoint
- [x] application.properties - Config

### Đã Xóa:
- [x] TesseractConfig.java - Không cần thiết

---

## API Endpoint

```
POST /api/ai/ocr/id-card
Content-Type: multipart/form-data

Parameters:
- frontImage: MultipartFile (bắt buộc)
- backImage: MultipartFile (bắt buộc)
```

## Pipeline

```
Client → ImageProcessor → OCR.space API → Parser (Regex) → LLM → Response
```

## Cấu Hình

```properties
# OCR.space
ocr.space.api-key=K83040723288957
ocr.space.language=viet

# LLM
spring.ai.openai.chat.options.model=stepfun/step-3.5-flash:free
```


