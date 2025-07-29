package com.board.plan.auth.controller;

import com.board.plan.auth.dto.LoginRequestDto;
import com.board.plan.auth.dto.LoginResponseDto;
import com.board.plan.auth.dto.RefreshTokenRequestDto;
import com.board.plan.auth.dto.RefreshTokenResponseDto;
import com.board.plan.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "인증 관련 API")
public class AuthController {

    private final AuthService authService;

    @Operation(
        summary = "로그인", 
        description = "이메일과 비밀번호로 로그인하여 JWT 토큰을 발급받습니다."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "로그인 성공",
            content = @Content(schema = @Schema(implementation = LoginResponseDto.class))
        ),
        @ApiResponse(responseCode = "401", description = "인증 실패 (잘못된 이메일/비밀번호)"),
        @ApiResponse(responseCode = "403", description = "계정 비활성화/잠금"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequest) {
        try {
            log.info("POST /api/auth/login - 로그인 요청: {}", loginRequest.getEmail());
            LoginResponseDto response = authService.login(loginRequest);
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            log.warn("로그인 실패 - 인증 실패: {}", loginRequest.getEmail());
            throw e;
        } catch (DisabledException e) {
            log.warn("로그인 실패 - 계정 비활성화: {}", loginRequest.getEmail());
            throw e;
        }
    }

    @Operation(
        summary = "토큰 갱신", 
        description = "Refresh Token을 사용하여 새로운 Access Token을 발급받습니다."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "토큰 갱신 성공",
            content = @Content(schema = @Schema(implementation = RefreshTokenResponseDto.class))
        ),
        @ApiResponse(responseCode = "401", description = "유효하지 않은 Refresh Token"),
        @ApiResponse(responseCode = "403", description = "계정 비활성화"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    })
    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponseDto> refreshToken(
            @Valid @RequestBody RefreshTokenRequestDto refreshRequest) {
        try {
            log.info("POST /api/auth/refresh - 토큰 갱신 요청");
            RefreshTokenResponseDto response = authService.refreshToken(refreshRequest);
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            log.warn("토큰 갱신 실패 - 유효하지 않은 토큰");
            throw e;
        } catch (DisabledException e) {
            log.warn("토큰 갱신 실패 - 계정 비활성화");
            throw e;
        }
    }

    @Operation(
        summary = "로그아웃", 
        description = "로그아웃을 처리합니다. (클라이언트에서 토큰 삭제 필요)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 요청")
    })
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletRequest request) {
        try {
            String token = getTokenFromRequest(request);
            if (token != null) {
                authService.logout(token);
            }
            
            log.info("POST /api/auth/logout - 로그아웃 처리");
            return ResponseEntity.ok(Map.of(
                "message", "로그아웃이 처리되었습니다. 클라이언트에서 토큰을 삭제해주세요.",
                "timestamp", String.valueOf(System.currentTimeMillis())
            ));
        } catch (Exception e) {
            log.error("로그아웃 처리 중 오류: {}", e.getMessage());
            return ResponseEntity.ok(Map.of(
                "message", "로그아웃이 처리되었습니다.",
                "timestamp", String.valueOf(System.currentTimeMillis())
            ));
        }
    }

    @Operation(
        summary = "토큰 유효성 검증", 
        description = "현재 Access Token의 유효성을 검증합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "유효한 토큰"),
        @ApiResponse(responseCode = "401", description = "유효하지 않은 토큰")
    })
    @GetMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateToken(HttpServletRequest request) {
        String token = getTokenFromRequest(request);
        
        if (token == null) {
            return ResponseEntity.status(401).body(Map.of(
                "valid", false,
                "message", "토큰이 제공되지 않았습니다."
            ));
        }

        // JWT 필터에서 이미 검증되었으므로 여기까지 오면 유효한 토큰
        return ResponseEntity.ok(Map.of(
            "valid", true,
            "message", "유효한 토큰입니다.",
            "timestamp", String.valueOf(System.currentTimeMillis())
        ));
    }

    /**
     * HTTP 요청에서 JWT 토큰 추출
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
} 