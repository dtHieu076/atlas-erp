import cv2
from ultralytics import YOLO

# 1. Load model của bạn
model = YOLO('best.pt') 

# 2. Chạy dự đoán với ngưỡng Confidence cực thấp (0.1) 
# Việc hạ thấp conf giúp ta biết mô hình có tìm thấy nhưng độ tự tin thấp, 
# hay là hoàn toàn không tìm thấy gì.
results = model.predict(source='cccdtT.jpg', conf=0.1, save=True)

# 3. Hiển thị kết quả ra màn hình (nếu máy bạn có giao diện)
# Hoặc nó sẽ tự lưu vào thư mục: runs/detect/predict/
for r in results:
    im_array = r.plot()  # Vẽ các box và label lên ảnh
    cv2.imshow('Kiem tra CCCD', im_array)
    cv2.waitKey(0) # Nhấn phím bất kỳ để đóng cửa sổ

# 4. In ra danh sách các Class mà nó detect được
for box in results[0].boxes:
    class_id = int(box.cls[0])
    conf = float(box.conf[0])
    print(f"Detected: {model.names[class_id]} with confidence: {conf:.2f}")