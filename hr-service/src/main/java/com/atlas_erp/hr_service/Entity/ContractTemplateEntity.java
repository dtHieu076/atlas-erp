package com.atlas_erp.hr_service.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "contract_template")
@Data
public class ContractTemplateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String code; // HD-TV, HD-XD...

    @Column(nullable = false)
    private String name; // Hợp đồng thử việc...

    private Integer defaultDuration; // 12
    private String durationType; // MONTH, YEAR, NONE

    private String groupName; // Chính thức, Thử việc...

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String policy; // Phúc lợi, BHXH...

    private Boolean isActive; // true/false

    private Instant createdAt;
    private String createdBy;

    private Instant lastUpdated;
}
