package com.atlas_erp.workflow_service.Event.serviceEvent;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WorkflowEventService {

    private static final String TOPIC_STEP_APPROVED = "workflow.step.approved";
    private static final String TOPIC_INSTANCE_COMPLETED = "workflow.instance.completed";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    // Phát sự kiện khi một bước được duyệt thành công
    public void publishStepApproved(UUID instanceId, Integer stepOrder, String businessType) {
        publish(TOPIC_STEP_APPROVED, instanceId, Map.of(
                "instanceId", instanceId,
                "stepOrder", stepOrder,
                "businessType", businessType));
    }

    // Phát sự kiện khi toàn bộ quy trình hoàn tất
    public void publishInstanceCompleted(UUID instanceId, UUID businessId, String businessType) {
        publish(TOPIC_INSTANCE_COMPLETED, instanceId, Map.of(
                "instanceId", instanceId,
                "businessId", businessId,
                "businessType", businessType,
                "status", "APPROVED"));
    }

    private void publish(String topic, UUID key, Object payload) {
        try {
            kafkaTemplate.send(topic, key.toString(), objectMapper.writeValueAsString(payload));
        } catch (Exception e) {
            throw new RuntimeException("Kafka publish failed", e);
        }
    }// Bổ sung vào WorkflowEventService.java

    private static final String TOPIC_INSTANCE_REJECTED = "workflow.instance.rejected";

    public void publishInstanceRejected(UUID instanceId, UUID businessId, String businessType, String reason) {
        publish(TOPIC_INSTANCE_REJECTED, instanceId, Map.of(
                "instanceId", instanceId,
                "businessId", businessId,
                "businessType", businessType,
                "status", "REJECTED",
                "reason", reason));
    }
}