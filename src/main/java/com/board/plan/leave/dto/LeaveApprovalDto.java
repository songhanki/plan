package com.board.plan.leave.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * 휴가 승인/반려 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveApprovalDto {
    
    private String approvalId;           // 승인/반려 기록 고유 ID
    private String requestId;            // 관련 휴가 신청 ID
    private String approverId;           // 승인/반려한 회원 ID
    private String approverName;         // 승인/반려한 회원 이름
    
    @NotBlank(message = "승인/반려 상태는 필수입니다.")
    private String approvalStatus;       // 승인/반려 상태 (승인, 반려)
    
    private String approvalReason;       // 반려 사유
    private LocalDateTime approvalDate;  // 승인/반려 일시
    private LocalDateTime createdAt;     // 생성일
    private LocalDateTime updatedAt;     // 최종 수정일
}
