package com.board.plan.role.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 역할 응답 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleResponseDto {
    
    private String roleId;               // 역할 고유 ID
    private String roleName;             // 역할 이름
    private String description;          // 역할 설명
    private LocalDateTime createdAt;     // 생성일
    private LocalDateTime updatedAt;     // 최종 수정일
    
    // 할당된 권한 목록
    private List<PermissionDto> permissions;
}

