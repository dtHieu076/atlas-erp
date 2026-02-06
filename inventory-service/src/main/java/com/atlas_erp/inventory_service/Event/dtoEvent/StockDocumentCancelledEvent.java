package com.atlas_erp.inventory_service.Event.dtoEvent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StockDocumentCancelledEvent {
    private UUID stockDocumentId;
    private String documentType;
    private String reason;
    private UUID cancelledBy;
    private Instant cancelledAt;

    // Topic name để quản lý tập trung
    public static final String TOPIC = "inventory.stock-document.cancelled";
}