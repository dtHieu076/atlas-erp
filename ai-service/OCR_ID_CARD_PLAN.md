# Kế Hoạch Triển Khai API OCR Căn Cước Công Dân

## 1. Tổng Quan Dự Án

### Mục tiêu
Tạo API nhận 2 ảnh căn cước công dân (mặt trước và mặt sau), trích xuất dữ liệu từ ảnh bằng OCR, sau đó chuyển cho LLM xử lý và trả về thông tin nhân viên.

### Input
- Ảnh căn cước công dân mặt trước (front)
- Ảnh căn cước công dân mặt sau (back)

### Output
- Thông tin trích xuất được định dạng thành câu văn tiếng Việt: "Thông tin của nhân viên như sau:......"

---

## 2. Nguyên Lý Hoạt Động (Đã Cập Nhật)

### Pipeline Chuẩn

```
Client gửi 2 ảnh CCCD
        ↓
┌─────────────────────────────────────────┐
│     Image Preprocessing (OpenCV)       │
│  - Grayscale                            │
│  - Denoise                              │
│  - Contrast enhancement                 │
│  - Deskew                               │
└─────────────────────────────────────────┘
        ↓
┌─────────────────────────────────────────┐
│           OCR Service (Tesseract)        │
│  - Trích xuất raw text từ ảnh          │
└─────────────────────────────────────────┘
        ↓
┌─────────────────────────────────────────┐
│        Parser Service (Regex)           │
│  - Parse field theo template CCCD      │
│  - Rule-based extraction                │
└─────────────────────────────────────────┘
        ↓
┌─────────────────────────────────────────┐
│          LLM Service (Optional)         │
│  - Normalize data                       │
│  - Fill missing fields                  │
│  - Generate câu văn hoàn chỉnh         │
└─────────────────────────────────────────┘
        ↓
        Response
```

### Tại Sao Pipeline Này Tốt Hơn?

| Bước | Vai trò | Lý do |
|------|---------|-------|
| Image Preprocessing | Cải thiện chất lượng ảnh | OCR accuracy phụ thuộc 80% vào preprocessing |
| OCR | Trích xuất raw text | Tesseract trả về text lộn xộn |
| Parser (Regex) | Parse field cố định | CCCD có format cố định, không cần LLM parse |
| LLM | Normalize + Generate | LLM chỉ làm việc it, tốn ít token |

---

## 3. Phương Án Triển Khai

### Phương án A: Sử dụng Tesseract OCR (Miễn phí - Demo)
- **Ưu điểm**: Miễn phí, chạy local
- **Nhược điểm**: Accuracy ~70%, cần preprocessing tốt
- **Thư viện**: `net.sourceforge.tess4j:tess4j` + `org.openpnp:opencv`

### Phương án B: Google Cloud Vision API (Production)
- **Ưu điểm**: Accuracy 90-95%, hỗ trợ Vietnamese tốt
- **Nhược điểm**: Cần tài khoản Google Cloud, tốn phí

### **Khuyến nghị**: Bắt đầu với Phương án A (Tesseract) cho demo

---

## 4. Kiến Trúc Service (Mới)

### Cấu Trúc Module

```
IdCardController
        ↓
IdCardService (Orchestrator)
        ↓
 ┌─────────────────────────────────────┐
 │         ImageProcessor              │
 │   - preprocess(MultipartFile)       │
 │   - toBufferedImage()                │
 │   - grayscale()                      │
 │   - enhanceContrast()               │
 └─────────────────────────────────────┘
        ↓
 ┌─────────────────────────────────────┐
 │           OcrService                │
 │   - extractText(BufferedImage)       │
 │   - extractTextFromImage()          │
 └─────────────────────────────────────┘
        ↓
 ┌─────────────────────────────────────┐
 │        ParserService                │
 │   - parseFrontCard(String rawText)  │
 │   - parseBackCard(String rawText)   │
 │   - extractField() with Regex       │
 └─────────────────────────────────────┘
        ↓
 ┌─────────────────────────────────────┐
 │         LlmService                  │
 │   - normalizeData(IdCardData)       │
 │   - generateSentence()             │
 └─────────────────────────────────────┘
```

### Lợi Ích Của Kiến Trúc Này
- ✅ Dễ test từng module
- ✅ Dễ thay OCR engine
- ✅ Dễ replace LLM
- ✅ Debug dễ dàng

---

## 5. Kế Hoạch Triển Khai Chi Tiết

### Bước 1: Thêm dependencies vào pom.xml
```xml
<!-- Tesseract OCR -->
<dependency>
    <groupId>net.sourceforge.tess4j</groupId>
    <artifactId>tess4j</artifactId>
    <version>5.7.0</version>
</dependency>

<!-- OpenCV for Image Processing -->
<dependency>
    <groupId>org.openpnp</groupId>
    <artifactId>opencv</artifactId>
    <version>4.8.0-1</version>
</dependency>
```

### Bước 2: Tạo DTOs mới (Structured Data)
- `IdCardRequest.java` - Request nhận 2 ảnh
- `IdCardData.java` - Structured data sau khi parse
- `IdCardResponse.java` - Response cuối cùng

### Bước 3: Tạo các Service
- `ImageProcessor.java` - Xử lý ảnh (grayscale, contrast, deskew)
- `OcrService.java` - Interface OCR
- `OcrServiceImpl.java` - Implementation với Tesseract
- `ParserService.java` - Regex parsing cho CCCD
- `IdCardService.java` - Orchestrator gọi các service

### Bước 4: Tạo Controller endpoint mới
- `IdCardController.java` - `POST /api/ai/ocr/id-card`

---

## 6. Cấu Trúc File Sau Khi Triển Khai

```
ai-service/src/main/java/com/atlas_erp/ai_service/
├── Controller/
│   ├── AiChatController.java          [EXISTING]
│   └── IdCardController.java         [NEW]
├── Service/
│   ├── IdCardService.java            [NEW] Orchestrator
│   ├── ImageProcessor.java            [NEW] Image preprocessing
│   ├── OcrService.java                [NEW] Interface
│   ├── ParserService.java             [NEW] Regex parsing
│   └── Impl/
│       ├── OcrServiceImpl.java        [NEW] Tesseract impl
│       └── ToolCallChatServiceImpl.java [EXISTING - reuse]
├── Config/
│   └── TesseractConfig.java           [NEW] OCR config
├── Dto/
│   ├── Request/
│   │   └── IdCardRequest.java         [NEW]
│   ├── Response/
│   │   ├── IdCardResponse.java        [NEW]
│   │   └── IdCardData.java            [NEW] Structured data
│   └── Response/
│       └── ChatResponse.java          [EXISTING]
└── tools/
    └── HrTools.java                   [EXISTING]
```

---

## 7. Regex Patterns Cho CCCD Việt Nam

### Mặt Trước CCCD
```java
// Họ và tên
Pattern.compile("(?:Họ\\s*và\\s*tên|Họ\\s*tên)[:\\s]+(.+)", Pattern.CASE_INSENSITIVE)

// Ngày sinh
Pattern.compile("(?:Ngày\\s*sinh|DOB)[:\\s]+(\\d{1,2}[/-]\\d{1,2}[/-]\\d{2,4})", Pattern.CASE_INSENSITIVE)

// Giới tính
Pattern.compile("(?:Giới\\s*tính|Sex|Gender)[:\\s]+(Nam|Nữ)", Pattern.CASE_INSENSITIVE)

// Số CCCD
Pattern.compile("(?:Số|CMND|CCCD|ID\\s*No)[:\\s]*(\\d{9,12})", Pattern.CASE_INSENSITIVE)

// Quê quán / Nơi thường trú
Pattern.compile("(?:Quê\\s*quán|Nơi\\s*thường\\s*trú|Address)[:\\s]+(.+?)(?=Ngày|$)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL)
```

### Mặt Sau CCCD
```java
// Ngày cấp
Pattern.compile("(?:Ngày\\s*cấp|Issue\\s*Date)[:\\s]+(\\d{1,2}[/-]\\d{1,2}[/-]\\d{2,4})", Pattern.CASE_INSENSITIVE)

// Nơi cấp
Pattern.compile("(?:Nơi\\s*cấp|Issue\\s*Place)[:\\s]+(.+?)(?=$)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL)

// Dân tộc
Pattern.compile("(?:Dân\\s*tộc|Ethnicity)[:\\s]+(.+?)(?=,|$)", Pattern.CASE_INSENSITIVE)

// Tôn giáo
Pattern.compile("(?:Tôn\\s*giáo|Religion)[:\\s]+(.+?)(?=,|$)", Pattern.CASE_INSENSITIVE)
```

---

## 8. API Specification

### Request
```
POST /api/ai/ocr/id-card
Content-Type: multipart/form-data

Parameters:
- frontImage: MultipartFile (bắt buộc) - Ảnh mặt trước CCCD
- backImage: MultipartFile (bắt buộc) - Ảnh mặt sau CCCD
```

### Response (Structured Data)
```json
{
  "success": true,
  "message": "Xử lý thành công",
  "data": {
    "fullName": "Nguyễn Văn A",
    "dateOfBirth": "01/01/1990",
    "gender": "Nam",
    "idNumber": "079203000123",
    "placeOfOrigin": "TP. Hồ Chí Minh",
    "address": "123 Đường Nguyễn Trãi, Quận 1, TP. Hồ Chí Minh",
    "issueDate": "01/01/2020",
    "issuePlace": "Công an TP. Hồ Chí Minh",
    "ethnicity": "Kinh",
    "religion": "Không",
    "rawOcrText": {
      "front": "...",
      "back": "..."
    },
    "formattedText": "Thông tin của nhân viên như sau: Họ tên: Nguyễn Văn A, sinh ngày 01/01/1990, giới tính Nam, số CCCD: 079203000123, ..."
  }
}
```

### Response Error
```json
{
  "success": false,
  "message": "Không thể trích xuất thông tin từ ảnh. Vui lòng thử lại với ảnh rõ hơn.",
  "data": null
}
```

---

## 9. Image Preprocessing Pipeline

### Java Implementation (OpenCV)
```java
public BufferedImage preprocess(BufferedImage image) {
    // 1. Convert to grayscale
    Mat mat = bufferedImageToMat(image);
    Mat gray = new Mat();
    Imgproc.cvtColor(mat, gray, Imgproc.COLOR_BGR2GRAY);
    
    // 2. Denoise
    Mat denoised = new Mat();
    Imgproc.fastNlMeansDenoising(gray, denoised);
    
    // 3. Increase contrast
    Mat contrast = new Mat();
    denoised.convertTo(contrast, -1, 1.5, 0);
    
    // 4. Binarize
    Mat binary = new Mat();
    Imgproc.threshold(contrast, binary, 0, 255, Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU);
    
    // 5. Deskew (optional - detect angle and rotate)
    
    return matToBufferedImage(binary);
}
```

---

## 10. Prompt Cho LLM (Chỉ Generate Câu Văn)

```prompt
Bạn là trợ lý AI chuyên format thông tin căn cước công dân Việt Nam.

Dưới đây là dữ liệu đã được parse từ CCCD:

- Họ tên: {fullName}
- Ngày sinh: {dateOfBirth}
- Giới tính: {gender}
- Số CCCD: {idNumber}
- Nơi thường trú: {address}
- Ngày cấp: {issueDate}
- Nơi cấp: {issuePlace}
- Dân tộc: {ethnicity}
- Tôn giáo: {religion}

Hãy tạo câu văn hoàn chỉnh bằng tiếng Việt, bắt đầu bằng:
"Thông tin của nhân viên như sau: ..."

Chỉ generate câu văn, không thêm giải thích.
```

---

## 11. Cấu Hình Cần Thiết

### application.properties
```properties
# Tesseract OCR
tesseract.data-path=C:/Program Files/Tesseract-OCR/tessdata
tesseract.language=vie+eng
tesseract.psm=6

# OpenCV (optional)
opencv.lib.path=C:/opencv/build/java/x64
```

### Download Tesseract data
- Download `vie.traineddata` và `eng.traineddata` từ:
  https://github.com/tesseract-ocr/tessdata

---

## 12. So Sánh Accuracy OCR

| Engine | Accuracy | Phù hợp |
|--------|----------|---------|
| Tesseract | ~70% | Demo |
| EasyOCR | ~85% | Demo nâng cao |
| Google Vision | ~90-95% | Production |
| AWS Textract | ~95% | Production |

**Khuyến nghị**:
- Demo: Tesseract với preprocessing tốt
- Production: Google Vision API hoặc Azure Form Recognizer

---

## 13. Risk & Mitigation

| Risk | Mitigation |
|------|-------------|
| Ảnh mờ | Image preprocessing + validation kích thước file |
| OCR sai sót | Regex parse + LLM validate |
| Ảnh không đúng format | Validation mime type + yêu cầu guide |
| Tesseract không nhận diện tiếng Việt | Cấu hình đúng language pack + preprocessing |

---

## 14. Timeline (Demo)

1. **Ngày 1**: Setup Tesseract + OpenCV + Tạo DTOs + Controller
2. **Ngày 2**: Implement ImageProcessor + OcrService
3. **Ngày 3**: Implement ParserService (Regex) + IdCardService
4. **Ngày 4**: Tích hợp LLM + Test + Fix bugs

---

## 15. Next Steps

1. ✅ Xác nhận kiến trúc pipeline (Preprocessing → OCR → Parser → LLM)
2. ✅ Xác nhận kiến trúc service (Tách rõ ImageProcessor, OcrService, ParserService, LlmService)
3. ✅ Xác nhận DTO structured data
4. Download Tesseract data files
5. Bắt đầu implement

---

## 16. Tài Liệu Tham Khảo

- Tesseract OCR: https://github.com/tesseract-ocr/tess4j
- OpenCV Java: https://github.com/openpnp/opencv
- Regex CCCD Việt Nam: https://regex101.com (test patterns)


