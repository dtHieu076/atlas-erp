package com.atlas_erp.iam_service.Dto.Response.Auth;

import lombok.Data;

@Data
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private Long expiresIn;

    private String userId;
    private String username;
    private String status;

    // Added per feedback
    private String roleId;
    private String roleName;
}
