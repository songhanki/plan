package com.board.plan.auth.service;

import com.board.plan.auth.dto.LoginRequestDto;
import com.board.plan.auth.dto.LoginResponseDto;
import com.board.plan.auth.dto.RefreshTokenRequestDto;
import com.board.plan.auth.dto.RefreshTokenResponseDto;
import com.board.plan.core.exception.UserNotFoundException;
import com.board.plan.core.jwt.JwtTokenProvider;
import com.board.plan.core.util.PasswordUtil;
import com.board.plan.member.dto.MemberDto;
import com.board.plan.member.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final MemberMapper memberMapper;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 사용자 로그인 및 JWT 토큰 발급
     */
    public LoginResponseDto login(LoginRequestDto loginRequest) {
        log.info("로그인 시도: {}", loginRequest.getEmail());

        // 1. 이메일로 사용자 조회
        MemberDto member = memberMapper.findMemberByEmail(loginRequest.getEmail());
        if (member == null) {
            log.warn("존재하지 않는 이메일로 로그인 시도: {}", loginRequest.getEmail());
            throw new BadCredentialsException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        // 2. 비밀번호 검증 (SHA256)
        log.info("로그인 시도 - 이메일: {}", loginRequest.getEmail());
        log.debug("입력된 패스워드와 저장된 패스워드 비교 시작");
        if (!PasswordUtil.matches(loginRequest.getPassword(), member.getPassword())) {
            log.warn("잘못된 비밀번호로 로그인 시도: {}", loginRequest.getEmail());
            throw new BadCredentialsException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        // 3. 계정 상태 확인
        if (!"ACTIVE".equals(member.getStatus())) {
            log.warn("비활성 계정으로 로그인 시도: {} (상태: {})", loginRequest.getEmail(), member.getStatus());
            throw new DisabledException("계정이 비활성화되었습니다. 관리자에게 문의하세요.");
        }

        // 4. 역할 정보 조회 (필요시)
        String roleName = getRoleNameById(member.getRoleId());

        // 5. JWT 토큰 생성
        String accessToken = jwtTokenProvider.generateAccessToken(
            member.getMemberId(), 
            member.getEmail(), 
            member.getRoleId()
        );
        
        String refreshToken = jwtTokenProvider.generateRefreshToken(member.getMemberId());

        // 6. 로그인 성공 로그
        log.info("로그인 성공: {} (member_id: {})", loginRequest.getEmail(), member.getMemberId());

        // 7. 응답 DTO 생성
        return LoginResponseDto.of(
            accessToken,
            refreshToken,
            jwtTokenProvider.getAccessTokenExpiration() / 1000, // 초 단위로 변환
            member.getMemberId(),
            member.getEmail(),
            member.getName(),
            member.getRoleId(),
            roleName
        );
    }

    /**
     * Refresh Token을 사용하여 새로운 Access Token 발급
     */
    public RefreshTokenResponseDto refreshToken(RefreshTokenRequestDto refreshRequest) {
        String refreshToken = refreshRequest.getRefreshToken();
        
        log.info("토큰 갱신 요청");

        // 1. Refresh Token 유효성 검증
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            log.warn("유효하지 않은 Refresh Token으로 갱신 시도");
            throw new BadCredentialsException("유효하지 않은 Refresh Token입니다.");
        }

        // 2. Refresh Token 타입 확인
        if (!jwtTokenProvider.isRefreshToken(refreshToken)) {
            log.warn("잘못된 토큰 타입으로 갱신 시도 (Refresh Token이 아님)");
            throw new BadCredentialsException("잘못된 토큰 타입입니다.");
        }

        // 3. 토큰에서 사용자 정보 추출
        String memberId = jwtTokenProvider.getMemberIdFromToken(refreshToken);
        
        // 4. 사용자 정보 조회 및 검증
        MemberDto member = memberMapper.findMemberByMemberId(memberId);
        if (member == null) {
            log.warn("존재하지 않는 사용자의 Refresh Token: {}", memberId);
            throw new UserNotFoundException("사용자를 찾을 수 없습니다.");
        }

        // 5. 계정 상태 확인
        if (!"ACTIVE".equals(member.getStatus())) {
            log.warn("비활성 계정의 Refresh Token 사용 시도: {}", memberId);
            throw new DisabledException("계정이 비활성화되었습니다.");
        }

        // 6. 새로운 Access Token 생성
        String newAccessToken = jwtTokenProvider.generateAccessToken(
            member.getMemberId(),
            member.getEmail(),
            member.getRoleId()
        );

        log.info("토큰 갱신 성공: {}", memberId);

        // 7. 응답 DTO 생성
        return RefreshTokenResponseDto.of(
            newAccessToken,
            jwtTokenProvider.getAccessTokenExpiration() / 1000 // 초 단위로 변환
        );
    }

    /**
     * 역할 ID로 역할 이름 조회
     */
    private String getRoleNameById(String roleId) {
        switch (roleId) {
            case "role-admin":
                return "관리자";
            case "role-manager":
                return "팀장";
            case "role-user":
                return "일반 사용자";
            default:
                return "알 수 없는 역할";
        }
    }

    /**
     * 로그아웃 (토큰 블랙리스트 - 선택적 구현)
     * 현재는 클라이언트에서 토큰을 삭제하는 것으로 처리
     * 필요시 Redis를 사용한 블랙리스트 기능을 추가할 수 있음
     */
    public void logout(String accessToken) {
        // TODO: 필요시 토큰 블랙리스트 기능 구현
        // Redis에 토큰 저장하거나 DB에 블랙리스트 테이블 생성
        log.info("로그아웃 처리 (클라이언트에서 토큰 삭제 필요)");
    }
} 