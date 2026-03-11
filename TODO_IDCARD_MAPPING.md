# TODO: Chỉnh sửa tính năng quét CCCD trong ai-service

## Mục tiêu: Map dữ liệu CCCD phù hợp với bảng Employee

## Các bước thực hiện:

### Bước 1: Sửa IdCardData.java
- [x] Thêm trường `hometown` (String) - map từ placeOfOrigin
- [x] Thêm trường `permanentAddress` (String) - map từ address

### Bước 2: Sửa IdCardServiceImpl.java
- [x] Cập nhật method parseWithLLMFromRawOcr() để map:
  - placeOfOrigin -> hometown
  - address -> permanentAddress
- [x] Cập nhật processIdCard() để set hometown và permanentAddress
- [x] Sửa method getTextOrNull() (lỗi syntax)
- [x] Thêm method nullToEmpty() (thiếu trong code)

### Bước 3: Test và verify
- [ ] Kiểm tra dữ liệu trả về

## Tổng kết thay đổi:

### IdCardData.java:
- Thêm trường `hometown`: Quê quán (map từ placeOfOrigin)
- Thêm trường `permanentAddress`: Địa chỉ thường trú (map từ address)

### IdCardServiceImpl.java:
- parseWithLLMFromRawOcr(): Map placeOfOrigin -> hometown, address -> permanentAddress
- processIdCard(): Set hometown và permanentAddress khi LLM parse thành công
- getTextOrNull(): Sửa lỗi syntax
- nullToEmpty(): Thêm method mới

