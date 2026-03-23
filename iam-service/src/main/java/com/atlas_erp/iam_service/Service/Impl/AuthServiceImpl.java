package com.atlas_erp.iam_service.Service.Impl;

import com.atlas_erp.iam_service.Cache.dtoCache.CachedUser;
import com.atlas_erp.iam_service.Cache.dtoCache.CachedUserIdentity;
import com.atlas_erp.iam_service.Cache.serviceCache.UserIdentityCacheService;
import com.atlas_erp.iam_service.Cache.serviceCache.UserCacheService;
import com.atlas_erp.iam_service.Dto.UserRoleDto;
import com.atlas_erp.iam_service.Dto.Request.Auth.LoginRequest;
import com.atlas_erp.iam_service.Dto.Request.Auth.LogoutRequest;
import com.atlas_erp.iam_service.Dto.Request.Auth.RefreshTokenRequest;
import com.atlas_erp.iam_service.Dto.Response.BaseSuccessResponse;
import com.atlas_erp.iam_service.Dto.Response.Auth.LoginResponse;
import com.atlas_erp.iam_service.Dto.Response.Auth.RefreshTokenResponse;
import com.atlas_erp.iam_service.Dto.Response.User.UserRolesResponse;
import com.atlas_erp.iam_service.Entity.AppUserEntity;
import com.atlas_erp.iam_service.Entity.UserIdentityEntity;
import com.atlas_erp.iam_service.Repository.AppUserRepository;
import com.atlas_erp.iam_service.Repository.UserIdentityRepository;
import com.atlas_erp.iam_service.Security.GoogleTokenVerifier;
import com.atlas_erp.iam_service.Security.JwtTokenService;
import com.atlas_erp.iam_service.Service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserIdentityRepository userIdentityRepository;
    private final AppUserRepository appUserRepository;
    private final UserIdentityCacheService userIdentityCacheService;
    private final UserCacheService userCacheService;
    private final JwtTokenService jwtTokenService;
    private final GoogleTokenVerifier googleTokenVerifier;

    @Override
    public LoginResponse login(LoginRequest request) {
        // Bước 1: Verify Google credential
        GoogleTokenVerifier.GoogleTokenPayload payload = googleTokenVerifier.verify(request.getCredential());
        String googleId = payload.googleId();
        String email = payload.email();

        System.out.println("DEBUG: GoogleID: " + payload.googleId());
        System.out.println("DEBUG: Email: " + payload.email());

        // Bước 2: Load UserIdentity (Redis -> DB) by googleId
        CachedUserIdentity identity = userIdentityCacheService.get(googleId);
        if (identity == null) {
            UserIdentityEntity dbIdentity = userIdentityRepository
                    .findByGoogleId(googleId)
                    .orElseThrow(() -> new IllegalStateException("User identity not found"));

            identity = CachedUserIdentity.from(dbIdentity);
            userIdentityCacheService.put(identity);
        }

        // Bước 3: Load AppUser (Redis -> DB)
        CachedUser user = userCacheService.getUser(identity.getUserId());
        if (user == null) {
            AppUserEntity dbUser = appUserRepository
                    .findById(identity.getUserId())
                    .orElseThrow(() -> new IllegalStateException("User not found"));

            user = CachedUser.from(dbUser);
            userCacheService.putUser(user);
        }

        // Bước 4: Check status ACTIVE
        if (!"ACTIVE".equalsIgnoreCase(user.getStatus())) {
            throw new IllegalStateException("User is not active");
        }

        // Bước 5: Generate sessionId
        UUID sessionId = UUID.randomUUID();

        // Bước 6: Generate tokens with sessionId
        String accessToken = jwtTokenService.generateAccessToken(
                user.getUserId(),
                identity.getEmail(),
                sessionId);

        String refreshToken = jwtTokenService.generateRefreshToken(
                user.getUserId(),
                sessionId);

        // Bước 7: Response
        LoginResponse response = new LoginResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setExpiresIn(jwtTokenService.getAccessTokenExpiry());
        response.setUserId(user.getUserId().toString());
        response.setUsername(email);
        response.setStatus(user.getStatus());

        // Add primary role info (first role)
        UserRolesResponse userRoles = userCacheService.getUserRoles(user.getUserId());
        if (userRoles != null && !userRoles.getRoles().isEmpty()) {
            UserRoleDto primaryRole = userRoles.getRoles().get(0);
            response.setRoleId(primaryRole.getRoleId());
            response.setRoleName(primaryRole.getRoleName());
        }

        return response;
    }

    @Override
    public BaseSuccessResponse logout(LogoutRequest request) {
        // 4. Return success
        return BaseSuccessResponse.success();
    }

    @Override
    public RefreshTokenResponse refresh(RefreshTokenRequest request) {
        RefreshTokenResponse response = null;
        return response;
    }

}
