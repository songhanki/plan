package com.board.plan.leave.service;

import com.board.plan.core.exception.UserNotFoundException;
import com.board.plan.leave.dto.LeaveApprovalDto;
import com.board.plan.leave.dto.LeaveRequestDto;
import com.board.plan.leave.dto.LeaveResponseDto;
import com.board.plan.leave.mapper.LeaveMapper;
import com.board.plan.leave.model.LeaveApproval;
import com.board.plan.leave.model.LeaveRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 휴가 관리 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LeaveService {

    private final LeaveMapper leaveMapper;

    /**
     * 내 휴가 신청 목록 조회
     */
    public List<LeaveResponseDto> getMyLeaveRequests(String memberId) {
        log.info("회원 휴가 신청 목록 조회: memberId={}", memberId);
        
        List<LeaveRequest> leaveRequests = leaveMapper.selectMyLeaveRequests(memberId);
        return leaveRequests.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * 모든 휴가 신청 목록 조회 (관리자/팀장용)
     */
    public List<LeaveResponseDto> getAllLeaveRequests(String status, LocalDate startDate, LocalDate endDate) {
        log.info("모든 휴가 신청 목록 조회: status={}, 시작일={}, 종료일={}", status, startDate, endDate);
        
        List<LeaveRequest> leaveRequests = leaveMapper.selectAllLeaveRequests(status, startDate, endDate);
        return leaveRequests.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * 휴가 신청
     */
    @Transactional
    public LeaveResponseDto createLeaveRequest(String memberId, LeaveRequestDto requestDto) {
        log.info("휴가 신청: memberId={}, 유형={}, 시작일={}, 종료일={}", 
                memberId, requestDto.getLeaveType(), requestDto.getStartDate(), requestDto.getEndDate());
        
        validateLeaveRequest(requestDto);
        
        String requestId = "req-" + UUID.randomUUID().toString();
        
        LeaveRequest leaveRequest = LeaveRequest.builder()
                .requestId(requestId)
                .memberId(memberId)
                .leaveType(requestDto.getLeaveType())
                .startDate(requestDto.getStartDate())
                .endDate(requestDto.getEndDate())
                .requestReason(requestDto.getRequestReason())
                .status("신청")
                .requestDate(LocalDateTime.now())
                .build();

        leaveMapper.insertLeaveRequest(leaveRequest);
        return getLeaveRequestById(requestId);
    }

    /**
     * 휴가 신청 수정
     */
    @Transactional
    public LeaveResponseDto updateLeaveRequest(String memberId, String requestId, LeaveRequestDto requestDto) {
        log.info("휴가 신청 수정: memberId={}, requestId={}", memberId, requestId);
        
        // 소유자 확인
        if (!leaveMapper.isLeaveRequestOwner(requestId, memberId)) {
            throw new UserNotFoundException("휴가 신청을 수정할 권한이 없습니다.");
        }
        
        LeaveRequest existingRequest = leaveMapper.selectLeaveRequestById(requestId);
        if (existingRequest == null) {
            throw new UserNotFoundException("휴가 신청을 찾을 수 없습니다.");
        }
        
        // 승인된 휴가는 수정 불가
        if ("승인".equals(existingRequest.getStatus())) {
            throw new IllegalStateException("승인된 휴가는 수정할 수 없습니다.");
        }
        
        validateLeaveRequest(requestDto);
        
        LeaveRequest leaveRequest = LeaveRequest.builder()
                .requestId(requestId)
                .memberId(memberId)
                .leaveType(requestDto.getLeaveType())
                .startDate(requestDto.getStartDate())
                .endDate(requestDto.getEndDate())
                .requestReason(requestDto.getRequestReason())
                .status(existingRequest.getStatus()) // 기존 상태 유지
                .requestDate(existingRequest.getRequestDate()) // 기존 신청일 유지
                .build();

        leaveMapper.updateLeaveRequest(leaveRequest);
        return getLeaveRequestById(requestId);
    }

    /**
     * 휴가 신청 취소
     */
    @Transactional
    public void cancelLeaveRequest(String memberId, String requestId) {
        log.info("휴가 신청 취소: memberId={}, requestId={}", memberId, requestId);
        
        // 소유자 확인
        if (!leaveMapper.isLeaveRequestOwner(requestId, memberId)) {
            throw new UserNotFoundException("휴가 신청을 취소할 권한이 없습니다.");
        }
        
        LeaveRequest existingRequest = leaveMapper.selectLeaveRequestById(requestId);
        if (existingRequest == null) {
            throw new UserNotFoundException("휴가 신청을 찾을 수 없습니다.");
        }
        
        // 승인된 휴가는 취소 불가 (별도 프로세스 필요)
        if ("승인".equals(existingRequest.getStatus())) {
            throw new IllegalStateException("승인된 휴가는 이 방법으로 취소할 수 없습니다.");
        }
        
        leaveMapper.deleteLeaveRequest(requestId);
    }

    /**
     * 휴가 승인/반려
     */
    @Transactional
    public LeaveResponseDto approveOrRejectLeave(String approverId, String requestId, LeaveApprovalDto approvalDto) {
        log.info("휴가 승인/반려: approverId={}, requestId={}, 상태={}", 
                approverId, requestId, approvalDto.getApprovalStatus());
        
        LeaveRequest existingRequest = leaveMapper.selectLeaveRequestById(requestId);
        if (existingRequest == null) {
            throw new UserNotFoundException("휴가 신청을 찾을 수 없습니다.");
        }
        
        // 이미 처리된 신청인지 확인
        if (!"신청".equals(existingRequest.getStatus())) {
            throw new IllegalStateException("이미 처리된 휴가 신청입니다.");
        }
        
        String approvalId = "app-" + UUID.randomUUID().toString();
        
        // 승인/반려 정보 생성
        LeaveApproval leaveApproval = LeaveApproval.builder()
                .approvalId(approvalId)
                .requestId(requestId)
                .approverId(approverId)
                .approvalStatus(approvalDto.getApprovalStatus())
                .approvalReason(approvalDto.getApprovalReason())
                .approvalDate(LocalDateTime.now())
                .build();
        
        leaveMapper.insertLeaveApproval(leaveApproval);
        
        // 휴가 신청 상태 업데이트
        leaveMapper.updateLeaveRequestStatus(requestId, approvalDto.getApprovalStatus(), approvalId);
        
        return getLeaveRequestById(requestId);
    }

    /**
     * 휴가 사용 이력 조회
     */
    public List<LeaveResponseDto> getLeaveHistory(String memberId, Integer year) {
        log.info("휴가 사용 이력 조회: memberId={}, year={}", memberId, year);
        
        List<LeaveRequest> leaveHistory = leaveMapper.selectLeaveHistory(memberId, year);
        return leaveHistory.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * 기간별 휴가 통계 조회
     */
    public List<LeaveResponseDto> getLeaveStatistics(LocalDate startDate, LocalDate endDate) {
        log.info("기간별 휴가 통계 조회: 시작일={}, 종료일={}", startDate, endDate);
        
        List<LeaveRequest> statistics = leaveMapper.selectLeaveStatistics(startDate, endDate);
        return statistics.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * 휴가 신청 조회 (ID로)
     */
    public LeaveResponseDto getLeaveRequestById(String requestId) {
        LeaveRequest leaveRequest = leaveMapper.selectLeaveRequestById(requestId);
        if (leaveRequest == null) {
            throw new UserNotFoundException("휴가 신청을 찾을 수 없습니다.");
        }
        
        return convertToResponseDto(leaveRequest);
    }

    /**
     * LeaveRequest를 LeaveResponseDto로 변환
     */
    private LeaveResponseDto convertToResponseDto(LeaveRequest leaveRequest) {
        LeaveApprovalDto approvalDto = null;
        
        // 승인/반려 정보가 있는 경우 조회
        if (leaveRequest.getApprovalId() != null) {
            LeaveApproval approval = leaveMapper.selectLeaveApprovalByRequestId(leaveRequest.getRequestId());
            if (approval != null) {
                approvalDto = LeaveApprovalDto.builder()
                        .approvalId(approval.getApprovalId())
                        .requestId(approval.getRequestId())
                        .approverId(approval.getApproverId())
                        .approvalStatus(approval.getApprovalStatus())
                        .approvalReason(approval.getApprovalReason())
                        .approvalDate(approval.getApprovalDate())
                        .createdAt(approval.getCreatedAt())
                        .updatedAt(approval.getUpdatedAt())
                        .build();
            }
        }

        return LeaveResponseDto.builder()
                .requestId(leaveRequest.getRequestId())
                .memberId(leaveRequest.getMemberId())
                .leaveType(leaveRequest.getLeaveType())
                .startDate(leaveRequest.getStartDate())
                .endDate(leaveRequest.getEndDate())
                .requestReason(leaveRequest.getRequestReason())
                .status(leaveRequest.getStatus())
                .requestDate(leaveRequest.getRequestDate())
                .createdAt(leaveRequest.getCreatedAt())
                .updatedAt(leaveRequest.getUpdatedAt())
                .approval(approvalDto)
                .build();
    }

    /**
     * 휴가 신청 유효성 검증
     */
    private void validateLeaveRequest(LeaveRequestDto requestDto) {
        if (requestDto.getStartDate().isAfter(requestDto.getEndDate())) {
            throw new IllegalArgumentException("시작일이 종료일보다 늦을 수 없습니다.");
        }
        
        if (requestDto.getStartDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("과거 날짜로 휴가를 신청할 수 없습니다.");
        }
        
        // 휴가 유형 검증
        List<String> validLeaveTypes = List.of("연차", "병가", "경조휴가", "기타");
        if (!validLeaveTypes.contains(requestDto.getLeaveType())) {
            throw new IllegalArgumentException("유효하지 않은 휴가 유형입니다.");
        }
    }
}

