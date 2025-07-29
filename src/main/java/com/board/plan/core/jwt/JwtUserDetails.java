package com.board.plan.core.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtUserDetails {
    
    private String memberId;
    private String email;
    private String roleId;
    
    /**
     * Spring Security에서 사용할 username 반환 (member_id 사용)
     */
    public String getUsername() {
        return memberId;
    }
} 