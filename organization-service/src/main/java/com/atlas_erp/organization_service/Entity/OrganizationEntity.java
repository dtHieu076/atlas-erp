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
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 50)
    private String code;

    @Column(nullable = false, length = 255)
    private String name;

    private String taxCode;
    private String address;
    private String legalRepresentative;

    private LocalDate validFrom;
    private LocalDate validTo;

    @Column(nullable = false)
    private String status; // ACTIVE | INACTIVE

    private Instant createdAt;
    private UUID createdBy;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }
}
