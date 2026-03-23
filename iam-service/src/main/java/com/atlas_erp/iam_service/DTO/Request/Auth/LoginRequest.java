package com.atlas_erp.iam_service.Dto.Request.Auth;

import lombok.Data;

/**
 * Google ID Token from client SDK
 */
@Data
public class LoginRequest {
    /**
     * Google ID Token (credential) from Google SDK
     */
    private String credential;
}
