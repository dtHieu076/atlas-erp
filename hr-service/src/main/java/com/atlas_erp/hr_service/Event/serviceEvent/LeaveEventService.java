package com.atlas_erp.hr_service.Event.serviceEvent;

import com.atlas_erp.hr_service.Event.dtoEvent.LeaveRequestCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class LeaveEventService {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishLeaveRequestCreated(LeaveRequestCreatedEvent event) {
        log.info("Publishing LeaveRequestCreated for employee: {}", event.getEmployeeId());
        kafkaTemplate.send("hr.leave.requested", event.getEmployeeId().toString(), event);
    }
}