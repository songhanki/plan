package com.board.plan.leave.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 휴가 승인/반려 모델
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveApproval {
    
    private String approvalId;           // 승인/반려 기록 고유 ID (UUID)
    private String requestId;            // 관련 휴가 신청 ID
    private String approverId;           // 승인/반려한 회원 ID
    private String approvalStatus;       // 승인/반려 상태 (승인, 반려)
    private String approvalReason;       // 반려 사유 (반려 시 필수)
    private LocalDateTime approvalDate;  // 승인/반려 일시
    private LocalDateTime createdAt;     // 생성일
    private LocalDateTime updatedAt;     // 최종 수정일
}

