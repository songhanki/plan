package com.board.plan.role.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 역할 정보 모델
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {
    
    private String roleId;               // 역할 고유 ID
    private String roleName;             // 역할 이름
    private String description;          // 역할 설명
    private LocalDateTime createdAt;     // 생성일
    private LocalDateTime updatedAt;     // 최종 수정일
}

