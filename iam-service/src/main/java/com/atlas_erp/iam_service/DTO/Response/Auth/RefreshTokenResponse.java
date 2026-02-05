package com.atlas_erp.iam_service.Dto.Response.Auth;

import lombok.Data;

@Data
public class RefreshTokenResponse {
    private String accessToken;
    private Long expiresIn;
}

