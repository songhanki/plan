package com.board.plan.core.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;
    private final String issuer;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.access-token.expiration}") long accessTokenExpiration,
            @Value("${jwt.refresh-token.expiration}") long refreshTokenExpiration,
            @Value("${jwt.issuer}") String issuer) {
        
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
        this.issuer = issuer;
    }

    /**
     * Access Token 생성
     */
    public String generateAccessToken(String memberId, String email, String roleId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessTokenExpiration);

        return Jwts.builder()
                .subject(memberId)
                .claim("email", email)
                .claim("roleId", roleId)
                .claim("type", "access")
                .issuer(issuer)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    /**
     * Refresh Token 생성
     */
    public String generateRefreshToken(String memberId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshTokenExpiration);

        return Jwts.builder()
                .subject(memberId)
                .claim("type", "refresh")
                .issuer(issuer)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    /**
     * JWT 토큰에서 사용자 ID(member_id) 추출
     */
    public String getMemberIdFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims.getSubject();
    }

    /**
     * JWT 토큰에서 이메일 추출
     */
    public String getEmailFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims.get("email", String.class);
    }

    /**
     * JWT 토큰에서 역할 ID 추출
     */
    public String getRoleIdFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims.get("roleId", String.class);
    }

    /**
     * JWT 토큰에서 토큰 타입 추출
     */
    public String getTokenTypeFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims.get("type", String.class);
    }

    /**
     * JWT 토큰 유효성 검증
     */
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("JWT token is unsupported: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.warn("JWT token is malformed: {}", e.getMessage());
        } catch (SecurityException e) {
            log.warn("JWT signature is invalid: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("JWT token compact is illegal: {}", e.getMessage());
        }
        return false;
    }

    /**
     * Access Token인지 확인
     */
    public boolean isAccessToken(String token) {
        try {
            String tokenType = getTokenTypeFromToken(token);
            return "access".equals(tokenType);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Refresh Token인지 확인
     */
    public boolean isRefreshToken(String token) {
        try {
            String tokenType = getTokenTypeFromToken(token);
            return "refresh".equals(tokenType);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * JWT 토큰의 만료 시간 반환
     */
    public Date getExpirationFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims.getExpiration();
    }

    /**
     * JWT 토큰이 만료되었는지 확인
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = getExpirationFromToken(token);
            return expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * JWT Claims 파싱
     */
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Access Token 만료 시간 반환 (밀리초)
     */
    public long getAccessTokenExpiration() {
        return accessTokenExpiration;
    }

    /**
     * Refresh Token 만료 시간 반환 (밀리초)
     */
    public long getRefreshTokenExpiration() {
        return refreshTokenExpiration;
    }
} 