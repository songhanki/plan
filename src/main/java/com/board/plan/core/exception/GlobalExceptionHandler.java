package com.board.plan.core.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    /**
     * 개발 환경에서 디버그 정보 포함 여부 결정
     */
    private boolean isDebugMode() {
        return "dev".equals(activeProfile) || "test".equals(activeProfile);
    }

    /**
     * 스택 트레이스를 문자열로 변환
     */
    private String getStackTrace(Exception ex) {
        if (!isDebugMode()) {
            return null;
        }
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        return sw.toString();
    }

    /**
     * 공통 ErrorResponse 생성 메서드
     */
    private ErrorResponse createErrorResponse(HttpStatus status, String message, Exception ex, WebRequest request) {
        String logMessage = String.format("[%s] %s - %s", 
            ex.getClass().getSimpleName(), 
            message, 
            ex.getMessage());

        ErrorResponse.ErrorResponseBuilder builder = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(request.getDescription(false).replace("uri=", ""));

        // 디버그 모드에서만 상세 정보 포함
        if (isDebugMode()) {
            builder.logLevel("WARN")
                   .logMessage(logMessage)
                   .exceptionClass(ex.getClass().getName())
                   .stackTrace(getStackTrace(ex));
        }

        return builder.build();
    }

    /**
     * 중복 데이터 예외 처리
     */
    @ExceptionHandler(DuplicateEntryException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateEntry(DuplicateEntryException ex, WebRequest request) {
        log.warn("Duplicate entry exception: {}", ex.getMessage());
        ErrorResponse errorResponse = createErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), ex, request);
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    /**
     * 사용자 없음 예외 처리
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex, WebRequest request) {
        log.warn("User not found exception: {}", ex.getMessage());
        ErrorResponse errorResponse = createErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), ex, request);
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * 인증 실패 예외 처리 (잘못된 이메일/비밀번호)
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex, WebRequest request) {
        String logMessage = String.format("Bad credentials exception: %s", ex.getMessage());
        log.warn(logMessage);
        
        ErrorResponse errorResponse = createErrorResponse(
            HttpStatus.UNAUTHORIZED, 
            "이메일 또는 비밀번호가 올바르지 않습니다.", 
            ex, 
            request);
        
        // 디버그 정보에 원본 에러 메시지 추가
        if (isDebugMode()) {
            Map<String, Object> debugInfo = new HashMap<>();
            debugInfo.put("originalMessage", ex.getMessage());
            debugInfo.put("logMessage", logMessage);
            errorResponse.setDebugInfo(debugInfo);
        }
        
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    /**
     * 계정 비활성화 예외 처리
     */
    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ErrorResponse> handleDisabled(DisabledException ex, WebRequest request) {
        String logMessage = String.format("Account disabled exception: %s", ex.getMessage());
        log.warn(logMessage);
        
        ErrorResponse errorResponse = createErrorResponse(
            HttpStatus.FORBIDDEN, 
            "계정이 비활성화되었습니다. 관리자에게 문의하세요.", 
            ex, 
            request);
        
        // 디버그 정보에 원본 에러 메시지 추가
        if (isDebugMode()) {
            Map<String, Object> debugInfo = new HashMap<>();
            debugInfo.put("originalMessage", ex.getMessage());
            debugInfo.put("logMessage", logMessage);
            errorResponse.setDebugInfo(debugInfo);
        }
        
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    /**
     * 일반 인증 예외 처리
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthentication(AuthenticationException ex, WebRequest request) {
        String logMessage = String.format("Authentication exception: %s", ex.getMessage());
        log.warn(logMessage);
        
        ErrorResponse errorResponse = createErrorResponse(
            HttpStatus.UNAUTHORIZED, 
            "인증에 실패했습니다. 다시 로그인해주세요.", 
            ex, 
            request);
        
        // 디버그 정보에 원본 에러 메시지 추가
        if (isDebugMode()) {
            Map<String, Object> debugInfo = new HashMap<>();
            debugInfo.put("originalMessage", ex.getMessage());
            debugInfo.put("logMessage", logMessage);
            errorResponse.setDebugInfo(debugInfo);
        }
        
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    /**
     * 권한 부족 예외 처리
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex, WebRequest request) {
        String logMessage = String.format("Access denied exception: %s", ex.getMessage());
        log.warn(logMessage);
        
        ErrorResponse errorResponse = createErrorResponse(
            HttpStatus.FORBIDDEN, 
            "해당 리소스에 접근할 권한이 없습니다.", 
            ex, 
            request);
        
        // 디버그 정보에 원본 에러 메시지 추가
        if (isDebugMode()) {
            Map<String, Object> debugInfo = new HashMap<>();
            debugInfo.put("originalMessage", ex.getMessage());
            debugInfo.put("logMessage", logMessage);
            errorResponse.setDebugInfo(debugInfo);
        }
        
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    /**
     * 유효성 검사 실패 예외 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        String logMessage = String.format("Validation exception: %s", errors);
        log.warn(logMessage);
        
        ErrorResponse errorResponse = createErrorResponse(
            HttpStatus.BAD_REQUEST, 
            "입력 데이터가 유효하지 않습니다.", 
            ex, 
            request);
        
        // 유효성 검사 에러는 항상 details에 포함
        errorResponse.setDetails(errors);
        
        // 디버그 정보 추가
        if (isDebugMode()) {
            Map<String, Object> debugInfo = new HashMap<>();
            debugInfo.put("validationErrors", errors);
            debugInfo.put("logMessage", logMessage);
            debugInfo.put("fieldCount", errors.size());
            errorResponse.setDebugInfo(debugInfo);
        }
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * 일반 예외 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex, WebRequest request) {
        String logMessage = String.format("Unexpected exception: %s", ex.getMessage());
        log.error(logMessage, ex);
        
        ErrorResponse errorResponse = createErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR, 
            "서버 내부 오류가 발생했습니다.", 
            ex, 
            request);
        
        // 일반 예외의 경우 더 상세한 디버그 정보 제공
        if (isDebugMode()) {
            Map<String, Object> debugInfo = new HashMap<>();
            debugInfo.put("originalMessage", ex.getMessage());
            debugInfo.put("logMessage", logMessage);
            debugInfo.put("cause", ex.getCause() != null ? ex.getCause().toString() : "No cause");
            
            // 스택 트레이스의 첫 몇 줄만 포함
            StackTraceElement[] stackTrace = ex.getStackTrace();
            if (stackTrace.length > 0) {
                StringBuilder sb = new StringBuilder();
                int limit = Math.min(5, stackTrace.length);
                for (int i = 0; i < limit; i++) {
                    sb.append(stackTrace[i].toString()).append("\n");
                }
                debugInfo.put("topStackTrace", sb.toString());
            }
            
            errorResponse.setDebugInfo(debugInfo);
            // 로그 레벨을 ERROR로 설정
            errorResponse.setLogLevel("ERROR");
        }
        
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
} 