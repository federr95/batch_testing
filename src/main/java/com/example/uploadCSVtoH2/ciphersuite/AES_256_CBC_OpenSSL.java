package com.example.uploadCSVtoH2.ciphersuite;

import lombok.Data;
import org.apache.commons.io.IOUtils;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Random;

import static java.nio.charset.StandardCharsets.US_ASCII;

@Data
public class AES_256_CBC_OpenSSL {

    private static final String SALTED_STR = "Salted__";
    private static final byte[] SALTED_MAGIC = SALTED_STR.getBytes(US_ASCII);
    public static final int SALT_LENGTH = 8;

    public static void encrypt(String secret, InputStream inputStream, OutputStream outputStream, byte[] iv, byte[] salt) {
        try {
            /*final byte[] pass = password.getBytes(US_ASCII);
            final byte[] salt = generateRandomBytes(SALT_LENGTH);
            // byte[] inBytes = readBytes(input);
            // final byte[] inBytes = IOUtils.toByteArray(input);

            //
            final byte[] passAndSalt = array_concat(pass, salt);
            byte[] hash = new byte[0];
            byte[] keyAndIv = new byte[0];
            for (int i = 0; i < 3 && keyAndIv.length < 48; i++) {
                final byte[] hashData = array_concat(hash, passAndSalt);
                final MessageDigest md = MessageDigest.getInstance("SHA-256");
                hash = md.digest(hashData);
                keyAndIv = array_concat(keyAndIv, hash);
            }

            final byte[] keyValue = Arrays.copyOfRange(keyAndIv, 0, 32);
            final byte[] iv = Arrays.copyOfRange(keyAndIv, 32, 48);
            final SecretKeySpec key = new SecretKeySpec(keyValue, "AES");*/

            //save hash of password
            PasswordAESCryptoUtils passwordAESCryptoUtils = new PasswordAESCryptoUtils(secret);

            // retrieve key and initialize cipher
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec keySpec = new PBEKeySpec(secret.toCharArray(), salt, 65536, 256);
            SecretKey tmp = factory.generateSecret(keySpec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");
            final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));

            InputStream cipherStream = new CipherInputStream(inputStream, cipher);

            byte[] saltLine = new byte[16];
            byte[] salted = "Salted__".getBytes();
            System.arraycopy(salted, 0, saltLine, 0, salted.length);
            System.arraycopy(salt, 0, saltLine, salted.length, salt.length);
            InputStream head = new ByteArrayInputStream(saltLine);
            cipherStream = new ComboInputStream(head, cipherStream);
            IOUtils.copy(cipherStream, outputStream);
            outputStream.close();
            inputStream.close();
            System.out.println("iv: " + Base64.getEncoder().encodeToString(iv) + " sale: " + Base64.getEncoder().encodeToString(salt));
        } catch (IOException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | InvalidKeyException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }

    public static void decrypt(String secret, InputStream inputStream, OutputStream outputStream, byte[] iv, byte[] salt) throws
            IllegalBlockSizeException, BadPaddingException, IOException, InvalidAlgorithmParameterException, InvalidKeyException,
            NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException {

            //final byte[] pass = password.getBytes(US_ASCII);
            final byte[] inBytes = IOUtils.toByteArray(inputStream);

            /*
            final byte[] shouldBeMagic = Arrays.copyOfRange(inBytes, 0, SALTED_MAGIC.length);
            if (!Arrays.equals(shouldBeMagic, SALTED_MAGIC)) {
                throw new IllegalArgumentException("Initial bytes from input do not match OpenSSL SALTED_MAGIC salt value.");
            }

            final byte[] salt = Arrays.copyOfRange(inBytes, SALTED_MAGIC.length, SALTED_MAGIC.length + SALT_LENGTH);

            final byte[] passAndSalt = array_concat(pass, salt);

            byte[] hash = new byte[0];
            byte[] keyAndIv = new byte[0];
            for (int i = 0; i < 3 && keyAndIv.length < 48; i++) {
                final byte[] hashData = array_concat(hash, passAndSalt);
                final MessageDigest md = MessageDigest.getInstance("SHA-256");
                hash = md.digest(hashData);
                keyAndIv = array_concat(keyAndIv, hash);
            }

            final byte[] keyValue = Arrays.copyOfRange(keyAndIv, 0, 32);
            */

            //final SecretKeySpec key = new SecretKeySpec(keyValue, "AES");
            //final byte[] iv = Arrays.copyOfRange(keyAndIv, 32, 48);

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec keySpec = new PBEKeySpec(secret.toCharArray(), salt, 65536, 256);
            SecretKey tmp = factory.generateSecret(keySpec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");
            final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));

            final byte[] clear = cipher.doFinal(inBytes, 16, inBytes.length - 16);
            outputStream.write(clear);
            outputStream.flush();
            //outputStream.close();
            //inputStream.close();
            //System.out.println("iv: " + Base64.getEncoder().encodeToString(iv) + " sale: " + Base64.getEncoder().encodeToString(salt) +
                //" key: " + tmp.getEncoded());
    }


    private static byte[] array_concat(final byte[] a, final byte[] b) {
        final byte[] c = new byte[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    public static byte[] generateRandomBytes(int length) {
        Random r = new SecureRandom();
        byte[] salt = new byte[length];
        r.nextBytes(salt);
        return salt;
    }

    public static byte[] readBytes(InputStream inputStream) throws IOException {
        byte[] b = new byte[1024];
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        int c;
        while ((c = inputStream.read(b)) != -1) {
            os.write(b, 0, c);
        }
        return os.toByteArray();
    }
}