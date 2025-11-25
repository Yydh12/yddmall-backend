package com.example.yddmall.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.lang.NonNull;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${upload.base-path}")
    private String basePath;

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        // 访问 /images/123/xxx.jpg -> 磁盘 /data/uploads/123/xxx.jpg
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + basePath + "/");
    }
}