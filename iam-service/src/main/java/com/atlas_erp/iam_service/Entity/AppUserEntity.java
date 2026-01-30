package com.atlas_erp.iam_service.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "app_user")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppUserEntity {

    @Id
    @GeneratedValue
    private UUID id;

    // FK logic sang HR, KHÔNG JOIN
    private UUID employeeId;

    private String status;

    private Instant createdAt;
}
