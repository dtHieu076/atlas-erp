package com.atlas_erp.production_service.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
import java.math.BigDecimal;

@Entity
@Table(name = "production_order")
@Data
public class ProductionOrderEntity {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID productionPlanId;
    private UUID productId;

    private BigDecimal quantity;
    private String status;
}
