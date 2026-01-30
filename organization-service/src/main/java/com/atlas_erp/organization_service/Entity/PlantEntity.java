package com.atlas_erp.organization_service.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
import java.time.LocalDate;

@Entity
@Table(name = "plant")
@Data
public class PlantEntity {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID organizationId;

    private String code;
    private String name;
    private String location;

    private LocalDate validFrom;
    private LocalDate validTo;
}
