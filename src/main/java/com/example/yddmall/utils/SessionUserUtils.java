package com.example.yddmall.utils;

import jakarta.servlet.http.HttpServletRequest;

public class SessionUserUtils {
        /**
     * 从 request 中获取当前用户ID（支持多种方式）
     */
    public static Long getUserId(HttpServletRequest request) {
        if (request == null) {
            System.out.println("[DEBUG] SessionUserUtils: request is null");
            return null;
        }
        
        // 方式1：优先从 Authorization header 中获取JWT token
        String authHeader = request.getHeader("Authorization");
        System.out.println("[DEBUG] SessionUserUtils: Authorization header = " + authHeader);
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7).trim();
            System.out.println("[DEBUG] SessionUserUtils: Extracted token = " + token.substring(0, Math.min(20, token.length())) + "...");
            
            Long userId = JwtUtil.getUserIdFromToken(token);
            System.out.println("[DEBUG] SessionUserUtils: Parsed userId from JWT = " + userId);
            
            if (userId != null) {
                return userId;
            }
        }
        
        // 方式2：兜底从 X-User-Id header 中获取
        String userIdHeader = request.getHeader("X-User-Id");
        System.out.println("[DEBUG] SessionUserUtils: X-User-Id header = " + userIdHeader);
        
        if (userIdHeader != null && !userIdHeader.trim().isEmpty()) {
            try {
                Long userId = Long.parseLong(userIdHeader.trim());
                System.out.println("[DEBUG] SessionUserUtils: Parsed userId from X-User-Id = " + userId);
                return userId;
            } catch (NumberFormatException e) {
                System.out.println("[DEBUG] SessionUserUtils: Invalid X-User-Id format: " + userIdHeader);
            }
        }
        
        System.out.println("[DEBUG] SessionUserUtils: No valid userId found in request");
        return null;
    }
}

