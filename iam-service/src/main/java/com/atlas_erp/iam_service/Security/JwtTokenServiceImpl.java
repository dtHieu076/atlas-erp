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
    public String generateAccessToken(UUID userId, String email) {

        Instant now = Instant.now();

        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuer(issuer)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(accessTokenExpiration)))
                .claim("email", email)
                .claim("type", "ACCESS")
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public String generateRefreshToken(UUID userId) {

        Instant now = Instant.now();

        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuer(issuer)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(refreshTokenExpiration)))
                .claim("type", "REFRESH")
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public Long getAccessTokenExpiry() {
        return accessTokenExpiration;
    }
}
