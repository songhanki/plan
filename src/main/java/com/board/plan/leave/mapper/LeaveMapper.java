package com.board.plan.leave.mapper;

import com.board.plan.leave.model.LeaveApproval;
import com.board.plan.leave.model.LeaveRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 휴가 관리 Mapper
 */
@Mapper
public interface LeaveMapper {
    
    // 휴가 신청
    int insertLeaveRequest(LeaveRequest leaveRequest);
    
    // 휴가 신청 수정
    int updateLeaveRequest(LeaveRequest leaveRequest);
    
    // 휴가 신청 취소
    int deleteLeaveRequest(@Param("requestId") String requestId);
    
    // 휴가 신청 조회 (ID로)
    LeaveRequest selectLeaveRequestById(@Param("requestId") String requestId);
    
    // 내 휴가 신청 목록 조회
    List<LeaveRequest> selectMyLeaveRequests(@Param("memberId") String memberId);
    
    // 모든 휴가 신청 목록 조회 (관리자/팀장용)
    List<LeaveRequest> selectAllLeaveRequests(@Param("status") String status, 
                                            @Param("startDate") LocalDate startDate, 
                                            @Param("endDate") LocalDate endDate);
    
    // 휴가 신청 상태 업데이트
    int updateLeaveRequestStatus(@Param("requestId") String requestId, 
                               @Param("status") String status, 
                               @Param("approvalId") String approvalId);
    
    // 휴가 승인/반려 정보 생성
    int insertLeaveApproval(LeaveApproval leaveApproval);
    
    // 휴가 승인/반려 정보 조회
    LeaveApproval selectLeaveApprovalByRequestId(@Param("requestId") String requestId);
    
    // 휴가 사용 이력 조회
    List<LeaveRequest> selectLeaveHistory(@Param("memberId") String memberId, 
                                        @Param("year") Integer year);
    
    // 휴가 신청 소유자 확인
    boolean isLeaveRequestOwner(@Param("requestId") String requestId, @Param("memberId") String memberId);
    
    // 기간별 휴가 통계 조회
    List<LeaveRequest> selectLeaveStatistics(@Param("startDate") LocalDate startDate, 
                                           @Param("endDate") LocalDate endDate);
}

