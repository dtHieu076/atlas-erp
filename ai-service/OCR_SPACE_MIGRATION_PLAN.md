# Kế Hoạch Chỉnh Sửa: Sử Dụng OCR.space Thay Vì Tesseract Local

## 1. Tổng Quan Thay Đổi

### Trước (Tesseract Local)
- Cần cài đặt Tesseract OCR local
- Cần download language data
- Accuracy: ~70%
- Không cần internet

### Sau (OCR.space API)
- Sử dụng API cloud
- Không cần cài đặt local
- Accuracy: ~85-90% (tốt hơn)
- Cần internet

---

## 2. Các Thay Đổi Cần Thực Hiện

### Bước 1: Cập nhật application.properties
Thêm API key OCR.space
```properties
# OCR.space Configuration
ocr.space.api-key=YOUR_OCR_SPACE_API_KEY
ocr.space.language=viet
```

### Bước 2: Tạo OCR.space Service
Tạo `OcrSpaceService.java` - gọi API của OCR.space

### Bước 3: Cập nhật OcrServiceImpl
Thay thế Tesseract bằng OCR.space API call

### Bước 4: Xóa bỏ không cần thiết
- Xóa `TesseractConfig.java`
- Xóa dependency tess4j trong pom.xml (tùy chọn, giữ lại cũng được)

---

## 3. Cấu Trúc OCR.space API

### Request
```
POST https://api.ocr.space/parse/image
Content-Type: multipart/form-data

Parameters:
- apikey: YOUR_API_KEY
- language: viet (Vietnamese)
- isOverlayRequired: false
- file: IMAGE_FILE
```

### Response
```json
{
  "ParsedResults": [
    {
      "TextOverlay": { ... },
      "Text": "Extracted text here...",
      "ErrorMessage": null,
      "ErrorDetails": ""
    }
  ],
  "OCRExitCode": 1,
  "IsErroredOnProcessing": false,
  "ErrorMessage": [],
  "ErrorDetails": ""
}
```

---

## 4. Các File Cần Chỉnh Sửa

| File | Hành động |
|------|-----------|
| `application.properties` | Thêm OCR.space config |
| `OcrServiceImpl.java` | Thay thế Tesseract bằng OCR.space API |
| `TesseractConfig.java` | Xóa hoặc comment out |
| `pom.xml` | Có thể giữ tess4j (không dùng) |

---

## 5. Ưu Điểm Của OCR.space

| Tiêu chí | Tesseract Local | OCR.space |
|----------|-----------------|-----------|
| Accuracy | ~70% | ~85-90% |
| Cài đặt | Cần cài đặt local | Không cần |
| Language pack | Cần download | Built-in |
| Tiếng Việt | Hỗ trợ nhưng kém | Hỗ trợ tốt |
| Miễn phí | Miễn phí (2500 requests/month) | Miễn phí (2500 requests/month) |

---

## 6. Kế Hoạch Triển Khai Chi Tiết

### Bước 1: Cập nhật application.properties
```properties
# OCR.space Configuration
ocr.space.api-key=HELLOWORLD
ocr.space.language=viet
ocr.space.url=https://api.ocr.space/parse/image
```

### Bước 2: Tạo OCR.space Service
Tạo phương thức gọi OCR.space API trong `OcrServiceImpl`

### Bước 3: Test
- Test với ảnh CCCD mẫu
- Kiểm tra kết quả OCR

---

## 7. Xác Nhận

### Thông tin bạn cung cấp:
- **API Key**: K83040723288957

### Đề xuất của tôi:
1. **Giữ lại code Tesseract** (comment out) - để dự phòng khi cần
2. **Thêm OCR.space** như primary OCR engine
3. **Cấu hình API key** trong application.properties

### Kế hoạch triển khai:
1. Thêm config OCR.space vào application.properties
2. Chỉnh sửa OcrServiceImpl để dùng OCR.space API
3. Giữ lại TesseractConfig (comment out)
4. Test với ảnh CCCD

**Bạn có muốn tôi tiến hành triển khai theo kế hoạch này không?**


