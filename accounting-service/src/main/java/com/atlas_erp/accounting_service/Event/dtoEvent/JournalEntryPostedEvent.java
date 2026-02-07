package com.atlas_erp.accounting_service.Event.dtoEvent;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;
import java.time.Instant;

@Data
@Builder
public class JournalEntryPostedEvent {
    private UUID entryId;
    private String description;
    private String postedBy;
    private Instant postedAt;
    // Để các service khác filter (PAYROLL, STOCK, PRODUCTION)
    private String referenceType;
}