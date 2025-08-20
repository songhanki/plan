package com.board.plan.role.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * 권한 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionDto {
    
    private String permissionId;         // 권한 고유 ID
    
    @NotBlank(message = "권한 이름은 필수입니다.")
    private String permissionName;       // 권한 이름
    
    private String description;          // 권한 설명
    private LocalDateTime createdAt;     // 생성일
    private LocalDateTime updatedAt;     // 최종 수정일
}
