package com.board.plan.role.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 권한 정보 모델
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permission {
    
    private String permissionId;         // 권한 고유 ID
    private String permissionName;       // 권한 이름
    private String description;          // 권한 설명
    private LocalDateTime createdAt;     // 생성일
    private LocalDateTime updatedAt;     // 최종 수정일
}

