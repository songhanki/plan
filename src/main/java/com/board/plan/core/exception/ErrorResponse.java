package com.board.plan.core.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    
    // 유효성 검사 오류 등의 상세 정보
    private Map<String, String> details;
    
    // 로그 정보 (개발 환경에서만 노출)
    private String logLevel;
    private String logMessage;
    private String exceptionClass;
    private String stackTrace;
    
    // 추가적인 디버깅 정보
    private Map<String, Object> debugInfo;
} 