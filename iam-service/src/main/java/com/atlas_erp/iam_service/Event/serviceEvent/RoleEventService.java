package com.atlas_erp.iam_service.Event.serviceEvent;

import com.atlas_erp.iam_service.Event.dtoEvent.RoleCreatedEvent;
import com.atlas_erp.iam_service.Event.dtoEvent.RolePermissionUpdatedEvent;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleEventService {

    private static final String TOPIC_ROLE_CREATED = "iam.role.created";
    private static final String TOPIC_ROLE_PERMISSION_UPDATED = "iam.role.permission.updated";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publishRoleCreated(RoleCreatedEvent event) {
        try {
            kafkaTemplate.send(
                    TOPIC_ROLE_CREATED,
                    event.getRoleId().toString(),
                    objectMapper.writeValueAsString(event));
        } catch (Exception e) {
            throw new RuntimeException("Serialize / send Kafka failed", e);
        }
    }

    public void publishRolePermissionUpdated(
            RolePermissionUpdatedEvent event) {

        try {
            kafkaTemplate.send(
                    TOPIC_ROLE_PERMISSION_UPDATED,
                    event.getRoleId().toString(),
                    objectMapper.writeValueAsString(event));
        } catch (Exception e) {
            throw new RuntimeException("Serialize / send Kafka failed", e);
        }
    }
}
