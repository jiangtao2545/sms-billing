package com.jiangtao.smsbilling.config;

import com.jiangtao.smsbilling.interceptor.AppAuthInterceptor;
import com.jiangtao.smsbilling.interceptor.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    private JwtInterceptor jwtInterceptor;
    @Autowired
    private AppAuthInterceptor appAuthInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/auth/login", "/api/openapi/**");

        registry.addInterceptor(appAuthInterceptor)
                .addPathPatterns("/api/openapi/**");
    }
}
