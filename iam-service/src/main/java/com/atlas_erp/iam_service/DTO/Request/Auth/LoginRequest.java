package com.atlas_erp.iam_service.Dto.Request.Auth;

import lombok.Data;

@Data
public class LoginRequest {
    /**
     * Identity provider
     * VD: GOOGLE
     */
    private String provider;

    /**
     * Unique user id from provider
     * VD: Google sub / user id
     */
    private String providerUid;

    /**
     * Email từ provider (metadata)
     */
    private String email;
}
