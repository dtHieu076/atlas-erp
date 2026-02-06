package com.atlas_erp.inventory_service.Entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "inventory_batch")
@Data
public class InventoryBatchEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "item_id", nullable = false)
    private UUID itemId;

    @Column(name = "batch_code", unique = true, nullable = false)
    private String batchCode;

    @Column(name = "origin")
    private String origin;

    @Column(name = "manufacture_date")
    private LocalDate manufactureDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;
}