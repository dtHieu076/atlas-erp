package com.atlas_erp.organization_service.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
import java.time.LocalDate;
import java.time.Instant;

@Entity
@Table(name = "plant")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlantEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private UUID organizationId;

    @Column(nullable = false, length = 50)
    private String code;

    @Column(nullable = false, length = 255)
    private String name;

    private String address;

    @Column(nullable = false)
    private String status; // ACTIVE | INACTIVE

    private LocalDate validFrom;
    private LocalDate validTo;

    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }
}
