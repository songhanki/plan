package com.board.plan.auth.service;

import com.board.plan.auth.mapper.TokenMapper;
import com.board.plan.auth.model.Token;
import com.board.plan.core.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TokenService {

    private final TokenMapper tokenMapper;
    private final JwtTokenProvider jwtTokenProvider;

    public void saveTokens(String memberId, String accessToken, String refreshToken) {
        Token existingToken = tokenMapper.findByMemberId(memberId);
        
        Token token = Token.builder()
                .memberId(memberId)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpiresAt(LocalDateTime.now().plusSeconds(jwtTokenProvider.getAccessTokenExpiration() / 1000))
                .refreshTokenExpiresAt(LocalDateTime.now().plusSeconds(jwtTokenProvider.getRefreshTokenExpiration() / 1000))
                .build();

        if (existingToken == null) {
            tokenMapper.saveToken(token);
            log.info("새로운 토큰 저장 완료: {}", memberId);
        } else {
            tokenMapper.updateToken(token);
            log.info("기존 토큰 업데이트 완료: {}", memberId);
        }
    }

    public boolean validateAccessToken(String accessToken) {
        if (!jwtTokenProvider.validateToken(accessToken)) {
            log.warn("JWT 토큰 검증 실패");
            return false;
        }

        Token token = tokenMapper.findByAccessToken(accessToken);
        if (token == null) {
            log.warn("DB에 저장된 토큰을 찾을 수 없음");
            return false;
        }

        if (token.getAccessTokenExpiresAt().isBefore(LocalDateTime.now())) {
            log.warn("토큰이 만료됨");
            return false;
        }

        return true;
    }

    @Scheduled(fixedRate = 300000) // 5분마다 실행
    public void cleanupExpiredTokens() {
        log.info("만료된 토큰 정리 작업 시작");
        // 실제 프로덕션 환경에서는 배치 작업으로 구현하는 것을 권장
        // 여기서는 예시로 로그만 남깁니다.
    }

    public void deleteToken(String memberId) {
        tokenMapper.deleteByMemberId(memberId);
        log.info("토큰 삭제 완료: {}", memberId);
    }
}