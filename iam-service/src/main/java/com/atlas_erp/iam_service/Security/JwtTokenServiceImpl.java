package com.atlas_erp.iam_service.Security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtTokenServiceImpl implements JwtTokenService {

    private final Key signingKey;
    private final Long accessTokenExpiration;
    private final Long refreshTokenExpiration;
    private final String issuer;

    public JwtTokenServiceImpl(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration}") Long accessTokenExpiration,
            @Value("${jwt.refresh-token-expiration}") Long refreshTokenExpiration,
            @Value("${jwt.issuer}") String issuer) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
        this.issuer = issuer;
    }

    @Override
    public String generateAccessToken(UUID userId, String email, UUID sessionId) {

        Instant now = Instant.now();

        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuer(issuer)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(accessTokenExpiration)))
                .claim("email", email)
                .claim("sessionId", sessionId.toString())
                .claim("type", "ACCESS")
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public String generateRefreshToken(UUID userId, UUID sessionId) {

        Instant now = Instant.now();

        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuer(issuer)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(refreshTokenExpiration)))
                .claim("sessionId", sessionId.toString())
                .claim("type", "REFRESH")
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public Long getAccessTokenExpiry() {
        return accessTokenExpiration;
    }

    @Override
    public boolean validateRefreshToken(String refreshToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .requireIssuer(issuer)
                    .build()
                    .parseClaimsJws(refreshToken);

            String type = Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(refreshToken)
                    .getBody()
                    .get("type", String.class);

            return "REFRESH".equals(type);

        } catch (Exception ex) {
            // signature invalid / expired / malformed
            return false;
        }
    }

    @Override
    public UUID getUserIdFromRefreshToken(String refreshToken) {

        try {
            String subject = Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .requireIssuer(issuer)
                    .build()
                    .parseClaimsJws(refreshToken)
                    .getBody()
                    .getSubject();

            UUID userId = UUID.fromString(subject);

            // Backward compatibility - old refresh tokens without sessionId
            return userId;
        } catch (Exception ex) {
            throw new IllegalStateException("Invalid refresh token", ex);
        }
    }

    @Override
    public UUID getSessionIdFromToken(String token) {
        try {
            String sessionIdStr = Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("sessionId", String.class);

            return sessionIdStr != null ? UUID.fromString(sessionIdStr) : null;
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public UUID getUserIdFromAccessToken(String token) {
        try {
            String subject = Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
            return UUID.fromString(subject);
        } catch (Exception ex) {
            return null;
        }
    }

}
