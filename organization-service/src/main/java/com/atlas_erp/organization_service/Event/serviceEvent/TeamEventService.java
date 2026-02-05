package com.atlas_erp.organization_service.Event.serviceEvent;

import com.atlas_erp.organization_service.Event.dtoEvent.TeamCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TeamEventService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Phát sự kiện khi một Tổ/Đội mới được tạo thành công
     */
    public void publishTeamCreatedEvent(TeamCreatedEvent event) {
        log.info("Publishing TeamCreatedEvent for Team: {} [ID: {}]", event.getName(), event.getTeamId());

        try {
            // Key: teamId (dùng String để Kafka partition nhất quán)
            // Topic: team.created
            kafkaTemplate.send("team.created", event.getTeamId().toString(), event);
        } catch (Exception e) {
            log.error("Failed to send TeamCreatedEvent for ID {}: {}", event.getTeamId(), e.getMessage());
            // Tùy chọn: Có thể ném lỗi để Transaction DB rollback nếu việc phát event là
            // bắt buộc
        }
    }
}