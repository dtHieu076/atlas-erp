package com.atlas_erp.production_service.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
import java.math.BigDecimal;

@Entity
@Table(name = "routing")
@Data
public class RoutingEntity {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID productId;
    private UUID productionStageId;

    private BigDecimal standardTime;
}
