package com.atlas_erp.production_service.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
import java.time.Instant;
import java.math.BigDecimal;

@Entity
@Table(name = "production_execution")
@Data
public class ProductionExecutionEntity {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID productionOrderId;
    private UUID productionStageId;

    private UUID teamId;
    private UUID employeeId;

    private Instant startTime;
    private Instant endTime;

    private BigDecimal goodQuantity;
    private BigDecimal defectQuantity;
}
