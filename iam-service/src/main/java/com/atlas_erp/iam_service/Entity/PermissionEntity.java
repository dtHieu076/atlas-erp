package com.atlas_erp.iam_service.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "permission")
@Data
public class PermissionEntity {

    @Id
    @GeneratedValue
    private UUID id;

    private String module;
    private String function;
    private String action;
}
