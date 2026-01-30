package com.atlas_erp.production_service.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
import java.time.LocalDate;
import java.time.Instant;

@Entity
@Table(name = "production_plan")
@Data
public class ProductionPlanEntity {

    @Id
    @GeneratedValue
    private UUID id;

    private String planCode;
    private Integer version;

    private LocalDate startDate;
    private LocalDate endDate;

    private String status;
    private UUID approvedBy;
    private Instant approvedAt;
}
