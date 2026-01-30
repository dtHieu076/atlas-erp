package com.atlas_erp.inventory_service.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
import java.time.LocalDate;

@Entity
@Table(name = "inventory_batch")
@Data
public class InventoryBatchEntity {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID itemId;

    private String batchCode;
    private String origin;

    private LocalDate manufactureDate;
    private LocalDate expiryDate;
}
