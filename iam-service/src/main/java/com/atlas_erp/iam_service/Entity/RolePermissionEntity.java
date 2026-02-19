package com.atlas_erp.iam_service.Entity;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "role_permission", uniqueConstraints = @UniqueConstraint(columnNames = { "role_id", "permission_id" }))
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RolePermissionEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "role_id", nullable = false)
    private UUID roleId;

    @Column(name = "permission_id", nullable = false)
    private UUID permissionId;
}
