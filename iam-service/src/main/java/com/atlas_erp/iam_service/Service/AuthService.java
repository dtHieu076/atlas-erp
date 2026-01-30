package com.atlas_erp.iam_service.Service;

import com.atlas_erp.iam_service.Dto.Request.Auth.LoginRequest;
import com.atlas_erp.iam_service.Dto.Request.Auth.LogoutRequest;
import com.atlas_erp.iam_service.Dto.Request.Auth.RefreshTokenRequest;
import com.atlas_erp.iam_service.Dto.Response.BaseSuccessResponse;
import com.atlas_erp.iam_service.Dto.Response.Auth.LoginResponse;
import com.atlas_erp.iam_service.Dto.Response.Auth.RefreshTokenResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    BaseSuccessResponse logout(LogoutRequest request);

    RefreshTokenResponse refresh(RefreshTokenRequest request);
}

