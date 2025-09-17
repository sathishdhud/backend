package com.hotelworks.service;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBlacklistService {
    
    private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();
    
    /**
     * Add token to blacklist (for logout)
     */
    public void blacklistToken(String token) {
        blacklistedTokens.add(token);
    }
    
    /**
     * Check if token is blacklisted
     */
    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }
    
    /**
     * Remove expired tokens from blacklist (cleanup)
     * This should be called periodically to free memory
     */
    public void cleanupExpiredTokens() {
        // In a production environment, you would implement a more sophisticated
        // cleanup mechanism, possibly using a scheduled task and checking token expiration
        // For now, this is a placeholder method
    }
}