package com.board.plan.schedule.controller;

import com.board.plan.schedule.dto.ScheduleRequestDto;
import com.board.plan.schedule.dto.ScheduleResponseDto;
import com.board.plan.schedule.service.ScheduleService;
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
 * 일정 관리 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
@Tag(name = "일정 관리", description = "일정 생성, 조회, 수정, 삭제 API")
public class ScheduleController {

    private final ScheduleService scheduleService;

    /**
     * 내 일정 확인
     */
    @GetMapping("/my")
    @PreAuthorize("hasAuthority('role-user') or hasAuthority('role-manager') or hasAuthority('role-admin')")
    @Operation(summary = "내 일정 목록 조회", description = "로그인한 사용자의 일정 목록을 조회합니다.")
    public ResponseEntity<List<ScheduleResponseDto>> getMySchedules(
            Authentication authentication,
            @Parameter(description = "시작일 (YYYY-MM-DD)", example = "2024-01-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "종료일 (YYYY-MM-DD)", example = "2024-12-31")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        log.info("내 일정 목록 조회 요청: 사용자={}, 시작일={}, 종료일={}", 
                authentication.getName(), startDate, endDate);
        
        String memberId = authentication.getName();
        List<ScheduleResponseDto> schedules = scheduleService.getMySchedules(memberId, startDate, endDate);
        
        return ResponseEntity.ok(schedules);
    }

    /**
     * 내 일정 등록
     */
    @PostMapping("/my")
    @PreAuthorize("hasAuthority('role-user') or hasAuthority('role-manager') or hasAuthority('role-admin')")
    @Operation(summary = "내 일정 등록", description = "개인 일정을 등록합니다.")
    public ResponseEntity<ScheduleResponseDto> createMySchedule(
            Authentication authentication,
            @Valid @RequestBody ScheduleRequestDto requestDto) {
        
        log.info("개인 일정 등록 요청: 사용자={}, 제목={}", 
                authentication.getName(), requestDto.getTitle());
        
        String memberId = authentication.getName();
        ScheduleResponseDto schedule = scheduleService.createMySchedule(memberId, requestDto);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(schedule);
    }

    /**
     * 내 일정 수정
     */
    @PutMapping("/my/{scheduleId}")
    @PreAuthorize("hasAuthority('perm-schedule-create') or hasRole('ROLE_USER')")
    @Operation(summary = "내 일정 수정", description = "개인 일정을 수정합니다.")
    public ResponseEntity<ScheduleResponseDto> updateMySchedule(
            Authentication authentication,
            @Parameter(description = "일정 ID", required = true)
            @PathVariable String scheduleId,
            @Valid @RequestBody ScheduleRequestDto requestDto) {
        
        log.info("개인 일정 수정 요청: 사용자={}, 일정ID={}", 
                authentication.getName(), scheduleId);
        
        String memberId = authentication.getName();
        ScheduleResponseDto schedule = scheduleService.updateMySchedule(memberId, scheduleId, requestDto);
        
        return ResponseEntity.ok(schedule);
    }

    /**
     * 내 일정 삭제
     */
    @DeleteMapping("/my/{scheduleId}")
    @PreAuthorize("hasAuthority('perm-schedule-create') or hasRole('ROLE_USER')")
    @Operation(summary = "내 일정 삭제", description = "개인 일정을 삭제합니다.")
    public ResponseEntity<Void> deleteMySchedule(
            Authentication authentication,
            @Parameter(description = "일정 ID", required = true)
            @PathVariable String scheduleId) {
        
        log.info("개인 일정 삭제 요청: 사용자={}, 일정ID={}", 
                authentication.getName(), scheduleId);
        
        String memberId = authentication.getName();
        scheduleService.deleteMySchedule(memberId, scheduleId);
        
        return ResponseEntity.noContent().build();
    }

    /**
     * 팀/전사 일정 등록
     */
    @PostMapping
    @PreAuthorize("hasAuthority('perm-schedule-read-all') or hasRole('ROLE_MANAGER')")
    @Operation(summary = "팀/전사 일정 등록", description = "팀 또는 전사 일정을 등록합니다.")
    public ResponseEntity<ScheduleResponseDto> createTeamSchedule(
            Authentication authentication,
            @Valid @RequestBody ScheduleRequestDto requestDto) {
        
        log.info("팀/전사 일정 등록 요청: 관리자={}, 제목={}", 
                authentication.getName(), requestDto.getTitle());
        
        String organizerId = authentication.getName();
        ScheduleResponseDto schedule = scheduleService.createTeamSchedule(organizerId, requestDto);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(schedule);
    }

    /**
     * 팀/전사 일정 수정
     */
    @PutMapping("/{scheduleId}")
    @PreAuthorize("hasAuthority('perm-schedule-read-all') or hasRole('ROLE_MANAGER')")
    @Operation(summary = "팀/전사 일정 수정", description = "팀 또는 전사 일정을 수정합니다.")
    public ResponseEntity<ScheduleResponseDto> updateTeamSchedule(
            Authentication authentication,
            @Parameter(description = "일정 ID", required = true)
            @PathVariable String scheduleId,
            @Valid @RequestBody ScheduleRequestDto requestDto) {
        
        log.info("팀/전사 일정 수정 요청: 관리자={}, 일정ID={}", 
                authentication.getName(), scheduleId);
        
        String managerId = authentication.getName();
        ScheduleResponseDto schedule = scheduleService.updateTeamSchedule(managerId, scheduleId, requestDto);
        
        return ResponseEntity.ok(schedule);
    }

    /**
     * 팀/전사 일정 삭제
     */
    @DeleteMapping("/{scheduleId}")
    @PreAuthorize("hasAuthority('perm-schedule-read-all') or hasRole('ROLE_MANAGER')")
    @Operation(summary = "팀/전사 일정 삭제", description = "팀 또는 전사 일정을 삭제합니다.")
    public ResponseEntity<Void> deleteTeamSchedule(
            Authentication authentication,
            @Parameter(description = "일정 ID", required = true)
            @PathVariable String scheduleId) {
        
        log.info("팀/전사 일정 삭제 요청: 관리자={}, 일정ID={}", 
                authentication.getName(), scheduleId);
        
        String managerId = authentication.getName();
        scheduleService.deleteTeamSchedule(managerId, scheduleId);
        
        return ResponseEntity.noContent().build();
    }

    /**
     * 팀/전사 일정 목록 조회 (관리자용)
     */
    @GetMapping
    @PreAuthorize("hasAuthority('perm-schedule-read-all') or hasRole('ROLE_MANAGER')")
    @Operation(summary = "팀/전사 일정 목록 조회", description = "팀 또는 전사 일정 목록을 조회합니다.")
    public ResponseEntity<List<ScheduleResponseDto>> getTeamSchedules(
            @Parameter(description = "시작일 (YYYY-MM-DD)", example = "2024-01-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "종료일 (YYYY-MM-DD)", example = "2024-12-31")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        log.info("팀/전사 일정 목록 조회 요청: 시작일={}, 종료일={}", startDate, endDate);
        
        List<ScheduleResponseDto> schedules = scheduleService.getTeamSchedules(startDate, endDate);
        
        return ResponseEntity.ok(schedules);
    }

    /**
     * 모든 일정 목록 조회 (관리자용)
     */
    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "모든 일정 목록 조회", description = "시스템의 모든 일정을 조회합니다. (관리자 전용)")
    public ResponseEntity<List<ScheduleResponseDto>> getAllSchedules(
            @Parameter(description = "시작일 (YYYY-MM-DD)", example = "2024-01-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "종료일 (YYYY-MM-DD)", example = "2024-12-31")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        log.info("모든 일정 목록 조회 요청: 시작일={}, 종료일={}", startDate, endDate);
        
        List<ScheduleResponseDto> schedules = scheduleService.getAllSchedules(startDate, endDate);
        
        return ResponseEntity.ok(schedules);
    }

    /**
     * 일정 상세 조회
     */
    @GetMapping("/{scheduleId}")
    @PreAuthorize("hasAuthority('perm-schedule-read-own') or hasRole('ROLE_USER')")
    @Operation(summary = "일정 상세 조회", description = "특정 일정의 상세 정보를 조회합니다.")
    public ResponseEntity<ScheduleResponseDto> getScheduleById(
            @Parameter(description = "일정 ID", required = true)
            @PathVariable String scheduleId) {
        
        log.info("일정 상세 조회 요청: 일정ID={}", scheduleId);
        
        ScheduleResponseDto schedule = scheduleService.getScheduleById(scheduleId);
        
        return ResponseEntity.ok(schedule);
    }
}
