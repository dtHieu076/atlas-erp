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

    @Column(nullable = false, unique = true)
    private String code;
    // Ví dụ: IAM_USER_LOCK

    @Column(nullable = false)
    private String name;
    // Ví dụ: Khóa người dùng

    @Column(nullable = false)
    private String module;
    // iam_service, hr_service

    @Column(nullable = false)
    private String function;
    // User, Role, Payroll

    @Column(nullable = false)
    private String action;
    // CREATE, UPDATE, LOCK

    @Column(nullable = false)
    private String status;
    // ACTIVE | INACTIVE
}
