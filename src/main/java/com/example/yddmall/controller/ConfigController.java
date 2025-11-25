package com.example.yddmall.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.yddmall.config.ApiResponse;


@RestController
@RequestMapping("/config")
public class ConfigController {
    
    @Value("${geo.tianditu.browser-key}")
    private String tiandituBrowserKey;
    
    @Value("${geo.tianditu.base-url}")
    private String tiandituBaseUrl;
    
    @Value("${delivery.origin.lat}")
    private Double originLat;
    
    @Value("${delivery.origin.lng}")
    private Double originLng;
    
    @GetMapping("/map")
    public ApiResponse<Map<String, Object>> getMapConfig() {
        try {
            Map<String, Object> config = new HashMap<>();
            config.put("tiandituBrowserKey", tiandituBrowserKey);
            config.put("tiandituBaseUrl", tiandituBaseUrl);
            config.put("defaultCenter", Arrays.asList(originLat, originLng));
            config.put("defaultZoom", 13);
            return new ApiResponse<>(200, "success", config);
        } catch (Exception e) {
            return new ApiResponse<>(500, "获取地图配置失败", null);
        }
    }
    
    @GetMapping("/geocode")
    public ApiResponse<Map<String, Object>> getGeocodeConfig() {
        try {
            Map<String, Object> config = new HashMap<>();
            config.put("searchUrl", "/geo/search");
            config.put("reverseUrl", "/geo/reverse");
            config.put("timeout", 10000);
            return new ApiResponse<>(200, "success", config);
        } catch (Exception e) {
            return new ApiResponse<>(500, "获取地理编码配置失败", null);
        }
    }
}