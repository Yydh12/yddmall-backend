package com.example.yddmall.controller;

import com.example.yddmall.config.ApiResponse;
import com.example.yddmall.service.GeoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/geo")
public class GeoController {

    @Autowired
    private GeoService geoService;

    @GetMapping("/search")
    public ApiResponse<String> search(@RequestParam("q") String q,
                                      @RequestParam(value = "limit", required = false) Integer limit) {
        String body = geoService.search(q, limit);
        return new ApiResponse<>(200, "ok", body);
    }

    @GetMapping("/reverse")
    public ApiResponse<String> reverse(@RequestParam("lat") double lat,
                                       @RequestParam("lon") double lon) {
        String body = geoService.reverse(lat, lon);
        return new ApiResponse<>(200, "ok", body);
    }
}