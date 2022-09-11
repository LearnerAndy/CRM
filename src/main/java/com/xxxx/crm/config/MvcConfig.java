package com.xxxx.crm.config;

import com.xxxx.crm.interceptors.NoLoginInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 登录拦截器
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Bean
    public NoLoginInterceptor noLoginInterceptor() {
        return new NoLoginInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //用于设置拦截器的过滤路径规则
        registry.addInterceptor(noLoginInterceptor()).addPathPatterns("/**")
                //排除的路径
                .excludePathPatterns("/index", "/user/login", "/css/**", "/js/**", "/images/**", "/lib/**");
    }
}
