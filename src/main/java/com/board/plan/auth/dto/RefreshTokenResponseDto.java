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
@Schema(description = "토큰 갱신 응답 DTO")
public class RefreshTokenResponseDto {

    @Schema(description = "새로운 JWT Access Token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;

    @Schema(description = "토큰 타입", example = "Bearer")
    private String tokenType;

    @Schema(description = "Access Token 만료 시간 (초)", example = "900")
    private Long expiresIn;

    @Schema(description = "갱신 시간")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime refreshTime;

    /**
     * 기본 토큰 타입을 Bearer로 설정하는 편의 메서드
     */
    public static RefreshTokenResponseDto of(String accessToken, Long expiresIn) {
        return RefreshTokenResponseDto.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .expiresIn(expiresIn)
                .refreshTime(LocalDateTime.now())
                .build();
    }
} 