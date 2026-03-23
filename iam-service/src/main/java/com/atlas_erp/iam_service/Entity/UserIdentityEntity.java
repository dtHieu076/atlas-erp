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

    @Column(nullable = false)
    private UUID userId;

    // Đã xóa AuthProvider vì chỉ dùng Google
    @Column(nullable = false, unique = true)
    private String googleId; // Thay thế cho providerUid

    @Column(nullable = false)
    private String email;

    private String lockReason;
    private Instant lockedAt;
}
