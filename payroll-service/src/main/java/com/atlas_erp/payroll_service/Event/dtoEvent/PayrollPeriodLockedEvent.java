package com.atlas_erp.payroll_service.Event.dtoEvent;

import lombok.Builder;
import lombok.Data;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class PayrollPeriodLockedEvent {
    private UUID periodId;
    private String periodCode;
    private Instant lockedAt;
}