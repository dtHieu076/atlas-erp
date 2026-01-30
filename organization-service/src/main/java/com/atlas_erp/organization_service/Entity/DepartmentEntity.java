package com.atlas_erp.organization_service.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
import java.time.LocalDate;

@Entity
@Table(name = "department")
@Data
public class DepartmentEntity {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID plantId;

    private String code;
    private String name;
    private String costCenterCode;

    private LocalDate validFrom;
    private LocalDate validTo;
}
