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
    @Schema(description = "사용자 이메일", example = "test@test.com", required = true)
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다")
    @Schema(description = "사용자 비밀번호 (웹에서는 SHA256 해싱된 값, 평문도 지원)", 
            example = "ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f", 
            required = true)
    private String password;
} 