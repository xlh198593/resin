package com.meitianhui.member.util;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;

public class AESTool {
    private static final Logger LOGGER = LoggerFactory.getLogger(AESTool.class);
 
    private static final String KEY_ALGORITHM = "AES";
    private static final String CHAR_SET = "UTF-8";
    private static final String KEY = "MTHT8c13R9BE9hf7";
    private static final Integer SECRET_KEY_LENGTH = 128;
    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";
 
    /**
     * AES加密操作
     */
    public static String encrypt(String content) {
        try {
            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
            byte[] byteContent = content.getBytes(CHAR_SET);
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey());
            byte[] encryptByte = cipher.doFinal(byteContent);
            return Base64.encodeBase64String(encryptByte);
        } catch (Exception e) {
            LOGGER.error("AES encryption operation has exception,content:{},password:{}", content, e);
        }
        return null;
    }
 
    /**
     * AES解密操作
     */
    public static String decrypt(String encryptContent) {
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey());
            byte[] result = cipher.doFinal(Base64.decodeBase64(encryptContent));
            return new String(result, CHAR_SET);
        } catch (Exception e) {
            LOGGER.error("AES decryption operation has exception,content:{},password:{}", encryptContent, e);
        }
        return null;
    }
 
    private static SecretKeySpec getSecretKey() throws NoSuchAlgorithmException {
        return new SecretKeySpec(KEY.getBytes(), KEY_ALGORITHM);
    }
 
    public static void main(String[] args) throws Exception {
        String str = "5811af86b640cc807570bba0083d299b";
        System.out.println("str: " + str);
 
        String encryptStr = encrypt(str);
        System.out.println("encrypt: " + encryptStr);
 
        String decryptStr = decrypt(encryptStr);
        System.out.println("decryptStr: " + decryptStr);
    }

 
}
