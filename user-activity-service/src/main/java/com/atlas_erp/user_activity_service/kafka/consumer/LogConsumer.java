package com.atlas_erp.user_activity_service.kafka.consumer;

import com.atlas_erp.user_activity_service.document.UserActivity;
import com.atlas_erp.user_activity_service.dto.ActivityLogRequest;
import com.atlas_erp.user_activity_service.repository.UserActivityRepository;
// Xóa import ObjectMapper
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Service
public class LogConsumer {

    private final UserActivityRepository repository;
    // Bỏ ObjectMapper đi vì Kafka đã tự parse JSON sang Object rồi

    public LogConsumer(UserActivityRepository repository) {
        this.repository = repository;
    }

    @KafkaListener(topics = "${app.kafka.topic.user-activity-log}", groupId = "user-activity-consumer")
    // Nhận trực tiếp Object (ActivityLogRequest) thay vì String (jsonMessage)
    public void consumeLog(ActivityLogRequest request) {
        try {
            // Không cần parse thủ công nữa

            // Map sang Document và làm giàu
            UserActivity activity = new UserActivity();
            activity.setSessionId(request.getSessionId());
            activity.setUserId(request.getUserId());
            activity.setAction(request.getAction());
            activity.setPage(request.getPage());
            activity.setClientTimestamp(request.getClientTimestamp());
            activity.setMetadata((Map<String, Object>) request.getMetadata());
            activity.setUserName(request.getUserName());

            // Thêm serverTimestamp
            activity.setServerTimestamp(Instant.now());

            // Lấy IP/UA từ metadata (đã enrich ở Producer)
            activity.setIpAddress((String) activity.getMetadata().get("ipAddress"));
            activity.setUserAgent((String) activity.getMetadata().get("userAgent"));

            // Lưu MongoDB
            repository.save(activity);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}