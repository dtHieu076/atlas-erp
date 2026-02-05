package com.atlas_erp.organization_service.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
import java.time.LocalDate;
import java.time.Instant;

@Entity
@Table(name = "organization")
@Data
@Builder
public class OrganizationEntity {

    @Id
    @GeneratedValue
    private UUID id;

    private String code;
    private String name;
    private String taxCode;
    private String address;
    private String legalRepresentative;

    private LocalDate validFrom;
    private LocalDate validTo;

    private String status;

    private Instant createdAt;
    private UUID createdBy;
}
