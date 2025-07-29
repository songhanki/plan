package com.board.plan.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "토큰 갱신 요청 DTO")
public class RefreshTokenRequestDto {

    @NotBlank(message = "Refresh Token은 필수입니다")
    @Schema(description = "JWT Refresh Token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...", required = true)
    private String refreshToken;
} 