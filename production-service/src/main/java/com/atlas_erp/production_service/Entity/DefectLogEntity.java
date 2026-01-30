package com.atlas_erp.production_service.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
import java.math.BigDecimal;

@Entity
@Table(name = "defect_log")
@Data
public class DefectLogEntity {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID productionExecutionId;

    private String defectCode;
    private String description;

    private BigDecimal quantity;
}
