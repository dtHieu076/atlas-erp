package com.atlas_erp.hr_service.Event.serviceEvent;

import com.atlas_erp.hr_service.Event.dtoEvent.OvertimeRequestCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OvertimeEventService {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishOvertimeRequested(OvertimeRequestCreatedEvent event) {
        kafkaTemplate.send("hr.overtime.requested", event.getEmployeeId().toString(), event);
    }
}