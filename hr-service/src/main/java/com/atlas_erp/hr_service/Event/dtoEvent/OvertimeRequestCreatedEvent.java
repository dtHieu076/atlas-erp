package com.atlas_erp.hr_service.Event.dtoEvent;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
public class OvertimeRequestCreatedEvent {
    private UUID requestId;
    private UUID employeeId;
    private LocalDate otDate;
    private LocalTime fromTime;
    private LocalTime toTime;
    private String workflowId;
}