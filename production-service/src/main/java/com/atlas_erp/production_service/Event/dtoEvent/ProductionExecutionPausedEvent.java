package com.atlas_erp.production_service.Event.dtoEvent;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;
import java.time.Instant;

@Data
@Builder
public class ProductionExecutionPausedEvent {
    private UUID executionId;
    private String reason;
    private Instant pausedAt;
}