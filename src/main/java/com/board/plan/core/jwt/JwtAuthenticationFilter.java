package com.board.plan.core.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        String token = getTokenFromRequest(request);
        
        if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token) && 
            jwtTokenProvider.isAccessToken(token)) {
            
            try {
                String memberId = jwtTokenProvider.getMemberIdFromToken(token);
                String email = jwtTokenProvider.getEmailFromToken(token);
                String roleId = jwtTokenProvider.getRoleIdFromToken(token);
                
                // JWT 토큰 정보로 사용자 객체 생성 (DB 조회 없이)
                JwtUserDetails userDetails = JwtUserDetails.builder()
                        .memberId(memberId)
                        .email(email)
                        .roleId(roleId)
                        .build();
                
                // 권한 생성 (역할 ID를 그대로 사용하고 ROLE_ 형식도 함께 추가)
                List<SimpleGrantedAuthority> authorities = List.of(
                    new SimpleGrantedAuthority(roleId), // role-admin, role-manager, role-user
                    new SimpleGrantedAuthority("ROLE_" + roleId.replace("role-", "").toUpperCase()) // ROLE_ADMIN, ROLE_MANAGER, ROLE_USER
                );
                
                // Authentication 객체 생성
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // SecurityContext에 인증 정보 설정
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
                log.debug("Set Authentication for member: {} with role: {}", memberId, roleId);
                
            } catch (Exception e) {
                log.error("Cannot set user authentication: {}", e.getMessage());
                SecurityContextHolder.clearContext();
            }
        }
        
        filterChain.doFilter(request, response);
    }

    /**
     * HTTP 요청에서 JWT 토큰 추출
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
} 