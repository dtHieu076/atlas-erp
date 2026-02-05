package com.atlas_erp.iam_service.Event.serviceEvent;

import com.atlas_erp.iam_service.Event.dtoEvent.UserCreatedEvent;
import com.atlas_erp.iam_service.Event.dtoEvent.UserLoggedOutEvent;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserEventService {

    private static final String TOPIC_USER_CREATED = "iam.user.created";
    private static final String TOPIC_USER_LOGGED_OUT = "iam.user.logged_out";

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

}
