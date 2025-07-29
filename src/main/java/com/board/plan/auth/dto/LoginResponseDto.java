package com.board.plan.auth.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "로그인 응답 DTO")
public class LoginResponseDto {

    @Schema(description = "JWT Access Token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;

    @Schema(description = "JWT Refresh Token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String refreshToken;

    @Schema(description = "토큰 타입", example = "Bearer")
    private String tokenType;

    @Schema(description = "Access Token 만료 시간 (초)", example = "900")
    private Long expiresIn;

    @Schema(description = "사용자 ID", example = "user-001")
    private String memberId;

    @Schema(description = "사용자 이메일", example = "user@example.com")
    private String email;

    @Schema(description = "사용자 이름", example = "홍길동")
    private String name;

    @Schema(description = "사용자 역할", example = "role-user")
    private String roleId;

    @Schema(description = "역할 이름", example = "일반 사용자")
    private String roleName;

    @Schema(description = "로그인 시간")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime loginTime;

    /**
     * 기본 토큰 타입을 Bearer로 설정하는 편의 메서드
     */
    public static LoginResponseDto of(String accessToken, String refreshToken, Long expiresIn,
                                    String memberId, String email, String name, 
                                    String roleId, String roleName) {
        return LoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(expiresIn)
                .memberId(memberId)
                .email(email)
                .name(name)
                .roleId(roleId)
                .roleName(roleName)
                .loginTime(LocalDateTime.now())
                .build();
    }
} 