package com.atlas_erp.user_activity_service.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import com.atlas_erp.user_activity_service.dto.ActivityLogRequest;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class LogProducer {

    // Đổi kiểu dữ liệu Value từ String sang Object (hoặc ActivityLogRequest)
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "user-activity-log";

    public void sendLog(ActivityLogRequest data) {
        // Gửi trực tiếp Object, Spring Kafka sẽ tự convert sang JSON nhờ cấu hình ở
        // trên
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(TOPIC, data);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Gửi log thành công cho User: {}", data.getUserId());
            } else {
                log.error("Lỗi gửi Kafka: {}", ex.getMessage());
            }
        });
    }
}