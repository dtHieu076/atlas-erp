package com.atlas_erp.inventory_service.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "stock_transaction")
@Data
public class StockTransactionEntity {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID stockDocumentId;
    private UUID warehouseId;
    private UUID itemId;
    private UUID batchId;

    private BigDecimal quantity;
    private Instant transactionDate;

    private String referenceType;
    private UUID referenceId;
}
