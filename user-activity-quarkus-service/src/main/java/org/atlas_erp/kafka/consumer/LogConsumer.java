package org.atlas_erp.kafka.consumer;

import org.atlas_erp.document.UserActivity;
import org.atlas_erp.dto.ActivityLogRequest;
import org.atlas_erp.repository.UserActivityRepository;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.Instant;
import java.util.Map;

@ApplicationScoped
public class LogConsumer {

    private final UserActivityRepository repository;

    public LogConsumer(UserActivityRepository repository) {
        this.repository = repository;
    }

    // Nhận từ kênh "user-activity-in" đã khai báo trong application.properties
    @Incoming("user-activity-in")
    public void consumeLog(ActivityLogRequest request) {
        try {
            UserActivity activity = new UserActivity();
            activity.setSessionId(request.getSessionId());
            activity.setUserId(request.getUserId());
            activity.setAction(request.getAction());
            activity.setPage(request.getPage());
            activity.setClientTimestamp(request.getClientTimestamp());
            activity.setMetadata((Map<String, Object>) request.getMetadata());
            activity.setUserName(request.getUserName());

            activity.setServerTimestamp(Instant.now());

            if (activity.getMetadata() != null) {
                activity.setIpAddress((String) activity.getMetadata().get("ipAddress"));
                activity.setUserAgent((String) activity.getMetadata().get("userAgent"));
            }

            // Lưu vào MongoDB bằng Panache
            repository.persist(activity);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}