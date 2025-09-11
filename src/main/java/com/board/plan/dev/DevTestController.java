package com.board.plan.dev;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 개발 환경 테스트용 컨트롤러
 * 운영 환경에서는 비활성화됩니다.
 */
@Slf4j
@RestController
@RequestMapping("/api/dev")
@ConditionalOnProperty(name = "app.security.dev.swagger-test-enabled", havingValue = "true")
@Tag(name = "개발 테스트", description = "개발 환경 전용 테스트 API (운영 환경에서는 비활성화)")
public class DevTestController {

    /**
     * 개발 환경 설정 확인
     */
    @GetMapping("/status")
    @Operation(summary = "개발 환경 상태 확인", description = "현재 개발 환경 설정 상태를 확인합니다.")
    public ResponseEntity<Map<String, Object>> getDevStatus() {
        log.info("개발 환경 상태 확인 요청");
        
        Map<String, Object> status = new HashMap<>();
        status.put("message", "🚨 개발 환경 모드 활성화 - 인증 체크가 비활성화되었습니다!");
        status.put("swaggerTestEnabled", true);
        status.put("authenticationDisabled", true);
        status.put("warning", "운영 환경에서는 절대 사용하지 마세요!");
        
        return ResponseEntity.ok(status);
    }

    /**
     * 테스트용 사용자 정보
     */
    @GetMapping("/test-users")
    @Operation(summary = "테스트용 사용자 정보", description = "Swagger 테스트를 위한 더미 사용자 정보를 제공합니다.")
    public ResponseEntity<Map<String, Object>> getTestUsers() {
        log.info("테스트용 사용자 정보 요청");
        
        Map<String, Object> testData = new HashMap<>();
        
        Map<String, String> admin = new HashMap<>();
        admin.put("memberId", "user-001");
        admin.put("email", "admin@example.com");
        admin.put("roleId", "role-admin");
        admin.put("roleName", "관리자");
        
        Map<String, String> manager = new HashMap<>();
        manager.put("memberId", "user-002");
        manager.put("email", "manager@example.com");
        manager.put("roleId", "role-manager");
        manager.put("roleName", "팀장");
        
        Map<String, String> user = new HashMap<>();
        user.put("memberId", "user-003");
        user.put("email", "user1@example.com");
        user.put("roleId", "role-user");
        user.put("roleName", "일반 사용자");
        
        testData.put("admin", admin);
        testData.put("manager", manager);
        testData.put("user", user);
        testData.put("note", "개발 환경에서는 인증이 비활성화되어 있어 모든 API를 자유롭게 테스트할 수 있습니다.");
        
        return ResponseEntity.ok(testData);
    }

    /**
     * 권한별 접근 가능한 API 목록
     */
    @GetMapping("/api-permissions")
    @Operation(summary = "권한별 API 접근 정보", description = "각 권한별로 접근 가능한 API 목록을 제공합니다.")
    public ResponseEntity<Map<String, Object>> getApiPermissions() {
        log.info("권한별 API 접근 정보 요청");
        
        Map<String, Object> permissions = new HashMap<>();
        
        permissions.put("role-user (일반 사용자)", new String[]{
            "GET /api/schedules/my - 내 일정 목록 조회",
            "POST /api/schedules/my - 내 일정 등록", 
            "PUT /api/schedules/my/{scheduleId} - 내 일정 수정",
            "DELETE /api/schedules/my/{scheduleId} - 내 일정 삭제",
            "GET /api/leaves/requests/my - 내 휴가 신청 목록",
            "POST /api/leaves/requests - 휴가 신청",
            "PUT /api/leaves/requests/{requestId} - 휴가 신청 수정",
            "DELETE /api/leaves/requests/{requestId} - 휴가 신청 취소",
            "GET /api/my/permissions - 내 권한 조회"
        });
        
        permissions.put("role-manager (팀장)", new String[]{
            "위의 일반 사용자 권한 + 추가 권한:",
            "GET /api/schedules - 팀/전사 일정 목록 조회",
            "POST /api/schedules - 팀/전사 일정 등록",
            "PUT /api/schedules/{scheduleId} - 팀/전사 일정 수정",
            "DELETE /api/schedules/{scheduleId} - 팀/전사 일정 삭제",
            "GET /api/leaves/approvals - 휴가 신청 목록 조회",
            "PUT /api/leaves/approvals/{requestId} - 휴가 승인/반려",
            "GET /api/leaves/history - 휴가 사용 이력 조회",
            "GET /api/leaves/statistics - 휴가 통계 조회",
            "GET /api/members/{memberId}/permissions - 회원 권한 조회"
        });
        
        permissions.put("role-admin (관리자)", new String[]{
            "위의 모든 권한 + 추가 권한:",
            "GET /api/schedules/all - 모든 일정 조회",
            "GET /api/roles - 역할 목록 조회",
            "POST /api/roles - 역할 생성",
            "PUT /api/roles/{roleId} - 역할 수정",
            "DELETE /api/roles/{roleId} - 역할 삭제",
            "GET /api/permissions - 권한 목록 조회",
            "POST /api/permissions - 권한 생성",
            "PUT /api/permissions/{permissionId} - 권한 수정",
            "DELETE /api/permissions/{permissionId} - 권한 삭제"
        });
        
        return ResponseEntity.ok(permissions);
    }
}
