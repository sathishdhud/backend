package com.hotelworks.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    
    @Value("${jwt.secret:hotelworks-jwt-secret-key-for-authentication-system-2025}")
    private String jwtSecret;
    
    @Value("${jwt.expiration:1800000}") // 30 minutes in milliseconds
    private long jwtExpiration;
    
    /**
     * Generate JWT token for user with 30-minute expiration
     */
    public String generateToken(String userId, String userName, String userTypeId, String userTypeRole) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("userName", userName);
        claims.put("userTypeId", userTypeId);
        if (userTypeRole != null) {
            claims.put("userTypeRole", userTypeRole);
        }
        return createToken(claims, userName);
    }
    
    /**
     * Create JWT token with claims and 30-minute expiration
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    /**
     * Get signing key for JWT
     */
    private Key getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
    /**
     * Extract username from token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    /**
     * Extract user ID from token
     */
    public String extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("userId", String.class));
    }
    
    /**
     * Extract user type ID from token
     */
    public String extractUserTypeId(String token) {
        return extractClaim(token, claims -> claims.get("userTypeId", String.class));
    }
    
    /**
     * Extract user type role from token
     */
    public String extractUserTypeRole(String token) {
        return extractClaim(token, claims -> claims.get("userTypeRole", String.class));
    }
    
    /**
     * Extract expiration date from token
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    /**
     * Extract claim from token
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    /**
     * Extract all claims from token
     */
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Token expired", e);
        } catch (UnsupportedJwtException e) {
            throw new RuntimeException("Token unsupported", e);
        } catch (MalformedJwtException e) {
            throw new RuntimeException("Token malformed", e);
        } catch (SecurityException e) {
            throw new RuntimeException("Token signature invalid", e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Token claims empty", e);
        }
    }
    
    /**
     * Check if token is expired
     */
    private Boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (Exception e) {
            // If we can't extract expiration, consider token expired
            return true;
        }
    }
    
    /**
     * Validate token with enhanced security checks
     */
    public Boolean validateToken(String token, String username) {
        try {
            final String extractedUsername = extractUsername(token);
            return (extractedUsername.equals(username) && !isTokenExpired(token));
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Check if token is valid with enhanced security checks
     */
    public Boolean isTokenValid(String token) {
        try {
            extractAllClaims(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Get token expiration time in milliseconds
     */
    public long getTokenExpirationTime() {
        return jwtExpiration;
    }
}