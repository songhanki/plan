package com.board.plan.leave.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 휴가 신청 모델
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveRequest {
    
    private String requestId;            // 휴가 신청 고유 ID (UUID)
    private String memberId;             // 신청한 회원 ID
    private String leaveType;            // 휴가 유형 (연차, 병가, 경조휴가, 기타)
    private LocalDate startDate;         // 휴가 시작일
    private LocalDate endDate;           // 휴가 종료일
    private String requestReason;        // 휴가 신청 사유
    private String status;               // 휴가 신청 상태 (신청, 승인, 반려, 취소)
    private LocalDateTime requestDate;   // 휴가 신청일시
    private String approvalId;           // 관련 승인/반려 정보 ID
    private LocalDateTime createdAt;     // 생성일
    private LocalDateTime updatedAt;     // 최종 수정일
}

