package com.atlas_erp.accounting_service.Event.dtoEvent;

import lombok.Builder;
import lombok.Data;
import java.time.Instant;

@Data
@Builder
public class AccountingPeriodLockedEvent {
    private String periodId;
    private String lockedBy;
    private Instant lockedAt;
}