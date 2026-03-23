# TODO: Implement Direct LLM Vision Endpoint for ID Card

✅ **All code changes complete**  
✅ **Step 5 Complete**: Compile test successful (no errors)

**✅ TASK COMPLETE**

## Summary:
- **New experimental API**: `POST /api/ai/ocr/id-card-direct-llm` 
- **Input**: JSON `{"imageBase64": "data:image/...;base64,..."}`
- **Process**: Direct to Vision LLM (qwen-2.5-vl-3b-instruct) via OpenRouter, bypasses OCR-service
- **Output**: Same `IdCardResponse` format as original `/id-card`
- **Config**: Add to `atlas-erp-config-repo/ai-service.properties`:
  ```
  ai-service.vision.llm.model=qwen/qwen-2.5-vl-3b-instruct:free
  ```
- **Original code 100% unchanged**

## Test Commands:
```
# Test old endpoint (unchanged)
curl -F "frontImage=@front.jpg" -F "backImage=@back.jpg" http://localhost:8080/api/ai/ocr/id-card

# Test new endpoint
curl -X POST http://localhost:8080/api/ai/ocr/id-card-direct-llm \
  -H "Content-Type: application/json" \
  -d "{\"imageBase64\": \"data:image/jpeg;base64,YOUR_BASE64_HERE\"}"
```

**Files modified**:
- IdCardBase64Request.java (NEW)
- IdCardService.java (+method)
- IdCardServiceImpl.java (+method, +visionModel @Value)
- IdCardController.java (+endpoint)

The new API is ready for testing. Original /id-card endpoint works exactly as before.
