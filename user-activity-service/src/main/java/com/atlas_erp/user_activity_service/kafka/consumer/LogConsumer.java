package com.atlas_erp.user_activity_service.kafka.consumer;

import com.atlas_erp.user_activity_service.document.UserActivity;
import com.atlas_erp.user_activity_service.dto.ActivityLogRequest;
import com.atlas_erp.user_activity_service.repository.UserActivityRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Service
public class LogConsumer {

    private final UserActivityRepository repository;
    private final ObjectMapper objectMapper;

    public LogConsumer(UserActivityRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${app.kafka.topic.user-activity-log}", groupId = "user-activity-consumer")
    public void consumeLog(String jsonMessage) {
        try {
            // Parse JSON từ Kafka về DTO
            ActivityLogRequest request = objectMapper.readValue(jsonMessage, ActivityLogRequest.class);

            // Map sang Document và làm giàu
            UserActivity activity = new UserActivity();
            activity.setSessionId(request.getSessionId());
            activity.setUserId(request.getUserId());
            activity.setAction(request.getAction());
            activity.setPage(request.getPage());
            activity.setClientTimestamp(request.getClientTimestamp());
            activity.setMetadata((Map<String, Object>) request.getMetadata());

            // Thêm serverTimestamp
            activity.setServerTimestamp(Instant.now());

            // Lấy IP/UA từ metadata (đã enrich ở Producer)
            activity.setIpAddress((String) activity.getMetadata().get("ipAddress"));
            activity.setUserAgent((String) activity.getMetadata().get("userAgent"));

            // Lưu MongoDB
            repository.save(activity);

        } catch (Exception e) {
            // Xử lý lỗi parse JSON
            e.printStackTrace();
        }
    }
    // Comment: Consumer lắng nghe topic, parse JSON -> UserActivity, set server
    // time, lưu DB. Retry nếu fail.
}
