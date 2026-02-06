package com.atlas_erp.hr_service.Event.serviceEvent;

import com.atlas_erp.hr_service.Event.dtoEvent.EmployeeContractCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ContractEventService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishContractCreatedEvent(EmployeeContractCreatedEvent event) {
        log.info("Publishing ContractCreatedEvent for employee: {}", event.getEmployeeId());
        kafkaTemplate.send("hr.contract.created", event.getEmployeeId().toString(), event);
    }
}