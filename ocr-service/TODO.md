# OCR Service Refactor TODO
## Plan Progress Tracking

### Completed:
- [ ] Update requirements.txt
- [ ] Create app/services/yolo_service.py
- [ ] Create app/services/vietocr_service.py
- [ ] Update app/models/response.py
- [ ] Update app/routes/ocr.py
- [ ] Delete app/services/easyocr_service.py
- [ ] Update app/services/__init__.py
- [ ] Update app/models/__init__.py
- [ ] Update app/main.py

### Testing:
- [ ] pip install -r requirements.txt
- [ ] uvicorn app.main:app --reload
- [ ] Test /api/ocr/extract-cccd endpoint

### Docker:
- [ ] Update Dockerfile if needed
- [ ] docker build -t ocr-service .
- [ ] docker run -p 8000:8000 ocr-service
