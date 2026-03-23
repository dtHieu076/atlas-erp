package com.atlas_erp.user_activity_service.service;

import com.atlas_erp.user_activity_service.dto.ActivityLogRequest;
import com.atlas_erp.user_activity_service.kafka.producer.LogProducer;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor// Dùng cái này của Lombok để thay cho Constructor thủ công
public class TrackingService {

    private final LogProducer logProducer;

    public void trackActivity(ActivityLogRequest request, HttpServletRequest httpRequest) {
        // Đảm bảo metadata có thể chỉnh sửa (Mutable)
        if (request.getMetadata() == null) {
            request.setMetadata(new java.util.HashMap<>());
        } else {
            // Nếu đã có map, bọc lại bằng HashMap để đảm bảo thêm được dữ liệu
            request.setMetadata(new java.util.HashMap<>(request.getMetadata()));
        }

        if (httpRequest != null) {
            request.getMetadata().put("ipAddress", getClientIpAddress(httpRequest));
            request.getMetadata().put("userAgent", httpRequest.getHeader("User-Agent"));
        }

        // Bây giờ đã khớp kiểu dữ liệu: truyền Object vào Producer
        logProducer.sendLog(request);
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}