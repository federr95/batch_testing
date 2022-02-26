package com.example.uploadCSVtoH2.ciphersuite;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;


@Data
@NoArgsConstructor
public class PasswordAESCryptoUtils {

    private static final Logger logger	= LoggerFactory.getLogger(PasswordAESCryptoUtils.class);
    private byte[] IV	= AES_256_CBC_OpenSSL.generateRandomBytes(16);
    private byte[] SALT = AES_256_CBC_OpenSSL.generateRandomBytes(AES_256_CBC_OpenSSL.SALT_LENGTH);
    private byte[] PASS_DIGEST = null;

    // saltString, ivString, passDigestString sono da inserire nelle properties
    //private final String saltString = "lwAqRrSZ5+A=";
    //private final String ivString = "cZYzVDB/D6wEIePn8x+9BA==";
    //private final String digestString = "C+ZK6J3dJOIlQ03pXVAXETObru4Y8Am6m0NpryfTDWA=";
    //private final byte[] IV = Base64.getDecoder().decode(ivString);
    //private final byte[] SALT = Base64.getDecoder().decode(saltString);
    //private final byte[] PASSWORDDIGEST = Base64.getDecoder().decode(digestString);
    //private Cipher cipher;

    public PasswordAESCryptoUtils(String secret) throws NoSuchAlgorithmException {
        byte[] secretByte = secret.getBytes(StandardCharsets.US_ASCII);
        final MessageDigest md = MessageDigest.getInstance("SHA-256");
        this.PASS_DIGEST = md.digest(secretByte);
    }

    public SecretKey derivationKey(String secret) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(secret.toCharArray(), SALT, 65536, 256);
        return factory.generateSecret(spec);
    }



}
