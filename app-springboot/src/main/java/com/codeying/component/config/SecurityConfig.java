package com.codeying.component.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Spring Security 配置类
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(org.springframework.security.config.Customizer.withDefaults()) // 开启跨域支持
            .csrf(csrf -> csrf.disable()) // 禁用CSRF，方便API测试
            .authorizeHttpRequests(auth -> auth
                // 放行注册、登录和项目列表
                .requestMatchers("/api/student/register", "/api/student/login", "/api/project/list").permitAll()
                // API 接口需认证
                .requestMatchers("/api/**").authenticated()
                // 其他请求（如 /admin, /hello）放行，由原有拦截器处理或默认允许
                .anyRequest().permitAll()
            )
            .exceptionHandling(ex -> ex
                // 未认证时返回JSON 401，而不是跳转登录页
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setContentType("application/json;charset=UTF-8");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("{\"code\":401, \"success\":false, \"message\":\"未登录或登录过期\"}");
                })
            );
        return http.build();
    }
}
