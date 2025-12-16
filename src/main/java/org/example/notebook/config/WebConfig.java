package org.example.notebook.config;

import org.example.notebook.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/**")  // 拦截所有请求
                .excludePathPatterns("/login", "/api/login", "/images/**", "/css/**", "/js/**", "/register", "/api/register"); // 排除不需要拦截的路径
    }
}