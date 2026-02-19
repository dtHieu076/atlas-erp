package com.atlas_erp.iam_service.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "role")
@Data
@Getter
@Setter
public class RoleEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private String roleCode;

    @Column(nullable = false)
    private String roleName;

    private String description;

    @Enumerated(EnumType.STRING)
    private RoleStatus status; // ACTIVE | INACTIVE

    @Enumerated(EnumType.STRING)
    private DataScope dataScope;
    // ALL | PLANT | DEPARTMENT

    public enum RoleStatus {
        ACTIVE,
        INACTIVE
    }

    public enum DataScope {
        ALL,
        PLANT,
        DEPARTMENT
    }
}
