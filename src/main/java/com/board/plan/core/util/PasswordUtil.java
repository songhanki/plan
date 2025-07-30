package com.board.plan.core.util;

import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 패스워드 암호화를 위한 유틸리티 클래스
 * SHA256 해시 알고리즘을 사용하여 패스워드를 암호화합니다.
 */
@Slf4j
public class PasswordUtil {

    private static final String ALGORITHM = "SHA-256";

    /**
     * 평문 패스워드를 SHA256으로 암호화합니다.
     * 
     * @param plainPassword 평문 패스워드
     * @return SHA256으로 암호화된 패스워드 (Hex 문자열)
     */
    public static String encryptPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("패스워드는 null이거나 빈 문자열일 수 없습니다.");
        }

        try {
            MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
            byte[] hash = digest.digest(plainPassword.getBytes(StandardCharsets.UTF_8));
            
            // 바이트 배열을 16진수 문자열로 변환
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            String encryptedPassword = hexString.toString();
            log.debug("패스워드 암호화 완료");
            return encryptedPassword;
            
        } catch (NoSuchAlgorithmException e) {
            log.error("SHA-256 알고리즘을 찾을 수 없습니다.", e);
            throw new RuntimeException("패스워드 암호화 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 평문 패스워드와 암호화된 패스워드를 비교합니다.
     * 
     * @param plainPassword 평문 패스워드
     * @param encryptedPassword 암호화된 패스워드
     * @return 일치하면 true, 그렇지 않으면 false
     */
    public static boolean matches(String plainPassword, String encryptedPassword) {
        if (plainPassword == null || encryptedPassword == null) {
            return false;
        }
        
        String encryptedPlainPassword = encryptPassword(plainPassword);
        boolean matches = encryptedPlainPassword.equals(encryptedPassword);
        
        log.debug("패스워드 비교 결과: {}", matches);
        return matches;
    }
} 