package com.board.plan.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenValidationRequestDto {
    @Schema(description = "검증할 토큰", example = "eyJhbGciOiJIUzM4NCJ9...")
    private String token;
}