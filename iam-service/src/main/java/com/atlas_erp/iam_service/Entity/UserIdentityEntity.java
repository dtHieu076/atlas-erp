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

    @Enumerated(EnumType.STRING)
    private AuthProvider provider; // LOCAL | GOOGLE | AZURE

    @Column(nullable = false)
    private String providerUid;

    @Column(nullable = false)
    private String email;

    private String lockReason;
    private Instant lockedAt;

    public enum AuthProvider {
        LOCAL,
        GOOGLE,
        AZURE
    }
}
