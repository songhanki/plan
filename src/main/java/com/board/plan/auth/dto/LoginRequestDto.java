package com.board.plan.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "로그인 요청 DTO")
public class LoginRequestDto {

    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "유효한 이메일 형식이어야 합니다")
    @Schema(description = "사용자 이메일", example = "user@example.com", required = true)
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다")
    @Schema(description = "사용자 비밀번호", example = "password123", required = true)
    private String password;
} 