package com.reverse.nsu.service;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Date;

@Component
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.salt}")
    private String salt;

    @Value("${jwt.access-expiry-minutes}")
    private long accessExpiryMinutes;

    @Value("${jwt.refresh-expiry-minutes}")
    private long refreshExpiryMinutes;

    /**
     * secret + salt를 SHA-256으로 해시하여 HS256 서명 키를 생성합니다.
     */
    private SecretKey getKey() {
        try {
            String combined = secret + salt;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] keyBytes = digest.digest(combined.getBytes(StandardCharsets.UTF_8));
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 알고리즘을 사용할 수 없습니다.", e);
        }
    }

    public String generateAccessToken(String userId) {
        long expiryMs = accessExpiryMinutes * 60 * 1000L;
        return Jwts.builder()
                .subject(userId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiryMs))
                .signWith(getKey(), Jwts.SIG.HS256)
                .compact();
    }

    public String generateRefreshToken(String userId) {
        long expiryMs = refreshExpiryMinutes * 60 * 1000L;
        return Jwts.builder()
                .subject(userId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiryMs))
                .signWith(getKey(), Jwts.SIG.HS256)
                .compact();
    }

    public LocalDateTime getAccessTokenExpiry() {
        return LocalDateTime.now().plusMinutes(accessExpiryMinutes);
    }

    public LocalDateTime getRefreshTokenExpiry() {
        return LocalDateTime.now().plusMinutes(refreshExpiryMinutes);
    }

    public String getUserId(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean isValid(String token) {
        try {
            Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
