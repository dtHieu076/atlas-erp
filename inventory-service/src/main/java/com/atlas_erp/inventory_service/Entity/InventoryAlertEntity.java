package com.atlas_erp.inventory_service.Entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "inventory_alert")
@Data
public class InventoryAlertEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String alertType; // LOW_STOCK, OVER_STOCK, AGING
    private UUID warehouseId;
    private UUID itemId;
    
    private BigDecimal currentQuantity;
    private BigDecimal threshold; // Định mức cảnh báo
    
    private Instant generatedAt;
    private String status; // NEW, READ, RESOLVED
}