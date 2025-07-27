package com.board.plan;

import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.junit.jupiter.api.Test;

class JasyptTest {

    @Test
    void encryptTest() {
        String username = "mysql ID 값";
        String password = "mysql password 값";
        
        System.out.println("암호화된 username: " + jasyptEncrypt(username));
        System.out.println("암호화된 password: " + jasyptEncrypt(password));
    }
    
    private String jasyptEncrypt(String value) {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword("your-secret-key"); // 실제 운영환경에서는 환경변수 등으로 관리
        config.setAlgorithm("PBEWithMD5AndDES");
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setIvGeneratorClassName("org.jasypt.iv.NoIvGenerator");
        config.setStringOutputType("base64");
        encryptor.setConfig(config);
        return encryptor.encrypt(value);
    }
} 