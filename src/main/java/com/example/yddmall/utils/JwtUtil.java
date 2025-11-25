package com.example.yddmall.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

public class JwtUtil {
    // 请替换为实际签名密钥，这里仅为示例密钥，可在配置文件读取
    private static final String SECRET_KEY = "yourSecretKey";

    /**
     * 从JWT token中解析出userId（支持 userId 为 Long/Integer/String）
     */
    public static Long getUserIdFromToken(String token) {
        try {
            System.out.println("[DEBUG] JwtUtil: Parsing token with SECRET_KEY = " + SECRET_KEY);
            
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY.getBytes())
                    .parseClaimsJws(token)
                    .getBody();
            
            System.out.println("[DEBUG] JwtUtil: Claims = " + claims);
            
            Object userIdVal = claims.get("userId");
            System.out.println("[DEBUG] JwtUtil: userId from claims = " + userIdVal + " (type: " + (userIdVal != null ? userIdVal.getClass().getSimpleName() : "null") + ")");
            
            if (userIdVal instanceof Integer) {
                return ((Integer) userIdVal).longValue();
            } else if (userIdVal instanceof Long) {
                return (Long) userIdVal;
            } else if (userIdVal instanceof String) {
                return Long.parseLong((String) userIdVal);
            }
            
            // 如果 userId 字段不存在，尝试其他可能的字段名
            Object subVal = claims.getSubject();
            System.out.println("[DEBUG] JwtUtil: subject from claims = " + subVal);
            if (subVal != null) {
                try {
                    return Long.parseLong(subVal.toString());
                } catch (NumberFormatException e) {
                    System.out.println("[DEBUG] JwtUtil: Subject is not a valid userId: " + subVal);
                }
            }
            
            return null;
        } catch (Exception e) {
            System.err.println("[ERROR] JwtUtil: Failed to parse token: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 生成测试用的 JWT token（仅用于调试）
     */
    public static String generateToken(Long userId) {
        try {
            return Jwts.builder()
                    .setSubject(userId.toString())
                    .claim("userId", userId)
                    .signWith(io.jsonwebtoken.SignatureAlgorithm.HS256, SECRET_KEY.getBytes())
                    .compact();
        } catch (Exception e) {
            System.err.println("[ERROR] JwtUtil: Failed to generate test token: " + e.getMessage());
            return null;
        }
    }
}
