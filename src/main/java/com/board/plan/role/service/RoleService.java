package com.board.plan.role.service;

import com.board.plan.core.exception.DuplicateEntryException;
import com.board.plan.core.exception.UserNotFoundException;
import com.board.plan.role.dto.PermissionDto;
import com.board.plan.role.dto.RoleRequestDto;
import com.board.plan.role.dto.RoleResponseDto;
import com.board.plan.role.mapper.RoleMapper;
import com.board.plan.role.model.Permission;
import com.board.plan.role.model.Role;
import com.board.plan.role.model.RolePermission;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 권한 관리 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleMapper roleMapper;

    // === 역할 관리 ===

    /**
     * 모든 역할 목록 조회
     */
    public List<RoleResponseDto> getAllRoles() {
        log.info("모든 역할 목록 조회");
        
        List<Role> roles = roleMapper.selectAllRoles();
        return roles.stream()
                .map(this::convertToRoleResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * 역할 조회 (ID로)
     */
    public RoleResponseDto getRoleById(String roleId) {
        log.info("역할 조회: roleId={}", roleId);
        
        Role role = roleMapper.selectRoleById(roleId);
        if (role == null) {
            throw new UserNotFoundException("역할을 찾을 수 없습니다.");
        }
        
        return convertToRoleResponseDto(role);
    }

    /**
     * 역할 생성
     */
    @Transactional
    public RoleResponseDto createRole(RoleRequestDto requestDto) {
        log.info("역할 생성: roleName={}", requestDto.getRoleName());
        
        // 역할 이름 중복 확인
        if (roleMapper.existsRoleName(requestDto.getRoleName(), null)) {
            throw new DuplicateEntryException("이미 존재하는 역할 이름입니다.");
        }
        
        String roleId = "role-" + UUID.randomUUID().toString();
        
        Role role = Role.builder()
                .roleId(roleId)
                .roleName(requestDto.getRoleName())
                .description(requestDto.getDescription())
                .build();

        roleMapper.insertRole(role);

        // 권한 할당
        if (requestDto.getPermissionIds() != null && !requestDto.getPermissionIds().isEmpty()) {
            assignPermissionsToRole(roleId, requestDto.getPermissionIds());
        }

        return getRoleById(roleId);
    }

    /**
     * 역할 수정
     */
    @Transactional
    public RoleResponseDto updateRole(String roleId, RoleRequestDto requestDto) {
        log.info("역할 수정: roleId={}, roleName={}", roleId, requestDto.getRoleName());
        
        Role existingRole = roleMapper.selectRoleById(roleId);
        if (existingRole == null) {
            throw new UserNotFoundException("역할을 찾을 수 없습니다.");
        }
        
        // 역할 이름 중복 확인 (자기 자신 제외)
        if (roleMapper.existsRoleName(requestDto.getRoleName(), roleId)) {
            throw new DuplicateEntryException("이미 존재하는 역할 이름입니다.");
        }
        
        Role role = Role.builder()
                .roleId(roleId)
                .roleName(requestDto.getRoleName())
                .description(requestDto.getDescription())
                .build();

        roleMapper.updateRole(role);

        // 기존 권한 제거 후 새로운 권한 할당
        if (requestDto.getPermissionIds() != null) {
            roleMapper.deleteAllRolePermissions(roleId);
            if (!requestDto.getPermissionIds().isEmpty()) {
                assignPermissionsToRole(roleId, requestDto.getPermissionIds());
            }
        }

        return getRoleById(roleId);
    }

    /**
     * 역할 삭제
     */
    @Transactional
    public void deleteRole(String roleId) {
        log.info("역할 삭제: roleId={}", roleId);
        
        Role existingRole = roleMapper.selectRoleById(roleId);
        if (existingRole == null) {
            throw new UserNotFoundException("역할을 찾을 수 없습니다.");
        }
        
        // 시스템 기본 역할 삭제 방지
        if (roleId.startsWith("role-admin") || roleId.startsWith("role-manager") || roleId.startsWith("role-user")) {
            throw new IllegalStateException("시스템 기본 역할은 삭제할 수 없습니다.");
        }
        
        roleMapper.deleteRole(roleId);
    }

    // === 권한 관리 ===

    /**
     * 모든 권한 목록 조회
     */
    public List<PermissionDto> getAllPermissions() {
        log.info("모든 권한 목록 조회");
        
        List<Permission> permissions = roleMapper.selectAllPermissions();
        return permissions.stream()
                .map(this::convertToPermissionDto)
                .collect(Collectors.toList());
    }

    /**
     * 권한 조회 (ID로)
     */
    public PermissionDto getPermissionById(String permissionId) {
        log.info("권한 조회: permissionId={}", permissionId);
        
        Permission permission = roleMapper.selectPermissionById(permissionId);
        if (permission == null) {
            throw new UserNotFoundException("권한을 찾을 수 없습니다.");
        }
        
        return convertToPermissionDto(permission);
    }

    /**
     * 권한 생성
     */
    @Transactional
    public PermissionDto createPermission(PermissionDto permissionDto) {
        log.info("권한 생성: permissionName={}", permissionDto.getPermissionName());
        
        // 권한 이름 중복 확인
        if (roleMapper.existsPermissionName(permissionDto.getPermissionName(), null)) {
            throw new DuplicateEntryException("이미 존재하는 권한 이름입니다.");
        }
        
        String permissionId = "perm-" + UUID.randomUUID().toString();
        
        Permission permission = Permission.builder()
                .permissionId(permissionId)
                .permissionName(permissionDto.getPermissionName())
                .description(permissionDto.getDescription())
                .build();

        roleMapper.insertPermission(permission);
        return getPermissionById(permissionId);
    }

    /**
     * 권한 수정
     */
    @Transactional
    public PermissionDto updatePermission(String permissionId, PermissionDto permissionDto) {
        log.info("권한 수정: permissionId={}, permissionName={}", permissionId, permissionDto.getPermissionName());
        
        Permission existingPermission = roleMapper.selectPermissionById(permissionId);
        if (existingPermission == null) {
            throw new UserNotFoundException("권한을 찾을 수 없습니다.");
        }
        
        // 권한 이름 중복 확인 (자기 자신 제외)
        if (roleMapper.existsPermissionName(permissionDto.getPermissionName(), permissionId)) {
            throw new DuplicateEntryException("이미 존재하는 권한 이름입니다.");
        }
        
        Permission permission = Permission.builder()
                .permissionId(permissionId)
                .permissionName(permissionDto.getPermissionName())
                .description(permissionDto.getDescription())
                .build();

        roleMapper.updatePermission(permission);
        return getPermissionById(permissionId);
    }

    /**
     * 권한 삭제
     */
    @Transactional
    public void deletePermission(String permissionId) {
        log.info("권한 삭제: permissionId={}", permissionId);
        
        Permission existingPermission = roleMapper.selectPermissionById(permissionId);
        if (existingPermission == null) {
            throw new UserNotFoundException("권한을 찾을 수 없습니다.");
        }
        
        // 시스템 기본 권한 삭제 방지
        if (permissionId.startsWith("perm-member-") || permissionId.startsWith("perm-schedule-") || permissionId.startsWith("perm-leave-")) {
            throw new IllegalStateException("시스템 기본 권한은 삭제할 수 없습니다.");
        }
        
        roleMapper.deletePermission(permissionId);
    }

    // === 유틸리티 메서드 ===

    /**
     * 회원의 권한 목록 조회
     */
    public List<PermissionDto> getMemberPermissions(String memberId) {
        log.info("회원 권한 조회: memberId={}", memberId);
        
        List<Permission> permissions = roleMapper.selectPermissionsByMemberId(memberId);
        return permissions.stream()
                .map(this::convertToPermissionDto)
                .collect(Collectors.toList());
    }

    /**
     * 역할에 권한들 할당
     */
    private void assignPermissionsToRole(String roleId, List<String> permissionIds) {
        for (String permissionId : permissionIds) {
            // 중복 확인
            if (!roleMapper.existsRolePermission(roleId, permissionId)) {
                RolePermission rolePermission = RolePermission.builder()
                        .roleId(roleId)
                        .permissionId(permissionId)
                        .build();
                
                roleMapper.insertRolePermission(rolePermission);
            }
        }
    }

    /**
     * Role을 RoleResponseDto로 변환
     */
    private RoleResponseDto convertToRoleResponseDto(Role role) {
        List<Permission> permissions = roleMapper.selectPermissionsByRoleId(role.getRoleId());
        
        List<PermissionDto> permissionDtos = permissions.stream()
                .map(this::convertToPermissionDto)
                .collect(Collectors.toList());

        return RoleResponseDto.builder()
                .roleId(role.getRoleId())
                .roleName(role.getRoleName())
                .description(role.getDescription())
                .createdAt(role.getCreatedAt())
                .updatedAt(role.getUpdatedAt())
                .permissions(permissionDtos)
                .build();
    }

    /**
     * Permission을 PermissionDto로 변환
     */
    private PermissionDto convertToPermissionDto(Permission permission) {
        return PermissionDto.builder()
                .permissionId(permission.getPermissionId())
                .permissionName(permission.getPermissionName())
                .description(permission.getDescription())
                .createdAt(permission.getCreatedAt())
                .updatedAt(permission.getUpdatedAt())
                .build();
    }
}