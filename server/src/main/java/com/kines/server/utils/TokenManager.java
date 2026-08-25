package com.kines.server.utils;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

public class TokenManager {

    private Cache<String, String> cache = Caffeine.newBuilder().expireAfterWrite(24, TimeUnit.HOURS).maximumSize(10_000).build();

    public String createToken(String user) {
        String token = UUID.randomUUID().toString();
        cache.put(token, user);
        return token;
    }

    public String validateToken(String token) {
        if (token == null || token.isEmpty()) return null;

        return cache.getIfPresent(token);
    }

    public void revokeToken(String token) {
        cache.invalidate(token);
    }

    public String extractToken(String url) {
        if (url != null && url.contains("token=")) {
            String[] parts = url.split("token=");
            if (parts.length > 1) {
                return parts[1].split("&")[0];
            }
        }
        return null;
    }
}
