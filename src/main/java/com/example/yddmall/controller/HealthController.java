package com.example.yddmall.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/health")
    public String health() {
        return "OK";
    }

    @GetMapping("/api/health")
    public Map<String, String> apiHealth() {
        return Map.of("status", "UP", "service", "yddmall-backend");
    }
}

