package com.board.plan.common.util;

import com.board.plan.core.jwt.JwtUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 인증 관련 유틸리티 클래스
 */
@Slf4j
public class AuthUtil {

    /**
     * 현재 로그인한 사용자의 ID를 반환
     */
    public static String getCurrentMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof JwtUserDetails) {
            JwtUserDetails userDetails = (JwtUserDetails) authentication.getPrincipal();
            return userDetails.getMemberId();
        }
        return null;
    }

    /**
     * 현재 로그인한 사용자의 역할 ID를 반환
     */
    public static String getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof JwtUserDetails) {
            JwtUserDetails userDetails = (JwtUserDetails) authentication.getPrincipal();
            return userDetails.getRoleId();
        }
        return null;
    }

    /**
     * 현재 로그인한 사용자가 관리자인지 확인
     */
    public static boolean isAdmin() {
        String roleId = getCurrentUserRole();
        return "role-admin".equals(roleId);
    }

    /**
     * 현재 로그인한 사용자가 팀장 이상인지 확인
     */
    public static boolean isManagerOrAbove() {
        String roleId = getCurrentUserRole();
        return "role-admin".equals(roleId) || "role-manager".equals(roleId);
    }

    /**
     * 현재 로그인한 사용자가 일반 사용자 이상인지 확인
     */
    public static boolean isUserOrAbove() {
        String roleId = getCurrentUserRole();
        return "role-admin".equals(roleId) || "role-manager".equals(roleId) || "role-user".equals(roleId);
    }
}


