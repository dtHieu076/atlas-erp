package org.atlas_erp.kafka.producer;

import org.atlas_erp.dto.ActivityLogRequest;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

@ApplicationScoped
public class LogProducer {

    private static final Logger log = Logger.getLogger(LogProducer.class);

    // Bắn vào kênh "user-activity-out" đã khai báo trong application.properties
    @Channel("user-activity-out")
    Emitter<ActivityLogRequest> emitter;

    public void sendLog(ActivityLogRequest data) {
        emitter.send(data).whenComplete((success, failure) -> {
            if (failure == null) {
                log.infof("Gửi log thành công cho User: %s", data.getUserId());
            } else {
                log.errorf("Lỗi gửi Kafka: %s", failure.getMessage());
            }
        });
    }
}