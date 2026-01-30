package com.atlas_erp.inventory_service.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
import java.time.Instant;

@Entity
@Table(name = "stock_document")
@Data
public class StockDocumentEntity {

    @Id
    @GeneratedValue
    private UUID id;

    private String documentType;
    private String status;

    private Instant createdAt;
    private Instant approvedAt;
    private UUID approvedBy;
}
