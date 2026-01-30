package com.atlas_erp.production_service.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "production_stage")
@Data
public class ProductionStageEntity {

    @Id
    @GeneratedValue
    private UUID id;

    private String code;
    private String name;
    private Integer sequence;
}
