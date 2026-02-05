package com.atlas_erp.organization_service.Event.serviceEvent;

import com.atlas_erp.organization_service.Event.dtoEvent.PlantCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PlantEventService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    // Tên topic nên khớp với cấu hình trong file application.yml của các consumer
    // (IAM, Inventory)
    private static final String TOPIC_PLANT_CREATED = "plant.created";
    // private static final String TOPIC_PLANT_UPDATED = "plant.updated";

    /**
     * Phát sự kiện khi một nhà máy mới được tạo
     */
    public void publishPlantCreatedEvent(PlantCreatedEvent event) {
        log.info("Publishing PlantCreatedEvent for Plant ID: {} in Org: {}",
                event.getPlantId(), event.getOrganizationId());

        try {
            // Key là plantId để đảm bảo các event của cùng 1 nhà máy luôn vào cùng 1
            // partition (thứ tự xử lý)
            kafkaTemplate.send(TOPIC_PLANT_CREATED, event.getPlantId().toString(), event);
        } catch (Exception e) {
            log.error("Failed to publish plant created event: {}", e.getMessage());
        }
    }

    /**
     * Phát sự kiện khi thông tin nhà máy thay đổi (nếu cần)
     */
    public void publishPlantUpdatedEvent(Object event) {
        // Triển khai tương tự khi bạn làm API PUT /plants
    }
}