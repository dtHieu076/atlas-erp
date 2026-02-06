package com.atlas_erp.production_service.Event.dtoEvent;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class ProductionOrderCreatedEvent {
    private UUID orderId;
    private UUID planId;
    private UUID productId;
    private BigDecimal quantity;
    private String teamId;
}