package com.atlas_erp.iam_service.Entity;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "role_permission", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "role_id", "permission_id" })
})
@Data
public class RolePermissionEntity {
    UUID roleId;
    UUID permissionId;
}
