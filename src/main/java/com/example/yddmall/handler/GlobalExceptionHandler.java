package com.example.yddmall.handler;

import com.example.yddmall.config.ApiResponse;
import com.example.yddmall.config.ResponseCode;
import com.example.yddmall.utils.ResponseUtils;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * 全局异常处理器，统一处理异常并返回标准响应格式
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理404异常
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<Void> handleNoHandlerFoundException(NoHandlerFoundException e) {
        return ResponseUtils.error(ResponseCode.NOT_FOUND);
    }

    /**
     * 处理参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        StringBuilder errorMsg = new StringBuilder();

        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            errorMsg.append(fieldError.getField()).append(":").append(fieldError.getDefaultMessage()).append(";");
        }

        return ResponseUtils.error(ResponseCode.BAD_REQUEST, errorMsg.toString());
    }

    /**
     * 处理自定义业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public ApiResponse<Void> handleBusinessException(BusinessException e) {
        return ResponseUtils.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理其他未捕获的异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleException(Exception e) {
        // 实际项目中可以在这里记录异常日志
        e.printStackTrace();
        return ResponseUtils.error(ResponseCode.INTERNAL_SERVER_ERROR);
    }
}
