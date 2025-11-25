package com.example.yddmall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.yddmall.mapper")
public class YddmallApplication {

    public static void main(String[] args) {
        SpringApplication.run(YddmallApplication.class, args);
    }

}
