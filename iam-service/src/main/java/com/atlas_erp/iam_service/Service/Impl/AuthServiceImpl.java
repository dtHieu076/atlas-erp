package com.atlas_erp.iam_service.Service.Impl;

import com.atlas_erp.iam_service.Dto.Request.Auth.LoginRequest;
import com.atlas_erp.iam_service.Dto.Request.Auth.LogoutRequest;
import com.atlas_erp.iam_service.Dto.Request.Auth.RefreshTokenRequest;
import com.atlas_erp.iam_service.Dto.Response.BaseSuccessResponse;
import com.atlas_erp.iam_service.Dto.Response.Auth.LoginResponse;
import com.atlas_erp.iam_service.Dto.Response.Auth.RefreshTokenResponse;
import com.atlas_erp.iam_service.Service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    @Override
    public LoginResponse login(LoginRequest request) {
        LoginResponse response = new LoginResponse();
        response.setAccessToken("jwt-token");
        response.setRefreshToken("refresh-token");
        response.setExpiresIn(3600L);

        response.setUserId("dummy-user-id"); // sau này map từ DB / auth result
        response.setUsername(request.getUsername());
        response.setStatus("ACTIVE");

        return response;
    }

    @Override
    public BaseSuccessResponse logout(LogoutRequest request) {
        return BaseSuccessResponse.success();
    }

    @Override
    public RefreshTokenResponse refresh(RefreshTokenRequest request) {
        RefreshTokenResponse response = new RefreshTokenResponse();
        response.setAccessToken("new-access-token");
        response.setExpiresIn(3600L);
        return response;
    }

}
