package com.atlas_erp.iam_service.Dto.Request.Auth;

import lombok.Data;

@Data
public class RefreshTokenRequest {
    private String refreshToken;
}
