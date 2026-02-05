package com.atlas_erp.iam_service.Security;

import java.util.UUID;

public interface JwtTokenService {

    String generateAccessToken(UUID userId, String email);

    String generateRefreshToken(UUID userId);

    Long getAccessTokenExpiry();

    boolean validateRefreshToken(String refreshToken);

    UUID getUserIdFromRefreshToken(String refreshToken);
}
