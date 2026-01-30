package com.atlas_erp.iam_service.Controller;

import com.atlas_erp.iam_service.Dto.Request.Auth.LoginRequest;
import com.atlas_erp.iam_service.Dto.Request.Auth.LogoutRequest;
import com.atlas_erp.iam_service.Dto.Request.Auth.RefreshTokenRequest;
import com.atlas_erp.iam_service.Dto.Response.Auth.LoginResponse;
import com.atlas_erp.iam_service.Dto.Response.Auth.RefreshTokenResponse;
import com.atlas_erp.iam_service.Dto.Response.BaseSuccessResponse;
import com.atlas_erp.iam_service.Service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/logout")
    public BaseSuccessResponse logout(@RequestBody LogoutRequest request) {
        authService.logout(request);
        return new BaseSuccessResponse();
    }

    @PostMapping("/refresh")
    public RefreshTokenResponse refresh(@RequestBody RefreshTokenRequest request) {
        return authService.refresh(request);
    }
}
