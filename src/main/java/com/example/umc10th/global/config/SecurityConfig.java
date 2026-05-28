package com.example.umc10th.global.config;

import com.example.umc10th.global.security.CustomAccessDenied;
import com.example.umc10th.global.security.CustomEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    // 로그인 없이 접근 가능한 경로 (Swagger - 개발 편의상 허용)
    private final String[] swaggerUris = {
            "/swagger-ui/**",
            "/swagger-resources/**",
            "/v3/api-docs/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(requests -> requests
                        // Public API: 회원가입
                        .requestMatchers(HttpMethod.POST, "/api/auth/signup").permitAll()
                        // Swagger
                        .requestMatchers(swaggerUris).permitAll()
                        // Private API: 그 외 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                )
                // 인증/인가 실패 시 통일된 JSON 응답 반환
                .exceptionHandling(handler -> handler
                        .authenticationEntryPoint(new CustomEntryPoint())
                        .accessDeniedHandler(new CustomAccessDenied())
                )
                .formLogin(form -> form
                        .defaultSuccessUrl("/swagger-ui/index.html", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}