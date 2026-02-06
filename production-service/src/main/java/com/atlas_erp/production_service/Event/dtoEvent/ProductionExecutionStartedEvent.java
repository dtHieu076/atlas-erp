package com.atlas_erp.production_service.Event.dtoEvent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;
import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductionExecutionStartedEvent {
    private UUID executionId;
    private UUID orderId;
    private UUID productId;
    private String teamId;
    private Instant startTime;
}