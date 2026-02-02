package com.atlas_erp.iam_service.Service.Impl;

import com.atlas_erp.iam_service.Cache.dtoCache.CachedUser;
import com.atlas_erp.iam_service.Cache.dtoCache.CachedUserIdentity;
import com.atlas_erp.iam_service.Cache.serviceCache.UserIdentityCacheService;
import com.atlas_erp.iam_service.Cache.serviceCache.UserCacheService;

import com.atlas_erp.iam_service.Dto.Request.Auth.LoginRequest;
import com.atlas_erp.iam_service.Dto.Request.Auth.LogoutRequest;
import com.atlas_erp.iam_service.Dto.Request.Auth.RefreshTokenRequest;
import com.atlas_erp.iam_service.Dto.Response.BaseSuccessResponse;
import com.atlas_erp.iam_service.Dto.Response.Auth.LoginResponse;
import com.atlas_erp.iam_service.Dto.Response.Auth.RefreshTokenResponse;

import com.atlas_erp.iam_service.Entity.AppUserEntity;
import com.atlas_erp.iam_service.Entity.UserIdentityEntity;

import com.atlas_erp.iam_service.Repository.AppUserRepository;
import com.atlas_erp.iam_service.Repository.UserIdentityRepository;

import com.atlas_erp.iam_service.Security.JwtTokenService;
import com.atlas_erp.iam_service.Service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserIdentityRepository userIdentityRepository;
    private final AppUserRepository appUserRepository;
    private final UserIdentityCacheService userIdentityCacheService;
    private final UserCacheService userCacheService;
    private final JwtTokenService jwtTokenService;

    @Override
    public LoginResponse login(LoginRequest request) {
        // 1. Validate provider (chỉ cho GOOGLE)
        if (!"GOOGLE".equalsIgnoreCase(request.getProvider())) {
            throw new IllegalArgumentException("Unsupported provider");
        }

        String provider = request.getProvider();
        String providerUid = request.getProviderUid();

        // 2. Load UserIdentity (Redis → DB)
        CachedUserIdentity identity = userIdentityCacheService.get(provider, providerUid);
        if (identity == null) {
            UserIdentityEntity dbIdentity = userIdentityRepository
                    .findByProviderAndProviderUid(provider, providerUid)
                    .orElseThrow(() -> new IllegalStateException("User identity not found"));

            identity = CachedUserIdentity.from(dbIdentity);
            userIdentityCacheService.put(identity);

        }

        // 3. Load AppUser (Redis → DB)
        CachedUser user = userCacheService.getUser(identity.getUserId());

        if (user == null) {
            AppUserEntity dbUser = appUserRepository
                    .findById(identity.getUserId())
                    .orElseThrow(() -> new IllegalStateException("User not found"));

            user = CachedUser.from(dbUser);
            userCacheService.putUser(user);
        }

        // 4. Check user status
        if (!"ACTIVE".equalsIgnoreCase(user.getStatus())) {
            throw new IllegalStateException("User is not active");
        }

        // 5. Generate tokens
        String accessToken = jwtTokenService.generateAccessToken(
                user.getUserId(),
                identity.getEmail());

        String refreshToken = jwtTokenService.generateRefreshToken(
                user.getUserId());

        // 6. Build response
        LoginResponse response = new LoginResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setExpiresIn(jwtTokenService.getAccessTokenExpiry());
        response.setUserId(user.getUserId().toString());
        response.setUsername(identity.getEmail());
        response.setStatus(user.getStatus());

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
