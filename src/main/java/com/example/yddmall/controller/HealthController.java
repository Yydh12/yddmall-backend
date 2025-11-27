package com.example.yddmall.controller;

import com.example.yddmall.config.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/healthz")
public class HealthController {
    @GetMapping
    public ApiResponse<String> ok() {
        return new ApiResponse<>(200, "ok", "ok");
    }
}

