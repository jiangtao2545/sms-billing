package com.jiangtao.smsbilling;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.jiangtao.smsbilling.mapper")
public class SmsBillingApplication {
    public static void main(String[] args) {
        SpringApplication.run(SmsBillingApplication.class, args);
    }
}
