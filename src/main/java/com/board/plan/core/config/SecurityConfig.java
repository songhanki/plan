package com.board.plan.core.config;

import com.board.plan.core.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CSRF 비활성화 (JWT 사용으로 인해)
            .csrf(AbstractHttpConfigurer::disable)
            
            // CORS 설정
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // 세션 관리 정책 설정 (JWT 사용으로 세션 비활성화)
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // 인증/인가 규칙 설정
            .authorizeHttpRequests(auth -> auth
                // 인증 없이 접근 가능한 엔드포인트
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/v1/tokens/validate").permitAll()  // 토큰 검증 엔드포인트
                .requestMatchers("/api/members").permitAll() // 회원 가입만 허용
                
                // Swagger/OpenAPI 문서 접근 허용
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                
                // 정적 리소스 접근 허용
                .requestMatchers("/static/**", "/css/**", "/js/**", "/images/**").permitAll()
                
                // 헬스체크 등 기본 엔드포인트
                .requestMatchers("/actuator/health").permitAll()
                
                // ============= 역할 기반 인가 규칙 =============
                
                // 관리자만 접근 가능한 엔드포인트
                .requestMatchers(HttpMethod.POST, "/api/members").permitAll() // 회원가입은 모두 허용
                .requestMatchers(HttpMethod.PUT, "/api/members/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/members/**").hasRole("ADMIN")
                
                // 팀장 또는 관리자만 접근 가능한 엔드포인트
                .requestMatchers("/api/leave-approvals/**").hasAnyRole("MANAGER", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/members").hasAnyRole("MANAGER", "ADMIN")
                
                // 로그인된 사용자는 모두 접근 가능한 엔드포인트
                .requestMatchers("/api/schedules/**").authenticated()
                .requestMatchers("/api/leave-requests/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/members/member/**").authenticated() // 자신의 정보 조회
                
                // 그 외 모든 요청은 인증 필요
                .anyRequest().authenticated()
            )
            
            // JWT 인증 필터 추가
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            
            // 예외 처리 설정
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint((request, response, authException) -> {
                    log.warn("인증되지 않은 요청: {} {}", request.getMethod(), request.getRequestURI());
                    response.setStatus(401);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("""
                        {
                            "error": "Unauthorized",
                            "message": "인증이 필요합니다. 로그인 후 다시 시도해주세요.",
                            "timestamp": "%s"
                        }
                        """.formatted(System.currentTimeMillis()));
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    log.warn("권한 없는 접근 시도: {} {}", request.getMethod(), request.getRequestURI());
                    response.setStatus(403);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("""
                        {
                            "error": "Forbidden",
                            "message": "해당 리소스에 접근할 권한이 없습니다.",
                            "timestamp": "%s"
                        }
                        """.formatted(System.currentTimeMillis()));
                })
            );

        return http.build();
    }

    /**
     * CORS 설정
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 허용할 오리진 (개발 환경)
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        
        // 허용할 HTTP 메서드
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        
        // 허용할 헤더
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        // 인증 정보 포함 허용
        configuration.setAllowCredentials(true);
        
        // preflight 요청 캐시 시간
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
} 