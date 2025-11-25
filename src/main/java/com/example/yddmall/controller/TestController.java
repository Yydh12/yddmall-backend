package com.example.yddmall.controller;

import com.example.yddmall.config.ApiResponse;
import com.example.yddmall.utils.JwtUtil;
import com.example.yddmall.utils.SessionUserUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
public class TestController {
    
    /**
     * 生成测试用的JWT token
     */
    @GetMapping("/generateToken/{userId}")
    public ApiResponse<String> generateTestToken(@PathVariable Long userId) {
        try {
            String token = JwtUtil.generateToken(userId);
            return new ApiResponse<>(200, "生成成功", token);
        } catch (Exception e) {
            return new ApiResponse<>(500, e.getMessage(), null);
        }
    }
    
    /**
     * 测试token解析
     */
    @GetMapping("/verifyToken")
    public ApiResponse<Long> verifyToken(HttpServletRequest request) {
        try {
            Long userId = SessionUserUtils.getUserId(request);
            return new ApiResponse<>(200, "解析成功", userId);
        } catch (Exception e) {
            return new ApiResponse<>(500, e.getMessage(), null);
        }
    }
    
    /**
     * 获取所有请求头信息
     */
    @GetMapping("/headers")
    public ApiResponse<String> getHeaders(HttpServletRequest request) {
        try {
            StringBuilder headers = new StringBuilder();
            request.getHeaderNames().asIterator().forEachRemaining(name -> {
                headers.append(name).append(": ").append(request.getHeader(name)).append("\n");
            });
            return new ApiResponse<>(200, "获取成功", headers.toString());
        } catch (Exception e) {
            return new ApiResponse<>(500, e.getMessage(), null);
        }
    }
}