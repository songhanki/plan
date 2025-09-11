package com.board.plan.core.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 개발 환경 전용 보안 설정
 * app.security.dev.swagger-test-enabled=true 일 때만 활성화
 */
@Slf4j
@Configuration
@EnableWebSecurity
@ConditionalOnProperty(name = "app.security.dev.swagger-test-enabled", havingValue = "true")
public class DevSecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Primary
    public SecurityFilterChain devFilterChain(HttpSecurity http) throws Exception {
        log.info("🚨🚨🚨 개발 전용 보안 설정이 활성화되었습니다! 🚨🚨🚨");
        log.info("🚨 모든 API 엔드포인트에 대한 인증이 비활성화됩니다!");
        log.info("🚨 Swagger UI에서 자유롭게 테스트할 수 있습니다!");
        log.info("🚨 운영 환경에서는 절대 사용하지 마세요!");
        
        http
            // CSRF 비활성화
            .csrf(AbstractHttpConfigurer::disable)
            
            // CORS 허용
            .cors(AbstractHttpConfigurer::disable)
            
            // 모든 요청 허용
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            
            // 폼 로그인 비활성화
            .formLogin(AbstractHttpConfigurer::disable)
            
            // HTTP Basic 인증 비활성화
            .httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }
}
