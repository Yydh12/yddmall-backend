package com.example.yddmall.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionUserUtils {
    private static final Logger log = LoggerFactory.getLogger(SessionUserUtils.class);

    public static Long getUserId(HttpServletRequest request) {
        if (request == null) {
            log.debug("request is null");
            return null;
        }

        String authHeader = request.getHeader("Authorization");
        log.debug("Authorization header = {}", authHeader);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7).trim();
            Long userId = JwtUtil.getUserIdFromToken(token);
            log.debug("Parsed userId from JWT header = {}", userId);
            if (userId != null) {
                return userId;
            }
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("Authorization".equalsIgnoreCase(c.getName()) || "token".equalsIgnoreCase(c.getName())) {
                    String v = c.getValue();
                    String token = v != null && v.startsWith("Bearer ") ? v.substring(7).trim() : v;
                    Long userId = JwtUtil.getUserIdFromToken(token);
                    log.debug("Parsed userId from JWT cookie = {}", userId);
                    if (userId != null) {
                        return userId;
                    }
                }
            }
        }

        String userIdHeader = request.getHeader("X-User-Id");
        log.debug("X-User-Id header = {}", userIdHeader);
        if (userIdHeader != null && !userIdHeader.trim().isEmpty()) {
            try {
                Long userId = Long.parseLong(userIdHeader.trim());
                log.debug("Parsed userId from X-User-Id = {}", userId);
                return userId;
            } catch (NumberFormatException e) {
                log.debug("Invalid X-User-Id format: {}", userIdHeader);
            }
        }

        log.debug("No valid userId found in request");
        return null;
    }
}

