package com.example.yddmall.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class SecurityUtils {

    private SecurityUtils() {}      // 防止 new

    /** 静态方法，供外部直接调用 */
    public static String getCurrentUsername() {   // ← 方法名必须是这个
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserDetails) {
            return ((UserDetails) auth.getPrincipal()).getUsername();
        }
        return "system";   // 匿名或未登录
    }
}
