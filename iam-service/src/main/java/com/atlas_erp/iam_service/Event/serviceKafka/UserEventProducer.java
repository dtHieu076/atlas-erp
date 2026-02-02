package com.atlas_erp.iam_service.Event.serviceKafka;

import com.atlas_erp.iam_service.Event.dtoEvent.UserCreatedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publishUserCreated(UserCreatedEvent event) {
        // Nơi nhận: HR Service và Workflow Service
        try {
            kafkaTemplate.send(
                    "iam.user.created",
                    event.getUserId().toString(),
                    objectMapper.writeValueAsString(event));
        } catch (Exception e) {
            throw new RuntimeException("Lỗi serialize event hoặc gửi Kafka", e);
        }
    }
}
