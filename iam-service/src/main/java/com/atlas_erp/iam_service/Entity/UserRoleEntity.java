package com.atlas_erp.iam_service.Entity;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "user_role", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "user_id", "role_id" })
})
@Data
public class UserRoleEntity {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID userId;
    private UUID roleId;
}
