package com.atlas_erp.organization_service.Event.serviceEvent;

import com.atlas_erp.organization_service.Event.dtoEvent.OrganizationCreatedEvent;
import com.atlas_erp.organization_service.Event.dtoEvent.OrganizationUpdatedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrganizationEventService {

    // Inject KafkaTemplate (Key là String, Value là Event DTO)
    private final KafkaTemplate<String, Object> kafkaTemplate;

    // Tên topic nên định nghĩa trong file application.yml
    private static final String TOPIC_ORG_CREATED = "organization.created";
    private static final String TOPIC_ORG_UPDATED = "organization.updated";

    public void publishCreatedEvent(OrganizationCreatedEvent event) {
        log.info("Publishing OrganizationCreatedEvent for ID: {}", event.getOrganizationId());

        try {
            // Bắn event vào Kafka
            // Key là organizationId để đảm bảo thứ tự xử lý nếu cần
            kafkaTemplate.send(TOPIC_ORG_CREATED, event.getOrganizationId().toString(), event);

            log.info("Event sent successfully to topic: {}", TOPIC_ORG_CREATED);
        } catch (Exception e) {
            log.error("Failed to publish event for organization {}: {}", event.getOrganizationId(), e.getMessage());
            // Tùy chọn: Có thể ném lỗi để Rollback Transaction nếu việc bắn Event là bắt
            // buộc
        }
    }

    public void publishUpdatedEvent(OrganizationUpdatedEvent event) {
        // Bắn vào topic "organization.updated"
        kafkaTemplate.send(TOPIC_ORG_UPDATED, event.getOrganizationId().toString(), event);
    }
}
