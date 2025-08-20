package com.board.plan.role.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 역할-권한 매핑 모델
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RolePermission {
    
    private Long rolePermissionId;       // 매핑 고유 ID
    private String roleId;               // 역할 ID
    private String permissionId;         // 권한 ID
    private LocalDateTime createdAt;     // 생성일
    private LocalDateTime updatedAt;     // 최종 수정일
}

