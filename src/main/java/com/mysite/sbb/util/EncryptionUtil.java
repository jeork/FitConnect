package com.mysite.sbb.util;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EncryptionUtil {
    private final String secretKey;
    private static final String ALGORITHM = "AES";

    public EncryptionUtil(@Value("${encryption.secret-key}") String secretKey) {
        this.secretKey = secretKey;
    }

    public String encrypt(String value) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            byte[] encryptedValue = cipher.doFinal(value.getBytes());
            return Base64.getUrlEncoder().encodeToString(encryptedValue);
        } catch (Exception e) {
            throw new EncryptionException("암호화 실패: " + e.getMessage(), e);
        }
    }

    public String decrypt(String encryptedValue) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            byte[] decodedValue = Base64.getUrlDecoder().decode(encryptedValue);
            byte[] decryptedValue = cipher.doFinal(decodedValue);
            return new String(decryptedValue);
        } catch (Exception e) {
            throw new EncryptionException("복호화 실패: " + e.getMessage(), e);
        }
    }

    public static class EncryptionException extends RuntimeException {
        public EncryptionException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}