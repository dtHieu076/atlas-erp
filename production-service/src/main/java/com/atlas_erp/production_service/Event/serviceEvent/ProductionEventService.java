package com.atlas_erp.production_service.Event.serviceEvent;

import com.atlas_erp.production_service.Event.dtoEvent.ProductionExecutionFinishedEvent;
import com.atlas_erp.production_service.Event.dtoEvent.ProductionExecutionPausedEvent;
import com.atlas_erp.production_service.Event.dtoEvent.ProductionExecutionResumedEvent;
import com.atlas_erp.production_service.Event.dtoEvent.ProductionOrderCreatedEvent;
import com.atlas_erp.production_service.Event.dtoEvent.ProductionPlanApprovedEvent;
import com.atlas_erp.production_service.Event.dtoEvent.ProductionPlanRejectedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductionEventService {

    private static final String TOPIC_EXECUTION_STARTED = "production.execution.started";
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publishExecutionStarted(Object event, UUID executionId) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(TOPIC_EXECUTION_STARTED, executionId.toString(), payload);
            log.info("Sent Kafka event to topic {}: {}", TOPIC_EXECUTION_STARTED, payload);
        } catch (Exception e) {
            log.error("Failed to publish Kafka event: {}", e.getMessage());
        }
    }// Thêm vào ProductionEventService.java

    private static final String TOPIC_EXECUTION_FINISHED = "production.execution.finished";

    public void publishExecutionFinished(ProductionExecutionFinishedEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(TOPIC_EXECUTION_FINISHED, event.getExecutionId().toString(), payload);
            log.info("Sent Kafka event [FINISHED]: {}", payload);
        } catch (Exception e) {
            log.error("Failed to publish Finished event: {}", e.getMessage());
        }
    }// Thêm vào ProductionEventService.java

    private static final String TOPIC_EXECUTION_PAUSED = "production.execution.paused";

    public void publishExecutionPaused(ProductionExecutionPausedEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(TOPIC_EXECUTION_PAUSED, event.getExecutionId().toString(), payload);
            log.info("Sent Kafka event [PAUSED]: {}", payload);
        } catch (Exception e) {
            log.error("Failed to publish Paused event: {}", e.getMessage());
        }
    }// Thêm vào ProductionEventService.java

    private static final String TOPIC_EXECUTION_RESUMED = "production.execution.resumed";

    public void publishExecutionResumed(ProductionExecutionResumedEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(TOPIC_EXECUTION_RESUMED, event.getExecutionId().toString(), payload);
            log.info("Sent Kafka event [RESUMED]: {}", payload);
        } catch (Exception e) {
            log.error("Failed to publish Resumed event: {}", e.getMessage());
        }
    }// Thêm vào ProductionEventService.java

    private static final String TOPIC_ORDER_CREATED = "production.order.created";

    public void publishOrderCreated(ProductionOrderCreatedEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(TOPIC_ORDER_CREATED, event.getOrderId().toString(), payload);
            log.info("🚀 Đã phát event tạo lệnh sản xuất: {}", payload);
        } catch (Exception e) {
            log.error("❌ Lỗi khi phát event PRODUCTION_ORDER_CREATED: {}", e.getMessage());
        }
    }// Thêm vào ProductionEventService.java

    private static final String TOPIC_PLAN_APPROVED = "production.plan.approved";

    public void publishPlanApproved(ProductionPlanApprovedEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(TOPIC_PLAN_APPROVED, event.getPlanId().toString(), payload);
            log.info("🚀 Đã phát event duyệt kế hoạch: {}", payload);
        } catch (Exception e) {
            log.error("❌ Lỗi khi phát event PRODUCTION_PLAN_APPROVED: {}", e.getMessage());
        }
    }// Thêm vào ProductionEventService.java

    private static final String TOPIC_PLAN_REJECTED = "production.plan.rejected";

    public void publishPlanRejected(ProductionPlanRejectedEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(TOPIC_PLAN_REJECTED, event.getPlanId().toString(), payload);
            log.info("🚀 Đã phát event từ chối kế hoạch: {}", payload);
        } catch (Exception e) {
            log.error("❌ Lỗi khi phát event PRODUCTION_PLAN_REJECTED: {}", e.getMessage());
        }
    }
}