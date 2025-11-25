package com.example.yddmall.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                // 1. 使用 lambda 形式关闭 CSRF
                .csrf(CsrfConfigurer::disable)

                // 2. 放行白名单
                .authorizeHttpRequests(req -> req
//                        .requestMatchers("/item-category/**", "/user/**").permitAll()
                        .requestMatchers("/**").permitAll()
                        .anyRequest().authenticated()
                )

                // 3. 表单登录（如需要）
                .formLogin(form -> form
                        .loginPage("/login")
                        .permitAll()
                );

        return http.build();
    }
}
