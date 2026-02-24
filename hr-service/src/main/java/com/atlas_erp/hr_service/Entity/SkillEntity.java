package com.atlas_erp.hr_service.Entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "skill")
@Data
public class SkillEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer maxLevel;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private SkillGroupEntity group;

    private String description;

    private String status; // ACTIVE / INACTIVE

    private Instant createdAt;
    private Instant updatedAt;
    private String updatedBy;
}
