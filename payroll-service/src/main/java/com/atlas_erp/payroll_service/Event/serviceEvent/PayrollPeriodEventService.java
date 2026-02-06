package com.atlas_erp.payroll_service.Event.serviceEvent;

import com.atlas_erp.payroll_service.Event.dtoEvent.PayrollPeriodLockedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PayrollPeriodEventService {

    // Định nghĩa các Topics
    private static final String TOPIC_PERIOD_LOCKED = "payroll.period.locked";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    /**
     * Phát sự kiện khi kỳ lương bị khóa
     * 
     * @param event DTO chứa thông tin kỳ lương bị khóa
     */
    public void publishPeriodLocked(PayrollPeriodLockedEvent event) {
        try {
            // Chuyển đổi object sang JSON string
            String payload = objectMapper.writeValueAsString(event);

            // Gửi Kafka: Topic, Key (Id kỳ lương), Payload (JSON)
            kafkaTemplate.send(
                    TOPIC_PERIOD_LOCKED,
                    event.getPeriodId().toString(),
                    payload);

            log.info("Sent Kafka event to {}: {}", TOPIC_PERIOD_LOCKED, payload);
        } catch (Exception e) {
            log.error("Failed to publish PayrollPeriodLockedEvent to Kafka: {}", e.getMessage());
            // Tùy theo yêu cầu, có thể throw Exception để rollback transaction nếu gửi
            // Kafka thất bại
            throw new RuntimeException("Serialize / send Kafka failed", e);
        }
    }
}