package com.atlas_erp.inventory_service.Entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "stock_document_item")
@Data
public class StockDocumentItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "stock_document_id", nullable = false)
    private UUID stockDocumentId;

    @Column(name = "item_id", nullable = false)
    private UUID itemId;

    private BigDecimal quantity;
    private BigDecimal unitPrice;
}