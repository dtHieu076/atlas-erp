package com.atlas_erp.iam_service.Entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_identity")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserIdentityEntity {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID userId;

    private String provider;

    private String providerUid;

    private String email;

    @Builder.Default
    private boolean locked = false; // Trạng thái khóa

    private String lockReason; // Lý do khóa

    private Instant lockedAt; // Thời điểm khóa (để Audit sau này)
}
