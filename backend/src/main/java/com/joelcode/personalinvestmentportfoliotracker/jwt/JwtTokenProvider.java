package com.joelcode.personalinvestmentportfoliotracker.jwt;

import com.joelcode.personalinvestmentportfoliotracker.entities.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

/**
 * JWT Token Provider - Creates, parses, and validates JWT tokens.
 * Uses JJWT library with explicit algorithm specification to avoid deprecation warnings.
 */
@Component
public class JwtTokenProvider {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration}")
    private int jwtExpirationInMs;

    /**
     * Get the signing key from the secret.
     * Uses HS256 algorithm which requires 256-bit (32 byte) key.
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    /**
     * Generate JWT token for a user
     */
    public String generateToken(User user) {
        Date now = new Date(System.currentTimeMillis());
        Date expiryDate = new Date(System.currentTimeMillis() + jwtExpirationInMs);

        return Jwts.builder()
                .setSubject(user.getUserId().toString())
                .claim("email", user.getEmail())
                .claim("username", user.getUsername())
                .claim("roles", user.getRoles().name())
                .claim("fullName", user.getFullName())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)  // ← FIXED: Explicit algorithm
                .compact();
    }

    /**
     * Get expiration date from token
     */
    public LocalDateTime getExpirationDate(String token) {
        return LocalDateTime.ofInstant(
                getClaims(token).getExpiration().toInstant(),
                java.time.ZoneId.systemDefault()
        );
    }

    /**
     * Parse and get claims from token
     */
    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Extract user ID from token
     */
    public UUID getUserIdFromToken(String token) {
        Claims claim = getClaims(token);
        return UUID.fromString(claim.getSubject());
    }

    /**
     * Extract username from token
     */
    public String getUsernameFromToken(String token) {
        Claims claim = getClaims(token);
        return claim.get("username", String.class);
    }

    /**
     * Validate JWT token signature and expiration
     */
    public boolean validateToken(String token) {
        try {
            getClaims(token);  // If this doesn't throw, token is valid
            return true;
        } catch (SecurityException ex) {
            throw new SecurityException("Invalid JWT signature: " + ex.getMessage());
        } catch (MalformedJwtException ex) {
            throw new MalformedJwtException("Invalid JWT token: " + ex.getMessage());
        } catch (ExpiredJwtException ex) {
            throw new ExpiredJwtException(ex.getHeader(), ex.getClaims(), "JWT token is expired: " + ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            throw new UnsupportedJwtException("Unsupported JWT token: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("JWT claims string is empty: " + ex.getMessage());
        }
    }

    /**
     * Check if token has expired
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claim = getClaims(token);
            Date expiration = claim.getExpiration();
            return expiration.before(new Date(System.currentTimeMillis()));
        } catch (Exception e) {
            return true;
        }
    }
}