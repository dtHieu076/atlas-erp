package com.atlas_erp.organization_service.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "team")
@Data
public class TeamEntity {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID departmentId;

    private String code;
    private String name;
    private String productionType;
}
