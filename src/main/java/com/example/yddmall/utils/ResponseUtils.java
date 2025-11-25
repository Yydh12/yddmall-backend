package com.example.yddmall.utils;

import com.example.yddmall.config.ApiResponse;
import com.example.yddmall.config.ResponseCode;

/**
 * 响应工具类，用于快速构建ApiResponse对象
 */
public class ResponseUtils {

    /**
     * 成功响应，不带数据
     */
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getMessage(), null);
    }

    /**
     * 成功响应，带数据
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getMessage(), data);
    }

    /**
     * 成功响应，自定义消息和数据
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(ResponseCode.SUCCESS.getCode(), message, data);
    }

    /**
     * 错误响应，使用预定义的响应码
     */
    public static <T> ApiResponse<T> error(ResponseCode responseCode) {
        return new ApiResponse<>(responseCode.getCode(), responseCode.getMessage(), null);
    }

    /**
     * 错误响应，使用预定义的响应码和自定义消息
     */
    public static <T> ApiResponse<T> error(ResponseCode responseCode, String message) {
        return new ApiResponse<>(responseCode.getCode(), message, null);
    }

    /**
     * 错误响应，自定义状态码和消息
     */
    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }

    /**
     * 未授权响应，不带自定义消息
     */
    public static <T> ApiResponse<T> unauthorized() {
        return new ApiResponse<>(ResponseCode.UNAUTHORIZED.getCode(), ResponseCode.UNAUTHORIZED.getMessage(), null);
    }

    /**
     * 未授权响应，带自定义消息
     */
    public static <T> ApiResponse<T> unauthorized(String message) {
        return new ApiResponse<>(ResponseCode.UNAUTHORIZED.getCode(), message, null);
    }
}

