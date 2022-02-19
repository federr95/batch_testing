package com.example.uploadCSVtoH2.ciphersuite;

import lombok.Data;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

@Data
public class CipherSuite {

    private String salt;
    private String password;
    private SecretKey secretKey;
    private IvParameterSpec ivParameterSpec;
    private String symAlgorithm;
    private Integer symKeySize;

    public CipherSuite(String password, String salt, String symAlgorithm, Integer symKeySize) throws NoSuchAlgorithmException, InvalidKeySpecException {
        this.password = password;
        this.salt = salt;
        this.symAlgorithm = symAlgorithm;
        this.symKeySize = symKeySize;
        this.ivParameterSpec = generateIv();
        this.secretKey = getKeyFromPassword(password, salt, symAlgorithm, symKeySize);
    }

    public SecretKey getKeyFromPassword(String password, String salt, String symAlgorithm, int symKeySize)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 1, symKeySize);
        SecretKey secretKey = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), symAlgorithm);
        return secretKey;
    }

    public static void encryptFile(String algorithm, SecretKey secretKey, IvParameterSpec ivParameterSpec) throws IOException, NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {

        Resource resource = new ClassPathResource("fake_csv_1000000.csv");
        File inputFile = resource.getFile();
        String fileName = "src/main/resources/fake_csv_1000000.encrypted";
        File encryptedFile = new File(fileName);
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);

        FileInputStream inputStream = new FileInputStream(inputFile);
        FileOutputStream outputStream = new FileOutputStream(encryptedFile);
        byte[] buffer = new byte[64];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byte[] output = cipher.update(buffer, 0, bytesRead);
            if (output != null) {
                outputStream.write(output);
            }
        }
        byte[] outputBytes = cipher.doFinal();
        if (outputBytes != null) {
            outputStream.write(outputBytes);
        }
        inputStream.close();
        outputStream.close();
    }

    public static void decryptFile(String algorithm, SecretKey secretKey, IvParameterSpec ivParameterSpec) throws IOException, NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {

        Resource resource = new ClassPathResource("fake_csv_1000000.encrypted");
        File encryptedFile = resource.getFile();
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
        FileInputStream inputStream = new FileInputStream(encryptedFile);
        String fileName = "src/main/resources/fake_csv_1000000_decrypted_version.csv";
        File decryptedFile = new File(fileName);
        FileOutputStream outputStream = new FileOutputStream(decryptedFile);
        byte[] buffer = new byte[64];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byte[] output = cipher.update(buffer, 0, bytesRead);
            if (output != null) {
                outputStream.write(output);
            }
        }
        byte[] output = cipher.doFinal();
        if (output != null) {
            outputStream.write(output);
        }
        inputStream.close();
        outputStream.close();
    }

    public static IvParameterSpec generateIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }
}
