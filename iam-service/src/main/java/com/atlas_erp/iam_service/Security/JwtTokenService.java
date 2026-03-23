package com.atlas_erp.iam_service.Security;

import java.util.UUID;

public interface JwtTokenService {

    String generateAccessToken(UUID userId, String email, UUID sessionId);

    String generateRefreshToken(UUID userId, UUID sessionId);

    Long getAccessTokenExpiry();

    boolean validateRefreshToken(String refreshToken);

    UUID getUserIdFromRefreshToken(String refreshToken);

    UUID getSessionIdFromToken(String token);

    UUID getUserIdFromAccessToken(String token);
}
