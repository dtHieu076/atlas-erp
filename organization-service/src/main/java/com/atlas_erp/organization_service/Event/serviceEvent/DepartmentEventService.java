package com.atlas_erp.organization_service.Event.serviceEvent;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import com.atlas_erp.organization_service.Event.dtoEvent.DepartmentCreatedEvent;

@Service
@RequiredArgsConstructor
public class DepartmentEventService {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishDepartmentCreatedEvent(DepartmentCreatedEvent event) {
        kafkaTemplate.send("department.created", event.getDepartmentId().toString(), event);
    }
}