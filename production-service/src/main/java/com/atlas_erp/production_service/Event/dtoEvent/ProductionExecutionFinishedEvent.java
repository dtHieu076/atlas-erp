package com.atlas_erp.production_service.Event.dtoEvent;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;
import java.time.Instant;

@Data
@Builder
public class ProductionExecutionFinishedEvent {
    private UUID executionId;
    private UUID orderId;
    private UUID productId;
    private BigDecimal goodQuantity;
    private BigDecimal defectQuantity;
    private Instant finishedAt;
}