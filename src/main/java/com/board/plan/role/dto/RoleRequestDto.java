package com.board.plan.role.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

/**
 * 역할 생성/수정 요청 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleRequestDto {
    
    @NotBlank(message = "역할 이름은 필수입니다.")
    private String roleName;             // 역할 이름
    
    private String description;          // 역할 설명
    
    // 역할에 할당할 권한 ID 목록
    private List<String> permissionIds;
}
