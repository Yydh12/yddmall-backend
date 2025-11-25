package com.example.yddmall.config;

/**
 * 响应状态码枚举
 */
public enum ResponseCode {
    // 成功状态码
    SUCCESS(200, "操作成功"),

    // 客户端错误
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "权限不足"),
    NOT_FOUND(404, "资源不存在"),

    // 服务器错误
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),
    SERVICE_UNAVAILABLE(503, "服务暂时不可用");

    private final int code;
    private final String message;

    ResponseCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}

