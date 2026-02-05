package com.atlas_erp.iam_service.Event.serviceEvent;

import com.atlas_erp.iam_service.Event.dtoEvent.UserAccountLockedEvent;
import com.atlas_erp.iam_service.Event.dtoEvent.UserAccountUnlockedEvent;
import com.atlas_erp.iam_service.Event.dtoEvent.UserCreatedEvent;
import com.atlas_erp.iam_service.Event.dtoEvent.UserLoggedOutEvent;
import com.atlas_erp.iam_service.Event.dtoEvent.UserRoleAssignedEvent;
import com.atlas_erp.iam_service.Event.dtoEvent.UserStatusUpdatedEvent;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserEventService {

    private static final String TOPIC_USER_CREATED = "iam.user.created";
    private static final String TOPIC_USER_LOGGED_OUT = "iam.user.logged_out";
    private static final String TOPIC_USER_STATUS_UPDATED = "iam.user.status_updated";
    private static final String TOPIC_USER_LOCKED = "iam.user.locked";
    private static final String TOPIC_USER_UNLOCKED = "iam.user.unlocked";
    private static final String TOPIC_USER_ROLE_ASSIGNED = "iam.user.role_assigned";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publishUserCreated(UserCreatedEvent event) {
        try {
            kafkaTemplate.send(
                    TOPIC_USER_CREATED,
                    event.getUserId().toString(),
                    objectMapper.writeValueAsString(event));
        } catch (Exception e) {
            throw new RuntimeException("Serialize / send Kafka failed", e);
        }
    }

    public void publishUserLoggedOut(UserLoggedOutEvent event) {
        try {
            kafkaTemplate.send(
                    TOPIC_USER_LOGGED_OUT,
                    event.getUserId().toString(),
                    objectMapper.writeValueAsString(event));
        } catch (Exception e) {
            throw new RuntimeException("Serialize / send Kafka failed", e);
        }
    }

    public void publishUserStatusUpdated(UserStatusUpdatedEvent event) {
        try {
            kafkaTemplate.send(
                    TOPIC_USER_STATUS_UPDATED,
                    event.getUserId().toString(),
                    objectMapper.writeValueAsString(event));
        } catch (Exception e) {
            throw new RuntimeException("Serialize / send Kafka failed", e);
        }
    }

    public void publishUserLocked(UserAccountLockedEvent event) {
        try {
            kafkaTemplate.send(
                    TOPIC_USER_LOCKED,
                    event.getUserId().toString(), // Sử dụng UserId làm message key để đảm bảo thứ tự xử lý (Ordering)
                    objectMapper.writeValueAsString(event));
        } catch (Exception e) {
            throw new RuntimeException("Serialize / send Kafka failed for UserLockedEvent", e);
        }
    }

    public void publishUserUnlocked(UserAccountUnlockedEvent event) {
        try {
            kafkaTemplate.send(
                    TOPIC_USER_UNLOCKED,
                    event.getUserId().toString(),
                    objectMapper.writeValueAsString(event));
        } catch (Exception e) {
            throw new RuntimeException("Send Kafka failed for UserUnlockedEvent", e);
        }
    }

    public void publishUserRoleAssigned(UserRoleAssignedEvent event) {
        try {
            kafkaTemplate.send(
                    TOPIC_USER_ROLE_ASSIGNED,
                    event.getUserId().toString(),
                    objectMapper.writeValueAsString(event));
        } catch (Exception e) {
            throw new RuntimeException("Kafka send failed for UserRoleAssignedEvent", e);
        }
    }
}
