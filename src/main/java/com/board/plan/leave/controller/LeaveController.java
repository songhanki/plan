package com.board.plan.leave.controller;

import com.board.plan.leave.dto.LeaveApprovalDto;
import com.board.plan.leave.dto.LeaveRequestDto;
import com.board.plan.leave.dto.LeaveResponseDto;
import com.board.plan.leave.service.LeaveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;

/**
 * 휴가 관리 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/leaves")
@RequiredArgsConstructor
@Tag(name = "휴가 관리", description = "휴가 신청, 승인, 조회 API")
public class LeaveController {

    private final LeaveService leaveService;

    // === 휴가 신청 API ===

    /**
     * 내 휴가 확인
     */
    @GetMapping("/requests/my")
    @PreAuthorize("hasAuthority('perm-leave-request') or hasRole('ROLE_USER')")
    @Operation(summary = "내 휴가 신청 목록 조회", description = "로그인한 사용자의 휴가 신청 목록을 조회합니다.")
    public ResponseEntity<List<LeaveResponseDto>> getMyLeaveRequests(Authentication authentication) {
        
        log.info("내 휴가 신청 목록 조회 요청: 사용자={}", authentication.getName());
        
        String memberId = authentication.getName();
        List<LeaveResponseDto> leaveRequests = leaveService.getMyLeaveRequests(memberId);
        
        return ResponseEntity.ok(leaveRequests);
    }

    /**
     * 휴가 신청
     */
    @PostMapping("/requests")
    @PreAuthorize("hasAuthority('perm-leave-request') or hasRole('ROLE_USER')")
    @Operation(summary = "휴가 신청", description = "새로운 휴가를 신청합니다.")
    public ResponseEntity<LeaveResponseDto> createLeaveRequest(
            Authentication authentication,
            @Valid @RequestBody LeaveRequestDto requestDto) {
        
        log.info("휴가 신청 요청: 사용자={}, 유형={}, 시작일={}, 종료일={}", 
                authentication.getName(), requestDto.getLeaveType(), 
                requestDto.getStartDate(), requestDto.getEndDate());
        
        String memberId = authentication.getName();
        LeaveResponseDto leaveRequest = leaveService.createLeaveRequest(memberId, requestDto);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(leaveRequest);
    }

    /**
     * 휴가 신청 수정
     */
    @PutMapping("/requests/{requestId}")
    @PreAuthorize("hasAuthority('perm-leave-request') or hasRole('ROLE_USER')")
    @Operation(summary = "휴가 신청 수정", description = "기존 휴가 신청을 수정합니다.")
    public ResponseEntity<LeaveResponseDto> updateLeaveRequest(
            Authentication authentication,
            @Parameter(description = "휴가 신청 ID", required = true)
            @PathVariable String requestId,
            @Valid @RequestBody LeaveRequestDto requestDto) {
        
        log.info("휴가 신청 수정 요청: 사용자={}, 신청ID={}", 
                authentication.getName(), requestId);
        
        String memberId = authentication.getName();
        LeaveResponseDto leaveRequest = leaveService.updateLeaveRequest(memberId, requestId, requestDto);
        
        return ResponseEntity.ok(leaveRequest);
    }

    /**
     * 휴가 신청 취소
     */
    @DeleteMapping("/requests/{requestId}")
    @PreAuthorize("hasAuthority('perm-leave-request') or hasRole('ROLE_USER')")
    @Operation(summary = "휴가 신청 취소", description = "휴가 신청을 취소합니다.")
    public ResponseEntity<Void> cancelLeaveRequest(
            Authentication authentication,
            @Parameter(description = "휴가 신청 ID", required = true)
            @PathVariable String requestId) {
        
        log.info("휴가 신청 취소 요청: 사용자={}, 신청ID={}", 
                authentication.getName(), requestId);
        
        String memberId = authentication.getName();
        leaveService.cancelLeaveRequest(memberId, requestId);
        
        return ResponseEntity.noContent().build();
    }

    // === 휴가 관리 API ===

    /**
     * 휴가 신청 조회 (관리자/팀장용)
     */
    @GetMapping("/approvals")
    @PreAuthorize("hasAuthority('perm-leave-approve') or hasRole('ROLE_MANAGER')")
    @Operation(summary = "휴가 신청 목록 조회", description = "모든 휴가 신청 목록을 조회합니다. (관리자/팀장 전용)")
    public ResponseEntity<List<LeaveResponseDto>> getAllLeaveRequests(
            @Parameter(description = "신청 상태 (신청, 승인, 반려, 취소)", example = "신청")
            @RequestParam(required = false) String status,
            @Parameter(description = "시작일 (YYYY-MM-DD)", example = "2024-01-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "종료일 (YYYY-MM-DD)", example = "2024-12-31")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        log.info("모든 휴가 신청 목록 조회 요청: 상태={}, 시작일={}, 종료일={}", status, startDate, endDate);
        
        List<LeaveResponseDto> leaveRequests = leaveService.getAllLeaveRequests(status, startDate, endDate);
        
        return ResponseEntity.ok(leaveRequests);
    }

    /**
     * 휴가 승인/반려
     */
    @PutMapping("/approvals/{requestId}")
    @PreAuthorize("hasAuthority('perm-leave-approve') or hasRole('ROLE_MANAGER')")
    @Operation(summary = "휴가 승인/반려", description = "휴가 신청을 승인하거나 반려합니다.")
    public ResponseEntity<LeaveResponseDto> approveOrRejectLeave(
            Authentication authentication,
            @Parameter(description = "휴가 신청 ID", required = true)
            @PathVariable String requestId,
            @Valid @RequestBody LeaveApprovalDto approvalDto) {
        
        log.info("휴가 승인/반려 요청: 승인자={}, 신청ID={}, 상태={}", 
                authentication.getName(), requestId, approvalDto.getApprovalStatus());
        
        String approverId = authentication.getName();
        LeaveResponseDto leaveRequest = leaveService.approveOrRejectLeave(approverId, requestId, approvalDto);
        
        return ResponseEntity.ok(leaveRequest);
    }

    /**
     * 휴가 사용 이력 조회
     */
    @GetMapping("/history")
    @PreAuthorize("hasAuthority('perm-leave-approve') or hasRole('ROLE_MANAGER')")
    @Operation(summary = "휴가 사용 이력 조회", description = "휴가 사용 이력을 조회합니다.")
    public ResponseEntity<List<LeaveResponseDto>> getLeaveHistory(
            @Parameter(description = "회원 ID (미입력 시 전체 조회)")
            @RequestParam(required = false) String memberId,
            @Parameter(description = "연도 (YYYY)", example = "2024")
            @RequestParam(required = false) Integer year) {
        
        log.info("휴가 사용 이력 조회 요청: 회원ID={}, 연도={}", memberId, year);
        
        List<LeaveResponseDto> leaveHistory = leaveService.getLeaveHistory(memberId, year);
        
        return ResponseEntity.ok(leaveHistory);
    }

    /**
     * 내 휴가 사용 이력 조회
     */
    @GetMapping("/history/my")
    @PreAuthorize("hasAuthority('perm-leave-request') or hasRole('ROLE_USER')")
    @Operation(summary = "내 휴가 사용 이력 조회", description = "로그인한 사용자의 휴가 사용 이력을 조회합니다.")
    public ResponseEntity<List<LeaveResponseDto>> getMyLeaveHistory(
            Authentication authentication,
            @Parameter(description = "연도 (YYYY)", example = "2024")
            @RequestParam(required = false) Integer year) {
        
        log.info("내 휴가 사용 이력 조회 요청: 사용자={}, 연도={}", authentication.getName(), year);
        
        String memberId = authentication.getName();
        List<LeaveResponseDto> leaveHistory = leaveService.getLeaveHistory(memberId, year);
        
        return ResponseEntity.ok(leaveHistory);
    }

    /**
     * 기간별 휴가 통계 조회
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasAuthority('perm-leave-approve') or hasRole('ROLE_MANAGER')")
    @Operation(summary = "기간별 휴가 통계 조회", description = "지정된 기간의 휴가 통계를 조회합니다.")
    public ResponseEntity<List<LeaveResponseDto>> getLeaveStatistics(
            @Parameter(description = "시작일 (YYYY-MM-DD)", required = true, example = "2024-01-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "종료일 (YYYY-MM-DD)", required = true, example = "2024-12-31")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        log.info("기간별 휴가 통계 조회 요청: 시작일={}, 종료일={}", startDate, endDate);
        
        List<LeaveResponseDto> statistics = leaveService.getLeaveStatistics(startDate, endDate);
        
        return ResponseEntity.ok(statistics);
    }

    /**
     * 휴가 신청 상세 조회
     */
    @GetMapping("/requests/{requestId}")
    @PreAuthorize("hasAuthority('perm-leave-request') or hasRole('ROLE_USER')")
    @Operation(summary = "휴가 신청 상세 조회", description = "특정 휴가 신청의 상세 정보를 조회합니다.")
    public ResponseEntity<LeaveResponseDto> getLeaveRequestById(
            @Parameter(description = "휴가 신청 ID", required = true)
            @PathVariable String requestId) {
        
        log.info("휴가 신청 상세 조회 요청: 신청ID={}", requestId);
        
        LeaveResponseDto leaveRequest = leaveService.getLeaveRequestById(requestId);
        
        return ResponseEntity.ok(leaveRequest);
    }
}
