package com.example.yddmall.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${upload.access-url:/uploads}")
    private String accessUrl;

    @Value("${upload.base-path}")
    private String basePath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String normalizedBase = basePath.endsWith("/") || basePath.endsWith("\\") ? basePath : basePath + "/";
        registry.addResourceHandler(accessUrl + "/**")
                .addResourceLocations("file:" + normalizedBase)
                .setCachePeriod(0);
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + normalizedBase)
                .setCachePeriod(0);
    }
}
