package com.atlas_erp.iam_service.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "user_identity")
@Data
public class UserIdentityEntity {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID userId;

    private String provider;

    private String providerUid;

    private String email;
}
