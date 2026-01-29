package com.atlas_erp.iam_service.DTO.Response;

import lombok.Data;

@Data
public class RefreshTokenResponse {
    private String accessToken;
    private Long expiresIn;
}
