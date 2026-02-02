package com.atlas_erp.iam_service.Cache.dtoCache;

import com.atlas_erp.iam_service.Entity.UserIdentityEntity;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
@Builder
public class CachedUserIdentity implements Serializable {

    private UUID userId;
    private String provider;
    private String providerUid;
    private String email;

    public static CachedUserIdentity from(UserIdentityEntity identity) {
        return CachedUserIdentity.builder()
                .userId(identity.getUserId())
                .provider(identity.getProvider())
                .providerUid(identity.getProviderUid())
                .email(identity.getEmail())
                .build();
    }
}
