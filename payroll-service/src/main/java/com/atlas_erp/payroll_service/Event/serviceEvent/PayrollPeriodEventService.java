package com.atlas_erp.payroll_service.Event.serviceEvent;

import com.atlas_erp.payroll_service.Event.dtoEvent.PayrollApprovedEvent; // Import mới
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
    private static final String TOPIC_PAYROLL_APPROVED = "payroll.approved"; // Topic mới

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    /**
     * Phát sự kiện khi kỳ lương bị khóa
     */
    public void publishPeriodLocked(PayrollPeriodLockedEvent event) {
        sendEvent(TOPIC_PERIOD_LOCKED, event.getPeriodId().toString(), event);
    }

    /**
     * Phát sự kiện khi bảng lương cá nhân được duyệt
     */
    public void publishPayrollApproved(PayrollApprovedEvent event) {
        sendEvent(TOPIC_PAYROLL_APPROVED, event.getPayrollId().toString(), event);
    }

    /**
     * Hàm dùng chung để gửi Kafka tránh lặp code
     */
    private void sendEvent(String topic, String key, Object event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topic, key, payload);
            log.info("Sent Kafka event to {}: {}", topic, payload);
        } catch (Exception e) {
            log.error("Failed to publish event to Kafka [Topic: {}]: {}", topic, e.getMessage());
            throw new RuntimeException("Kafka message delivery failed", e);
        }
    }
}