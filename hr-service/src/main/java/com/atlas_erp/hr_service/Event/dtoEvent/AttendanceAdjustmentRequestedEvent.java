package com.atlas_erp.hr_service.Event.dtoEvent;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class AttendanceAdjustmentRequestedEvent {
    private String adjustmentId;
    private UUID employeeId;
    private LocalDate date;
    private String adjustmentType;
    private String oldValue;
    private String newValue;
    private String reason;
    private String requestedBy;
}