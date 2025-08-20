package com.board.plan.role.mapper;

import com.board.plan.role.model.Permission;
import com.board.plan.role.model.Role;
import com.board.plan.role.model.RolePermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 권한 관리 Mapper
 */
@Mapper
public interface RoleMapper {
    
    // === 역할 관리 ===
    
    // 역할 생성
    int insertRole(Role role);
    
    // 역할 수정
    int updateRole(Role role);
    
    // 역할 삭제
    int deleteRole(@Param("roleId") String roleId);
    
    // 역할 조회 (ID로)
    Role selectRoleById(@Param("roleId") String roleId);
    
    // 모든 역할 목록 조회
    List<Role> selectAllRoles();
    
    // === 권한 관리 ===
    
    // 권한 생성
    int insertPermission(Permission permission);
    
    // 권한 수정
    int updatePermission(Permission permission);
    
    // 권한 삭제
    int deletePermission(@Param("permissionId") String permissionId);
    
    // 권한 조회 (ID로)
    Permission selectPermissionById(@Param("permissionId") String permissionId);
    
    // 모든 권한 목록 조회
    List<Permission> selectAllPermissions();
    
    // === 역할-권한 매핑 관리 ===
    
    // 역할에 권한 할당
    int insertRolePermission(RolePermission rolePermission);
    
    // 역할의 권한 제거
    int deleteRolePermission(@Param("roleId") String roleId, @Param("permissionId") String permissionId);
    
    // 역할의 모든 권한 제거
    int deleteAllRolePermissions(@Param("roleId") String roleId);
    
    // 역할의 권한 목록 조회
    List<Permission> selectPermissionsByRoleId(@Param("roleId") String roleId);
    
    // 회원의 권한 목록 조회 (역할을 통해)
    List<Permission> selectPermissionsByMemberId(@Param("memberId") String memberId);
    
    // 권한 중복 확인
    boolean existsRolePermission(@Param("roleId") String roleId, @Param("permissionId") String permissionId);
    
    // 역할 이름 중복 확인
    boolean existsRoleName(@Param("roleName") String roleName, @Param("excludeRoleId") String excludeRoleId);
    
    // 권한 이름 중복 확인
    boolean existsPermissionName(@Param("permissionName") String permissionName, @Param("excludePermissionId") String excludePermissionId);
}

