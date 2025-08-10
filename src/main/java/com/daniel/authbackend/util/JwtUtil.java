package com.daniel.authbackend.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
    private static final String ROLE_CLAIM = "role";

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access.expiration.ms}")
    private long accessTokenExpirationMs;

    @Value("${jwt.refresh.expiration.ms}")
    private long refreshTokenExpirationMs;

    private final Map<String, Boolean> invalidatedTokens = new ConcurrentHashMap<>();

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String username, String role) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (role == null || role.trim().isEmpty()) {
            throw new IllegalArgumentException("Role cannot be null or empty");
        }

        logger.debug("Generating access token for user: {} with role: {}", username, role);

        String token = Jwts.builder()
                .setSubject(username)
                .claim(ROLE_CLAIM, role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();

        logger.debug("Access token generated successfully for user: {}", username);
        return token;
    }

    public String generateRefreshToken(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }

        logger.debug("Generating refresh token for user: {}", username);

        String token = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();

        logger.debug("Refresh token generated successfully for user: {}", username);
        return token;
    }

    public String extractUsername(String token) {
        try {
            String username = Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
            logger.debug("Extracted username: {} from token", username);
            return username;
        } catch (JwtException | IllegalArgumentException e) {
            logger.warn("Failed to extract username from token: {}", e.getMessage());
            return null;
        }
    }

    public String extractRole(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            Object roleObj = claims.get(ROLE_CLAIM);
            String role = roleObj != null ? roleObj.toString() : null;
            logger.debug("Extracted role: {} from token", role);
            return role;
        } catch (JwtException | IllegalArgumentException e) {
            logger.warn("Failed to extract role from token: {}", e.getMessage());
            return null;
        }
    }

    public boolean isTokenValid(String token, String username) {
        if (token == null || username == null) {
            logger.warn("Token or username is null");
            return false;
        }

        if (invalidatedTokens.getOrDefault(token, false)) {
            logger.debug("Token is invalidated for user: {}", username);
            return false;
        }

        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            boolean isValid = claims.getSubject().equals(username) && !isTokenExpired(token);
            logger.debug("Token validation for user {}: {}", username, isValid);
            return isValid;
        } catch (JwtException | IllegalArgumentException e) {
            logger.warn("Token validation failed for user {}: {}", username, e.getMessage());
            return false;
        }
    }

    public void invalidateToken(String token) {
        if (token != null) {
            invalidatedTokens.put(token, true);
            logger.debug("Token invalidated successfully");
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            Date expiration = Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();
            boolean expired = expiration.before(new Date());
            logger.debug("Token expiration check: {}", expired ? "expired" : "valid");
            return expired;
        } catch (JwtException | IllegalArgumentException e) {
            logger.warn("Failed to check token expiration: {}", e.getMessage());
            return true;
        }
    }
}