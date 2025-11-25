package com.example.yddmall.handler;

import com.example.yddmall.config.ResponseCode;

/**
 * 自定义业务异常类，用于处理业务逻辑中的异常情况
 */
public class BusinessException extends RuntimeException {
    private int code;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(ResponseCode responseCode) {
        super(responseCode.getMessage());
        this.code = responseCode.getCode();
    }

    public int getCode() {
        return code;
    }
}

