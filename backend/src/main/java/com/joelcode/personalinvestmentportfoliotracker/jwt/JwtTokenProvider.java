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

// This class is responsible for creating, parsing, and validating JWT tokens.
@Component
public class JwtTokenProvider {

    // Inject value from config for token signing
    @Value("${app.jwt.secret}")
    private String jwtSecret;

    // Determines how long the JWT token is valid for.
    @Value("${app.jwt.expiration}")
    private int jwtExpirationInMs;

    // Generate jwt token for user
    public String generateToken(User user) {
        // Create expiry date
        Date expiryDate = new Date(System.currentTimeMillis() + jwtExpirationInMs);

        // Generate secret key
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

        // Build token and sign
        return Jwts.builder()
                .setSubject(user.getUserId().toString())
                .claim("email", user.getEmail())
                .claim("password", user.getPassword())
                .claim("username", user.getUsername())
                .claim("roles", user.getRoles().name())
                .claim("fullName", user.getFullName())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(expiryDate)
                .signWith(key)
                .compact();
    }

    public LocalDateTime getExpirationDate(String token) {
        return LocalDateTime.ofInstant(getClaims(token).getExpiration().toInstant(), java.time.ZoneId.systemDefault());
    }

    public Claims getClaims(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

        return Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

    }

    // Get user id from jwt token
    public UUID getUserIdFromToken(String token){
        Claims claim = getClaims(token);
        return UUID.fromString(claim.getSubject());
    }

    // Get username from jwt token
    public String getUsernameFromToken(String token){
        Claims claim = getClaims(token);
        return claim.get("username", String.class);
    }

    // Validate jwt token
    public boolean validateToken(String token) {
        try {
            getClaims(token); // If this doesn't throw, token is valid
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

    // Validate jwt is not expired
    public boolean isTokenExpire(String token){
        try {
            Claims claim = getClaims(token);
            Date expiration = claim.getExpiration();
            return expiration.before(new Date(System.currentTimeMillis()));
        }
        catch (Exception e){
            return true;
        }
    }
}
