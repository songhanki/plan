package com.board.plan.member.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDto {

    @Schema(description = "회원 고유 ID (UUID) (user-XXX 형식)", example = "user-001")
    private String memberId;

    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "유효한 이메일 형식이어야 합니다")
    @Schema(description = "이메일", example = "user@example.com")
    private String email;
    
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank(message = "비밀번호는 필수입니다")
    @Schema(description = "비밀번호", example = "password123")
    private String password;
    
    @NotBlank(message = "이름은 필수입니다")
    @Schema(description = "이름", example = "홍길동")
    @NotBlank(message = "이름은 필수입니다")
    private String name;
    
    @NotBlank(message = "전화번호는 필수입니다")
    @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "전화번호 형식이 올바르지 않습니다 (예: 010-1234-5678)")
    @Schema(description = "전화번호", example = "010-1234-5678")
    private String phoneNumber;
    
    @Schema(description = "생년월일", example = "1990-01-01")
    private String dateOfBirth;

    @Schema(description = "역할 ID", example = "role-user")
    private String roleId;

    @Schema(description = "역할 이름", example = "관리자")
    private String roleName;

    @Schema(description = "닉네임", example = "홍길동")
    private String nickName;

    @Schema(description = "성별", example = "남(등록시에는 F/M 으로 구분)")
    private String gender;

    @Schema(description = "상태", example = "ACTIVE")
    private String status;

    @Schema(description = "생성일시", example = "2025-01-01 12:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "수정일시", example = "2025-01-01 12:00:00")
    private LocalDateTime updatedAt;
} 