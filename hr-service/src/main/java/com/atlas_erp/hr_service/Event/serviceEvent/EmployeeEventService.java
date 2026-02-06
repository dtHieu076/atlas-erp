package com.atlas_erp.hr_service.Event.serviceEvent;

import com.atlas_erp.hr_service.Event.dtoEvent.EmployeeCreatedEvent;
import com.atlas_erp.hr_service.Event.dtoEvent.EmployeeMovedEvent;
import com.atlas_erp.hr_service.Event.dtoEvent.EmployeeUpdatedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmployeeEventService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishEmployeeCreatedEvent(EmployeeCreatedEvent event) {
        log.info("Publishing EmployeeCreatedEvent for: {}", event.getEmployeeCode());
        try {
            // Topic: hr.employee.created
            kafkaTemplate.send("hr.employee.created", event.getEmployeeId().toString(), event);
        } catch (Exception e) {
            log.error("Failed to publish event: {}", e.getMessage());
        }
    }

    public void publishEmployeeUpdatedEvent(EmployeeUpdatedEvent event) {
        kafkaTemplate.send("hr.employee.updated", event.getEmployeeId().toString(), event);
    }

    public void publishEmployeeMovedEvent(EmployeeMovedEvent event) {
        // Topic này các service IAM, Production, Payroll sẽ lắng nghe
        kafkaTemplate.send("hr.employee.moved", event.getEmployeeId().toString(), event);
    }
}
