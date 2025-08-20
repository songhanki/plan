package com.board.plan.role.controller;

import com.board.plan.role.dto.PermissionDto;
import com.board.plan.role.dto.RoleRequestDto;
import com.board.plan.role.dto.RoleResponseDto;
import com.board.plan.role.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 권한 관리 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "권한 관리", description = "역할 및 권한 관리 API")
public class RoleController {

    private final RoleService roleService;

    // === 역할 관리 API ===

    /**
     * 모든 역할 목록 조회
     */
    @GetMapping("/roles")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "모든 역할 목록 조회", description = "시스템의 모든 역할을 조회합니다.")
    public ResponseEntity<List<RoleResponseDto>> getAllRoles() {
        
        log.info("모든 역할 목록 조회 요청");
        
        List<RoleResponseDto> roles = roleService.getAllRoles();
        
        return ResponseEntity.ok(roles);
    }

    /**
     * 역할 상세 조회
     */
    @GetMapping("/roles/{roleId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "역할 상세 조회", description = "특정 역할의 상세 정보를 조회합니다.")
    public ResponseEntity<RoleResponseDto> getRoleById(
            @Parameter(description = "역할 ID", required = true)
            @PathVariable String roleId) {
        
        log.info("역할 상세 조회 요청: roleId={}", roleId);
        
        RoleResponseDto role = roleService.getRoleById(roleId);
        
        return ResponseEntity.ok(role);
    }

    /**
     * 역할 생성
     */
    @PostMapping("/roles")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "역할 생성", description = "새로운 역할을 생성합니다.")
    public ResponseEntity<RoleResponseDto> createRole(
            Authentication authentication,
            @Valid @RequestBody RoleRequestDto requestDto) {
        
        log.info("역할 생성 요청: 관리자={}, 역할명={}", 
                authentication.getName(), requestDto.getRoleName());
        
        RoleResponseDto role = roleService.createRole(requestDto);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(role);
    }

    /**
     * 역할 수정
     */
    @PutMapping("/roles/{roleId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "역할 수정", description = "기존 역할을 수정합니다.")
    public ResponseEntity<RoleResponseDto> updateRole(
            Authentication authentication,
            @Parameter(description = "역할 ID", required = true)
            @PathVariable String roleId,
            @Valid @RequestBody RoleRequestDto requestDto) {
        
        log.info("역할 수정 요청: 관리자={}, roleId={}, 역할명={}", 
                authentication.getName(), roleId, requestDto.getRoleName());
        
        RoleResponseDto role = roleService.updateRole(roleId, requestDto);
        
        return ResponseEntity.ok(role);
    }

    /**
     * 역할 삭제
     */
    @DeleteMapping("/roles/{roleId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "역할 삭제", description = "기존 역할을 삭제합니다.")
    public ResponseEntity<Void> deleteRole(
            Authentication authentication,
            @Parameter(description = "역할 ID", required = true)
            @PathVariable String roleId) {
        
        log.info("역할 삭제 요청: 관리자={}, roleId={}", 
                authentication.getName(), roleId);
        
        roleService.deleteRole(roleId);
        
        return ResponseEntity.noContent().build();
    }

    // === 권한 관리 API ===

    /**
     * 모든 권한 목록 조회
     */
    @GetMapping("/permissions")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "모든 권한 목록 조회", description = "시스템의 모든 권한을 조회합니다.")
    public ResponseEntity<List<PermissionDto>> getAllPermissions() {
        
        log.info("모든 권한 목록 조회 요청");
        
        List<PermissionDto> permissions = roleService.getAllPermissions();
        
        return ResponseEntity.ok(permissions);
    }

    /**
     * 권한 상세 조회
     */
    @GetMapping("/permissions/{permissionId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "권한 상세 조회", description = "특정 권한의 상세 정보를 조회합니다.")
    public ResponseEntity<PermissionDto> getPermissionById(
            @Parameter(description = "권한 ID", required = true)
            @PathVariable String permissionId) {
        
        log.info("권한 상세 조회 요청: permissionId={}", permissionId);
        
        PermissionDto permission = roleService.getPermissionById(permissionId);
        
        return ResponseEntity.ok(permission);
    }

    /**
     * 권한 생성
     */
    @PostMapping("/permissions")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "권한 생성", description = "새로운 권한을 생성합니다.")
    public ResponseEntity<PermissionDto> createPermission(
            Authentication authentication,
            @Valid @RequestBody PermissionDto permissionDto) {
        
        log.info("권한 생성 요청: 관리자={}, 권한명={}", 
                authentication.getName(), permissionDto.getPermissionName());
        
        PermissionDto permission = roleService.createPermission(permissionDto);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(permission);
    }

    /**
     * 권한 수정
     */
    @PutMapping("/permissions/{permissionId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "권한 수정", description = "기존 권한을 수정합니다.")
    public ResponseEntity<PermissionDto> updatePermission(
            Authentication authentication,
            @Parameter(description = "권한 ID", required = true)
            @PathVariable String permissionId,
            @Valid @RequestBody PermissionDto permissionDto) {
        
        log.info("권한 수정 요청: 관리자={}, permissionId={}, 권한명={}", 
                authentication.getName(), permissionId, permissionDto.getPermissionName());
        
        PermissionDto permission = roleService.updatePermission(permissionId, permissionDto);
        
        return ResponseEntity.ok(permission);
    }

    /**
     * 권한 삭제
     */
    @DeleteMapping("/permissions/{permissionId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "권한 삭제", description = "기존 권한을 삭제합니다.")
    public ResponseEntity<Void> deletePermission(
            Authentication authentication,
            @Parameter(description = "권한 ID", required = true)
            @PathVariable String permissionId) {
        
        log.info("권한 삭제 요청: 관리자={}, permissionId={}", 
                authentication.getName(), permissionId);
        
        roleService.deletePermission(permissionId);
        
        return ResponseEntity.noContent().build();
    }

    // === 유틸리티 API ===

    /**
     * 회원의 권한 목록 조회
     */
    @GetMapping("/members/{memberId}/permissions")
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_MANAGER') and authentication.name == #memberId)")
    @Operation(summary = "회원의 권한 목록 조회", description = "특정 회원의 권한 목록을 조회합니다.")
    public ResponseEntity<List<PermissionDto>> getMemberPermissions(
            @Parameter(description = "회원 ID", required = true)
            @PathVariable String memberId) {
        
        log.info("회원 권한 목록 조회 요청: memberId={}", memberId);
        
        List<PermissionDto> permissions = roleService.getMemberPermissions(memberId);
        
        return ResponseEntity.ok(permissions);
    }

    /**
     * 내 권한 목록 조회
     */
    @GetMapping("/my/permissions")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "내 권한 목록 조회", description = "로그인한 사용자의 권한 목록을 조회합니다.")
    public ResponseEntity<List<PermissionDto>> getMyPermissions(Authentication authentication) {
        
        log.info("내 권한 목록 조회 요청: 사용자={}", authentication.getName());
        
        String memberId = authentication.getName();
        List<PermissionDto> permissions = roleService.getMemberPermissions(memberId);
        
        return ResponseEntity.ok(permissions);
    }
}
