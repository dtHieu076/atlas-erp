package com.atlas_erp.production_service.Event.dtoEvent;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;
import java.time.Instant;

@Data
@Builder
public class ProductionPlanApprovedEvent {
    private UUID planId;
    private String planCode;
    private UUID productId;
    private UUID approvedBy;
    private Instant approvedAt;
}