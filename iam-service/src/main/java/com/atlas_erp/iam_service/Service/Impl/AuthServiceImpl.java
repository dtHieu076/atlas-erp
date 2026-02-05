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
import com.atlas_erp.iam_service.Event.dtoEvent.UserLoggedOutEvent;
import com.atlas_erp.iam_service.Event.serviceEvent.UserEventService;
import com.atlas_erp.iam_service.Repository.AppUserRepository;
import com.atlas_erp.iam_service.Repository.UserIdentityRepository;

import com.atlas_erp.iam_service.Security.JwtTokenService;
import com.atlas_erp.iam_service.Service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserIdentityRepository userIdentityRepository;
    private final AppUserRepository appUserRepository;
    private final UserIdentityCacheService userIdentityCacheService;
    private final UserCacheService userCacheService;
    private final JwtTokenService jwtTokenService;
    private final UserEventService userEventService;

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

        // 1. Validate refreshToken
        String refreshToken = request.getRefreshToken();
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new IllegalArgumentException("Refresh token is required");
        }

        if (!jwtTokenService.validateRefreshToken(refreshToken)) {
            throw new IllegalStateException("Invalid refresh token");
        }

        // 2. Extract userId từ refreshToken
        UUID userId = jwtTokenService.getUserIdFromRefreshToken(refreshToken);

        // 3. Publish logout event
        userEventService.publishUserLoggedOut(
                new UserLoggedOutEvent(
                        userId,
                        Instant.now(),
                        "USER_LOGOUT"));

        // 4. Return success
        return BaseSuccessResponse.success();
    }

    @Override
    public RefreshTokenResponse refresh(RefreshTokenRequest request) {

        // 1. Validate refresh token exist
        String refreshToken = request.getRefreshToken();
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new IllegalArgumentException("Refresh token is required");
        }

        // 2. Validate refresh token (signature, exp, issuer, type)
        if (!jwtTokenService.validateRefreshToken(refreshToken)) {
            throw new IllegalStateException("Invalid refresh token");
        }

        // 3. Extract userId from refresh token
        UUID userId = jwtTokenService.getUserIdFromRefreshToken(refreshToken);

        /// 4. Load UserIdentity (Redis → DB) để lấy email
        CachedUserIdentity identity = userIdentityCacheService.getByUserId(userId);

        if (identity == null) {
            UserIdentityEntity dbIdentity = userIdentityRepository
                    .findByUserId(userId)
                    .orElseThrow(() -> new IllegalStateException("User identity not found"));

            identity = CachedUserIdentity.from(dbIdentity);
            userIdentityCacheService.put(identity);
        }

        // 5. Generate new access token
        String newAccessToken = jwtTokenService.generateAccessToken(
                userId,
                identity.getEmail());

        // 5. Build response
        RefreshTokenResponse response = new RefreshTokenResponse();
        response.setAccessToken(newAccessToken);
        response.setExpiresIn(jwtTokenService.getAccessTokenExpiry());

        return response;
    }

}
