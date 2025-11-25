package com.example.yddmall.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j整合Swagger3 Api接口文档配置类
 * @author Hva
 */
@Configuration
public class Knife4jConfig {

    /**
     * 创建了一个api接口的分组
     * 除了配置文件方式创建分组，也可以通过注册bean创建分组
     */
    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                // 分组名称
                .group("user")
                // 接口请求路径规则
                .pathsToMatch("/user/**")
                .packagesToScan("com.example.yddmall.controller")
                .build();
    }

    @Bean
    public GroupedOpenApi categoryApi() {
        return GroupedOpenApi.builder()
                // 分组名称
                .group("item-category")
                // 接口请求路径规则
                .pathsToMatch("/item-category/**")
                .packagesToScan("com.example.yddmall.controller")
                .build();
    }

    @Bean
    public GroupedOpenApi merchantApi() {
        return GroupedOpenApi.builder()
                // 分组名称
                .group("merchant")
                // 接口请求路径规则
                .pathsToMatch("/merchant/**")
                .packagesToScan("com.example.yddmall.controller")
                .build();
    }

    @Bean
    public GroupedOpenApi roleApi() {
        return GroupedOpenApi.builder()
                // 分组名称
                .group("role")
                // 接口请求路径规则
                .pathsToMatch("/role/**")
                .packagesToScan("com.example.yddmall.controller")
                .build();
    }

    @Bean
    public GroupedOpenApi propApi() {
        return GroupedOpenApi.builder()
                // 分组名称
                .group("item-prop")
                // 接口请求路径规则
                .pathsToMatch("/item-prop/**")
                .packagesToScan("com.example.yddmall.controller")
                .build();
    }

    @Bean
    public GroupedOpenApi skuApi() {
        return GroupedOpenApi.builder()
                // 分组名称
                .group("item-sku")
                // 接口请求路径规则
                .pathsToMatch("/item-sku/**")
                .packagesToScan("com.example.yddmall.controller")
                .build();
    }

    /**
     * 配置基本信息
     */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        // 标题
                        .title("商城接口文档")
                        // 描述Api接口文档的基本信息
                        .description("商城后端API接口服务")
                        // 版本
                        .version("v1.0.0")
                        // 设置OpenAPI文档的联系信息，姓名，邮箱。
                        .contact(new Contact().name("Yy").email("1745499590@qq.com"))
                        // 设置OpenAPI文档的许可证信息，包括许可证名称为"Apache 2.0"，许可证URL为"http://springdoc.org"。
                        .license(new License().name("Apache 2.0").url("http://springdoc.org"))
                );
    }
}
