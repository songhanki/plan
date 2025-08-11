package com.board.plan.auth.controller;

import com.board.plan.auth.dto.TokenValidationRequestDto;
import com.board.plan.auth.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Slf4j
@RestController
@RequestMapping("/api/v1/tokens")
@RequiredArgsConstructor
public class TokenController {

    private final TokenService tokenService;

    @Operation(
        summary = "토큰 유효성 검증", 
        description = "Access Token의 유효성을 검증합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "토큰 검증 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping("/validate")
    public ResponseEntity<Boolean> validateToken(@RequestBody TokenValidationRequestDto request) {
        log.info("=== Token Validation Request ===");
        
        try {
            if (request == null || request.getToken() == null || request.getToken().trim().isEmpty()) {
                log.warn("토큰이 비어있음");
                return ResponseEntity.ok(false);
            }

            String token = request.getToken().trim();
            if (token.startsWith("Bearer ")) {
                token = token.substring(7).trim();
            }
            
            log.info("토큰 검증 시작: {}", token.substring(0, Math.min(token.length(), 10)) + "...");
            boolean isValid = tokenService.validateAccessToken(token);
            log.info("토큰 검증 결과: {}", isValid);
            
            return ResponseEntity.ok(isValid);
        } catch (Exception e) {
            log.error("토큰 검증 중 오류 발생", e);
            return ResponseEntity.ok(false);
        }
    }
}