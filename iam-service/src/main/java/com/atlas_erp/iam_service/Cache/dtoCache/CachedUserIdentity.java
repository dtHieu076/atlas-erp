package com.atlas_erp.iam_service.Cache.dtoCache;

import com.atlas_erp.iam_service.Entity.UserIdentityEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor // BẮT BUỘC: Để Jackson có thể tạo instance khi deserialize
@AllArgsConstructor
public class CachedUserIdentity implements Serializable {

    private UUID userId;
    private String googleId; // Thay thế providerUid
    private String email;

    public static CachedUserIdentity from(UserIdentityEntity identity) {
        return CachedUserIdentity.builder()
                .userId(identity.getUserId())
                .googleId(identity.getGoogleId())
                .email(identity.getEmail())
                .build();
    }
}
