package com.meitianhui.order.street.encryption;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import com.meitianhui.order.street.exception.EncryptionException;
import com.meitianhui.order.street.exception.ErrorCode;
import com.meitianhui.order.street.exception.ErrorInfo;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Objects;

/**
 * <p> Desede加密工具类，简称3DES加密，单例且线程安全</p>
 *
 * @author Tortoise
 * @since 2019/3/27 12:40
 */
@SuppressWarnings({"unused", "Duplicates"})
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Desede extends BaseEncryption {

    /**
     * Key生成算法
     */
    private static final String KEY_ALGORITHM = "Desede";

    /**
     * 加密填充算法
     */
    private static final String ALGORITHM = "Desede/ECB/PKCS5Padding";

    public static Desede getInstance() {
        return DesedeEncryptionHolder.instance;
    }

    @Override
    public String getAlgorithm() {
        return ALGORITHM;
    }

    @Override
    public String encrypt(EncryptionKey encryptionKey, String data) {
        try {
            Cipher cipher = getCipher(encryptionKey, Cipher.ENCRYPT_MODE);
            byte[] bytes = cipher.doFinal(StrUtil.bytes(data));
            return new String(Hex.encodeHex(bytes));
        } catch (IllegalBlockSizeException e) {
            throw new EncryptionException(ErrorInfo.error(ErrorCode.SYSTEM_ERROR.getValue()
                    , String.format("Encrypted illegal block size exception [%s]", data)), e);
        } catch (BadPaddingException e) {
            throw new EncryptionException(ErrorInfo.error(ErrorCode.SYSTEM_ERROR.getValue()
                    , String.format("Encrypted bad padding exception [%s]", data)), e);
        }
    }

    @Override
    public String decrypt(EncryptionKey encryptionKey, String data) {
        try {
            Cipher cipher = getCipher(encryptionKey, Cipher.DECRYPT_MODE);
            byte[] bytes = cipher.doFinal(Hex.decodeHex(data.toCharArray()));
            return StrUtil.str(bytes, CharsetUtil.CHARSET_UTF_8);
        } catch (IllegalBlockSizeException e) {
            throw new EncryptionException(ErrorInfo.error(ErrorCode.SYSTEM_ERROR.getValue()
                    , String.format("Encrypted illegal block size exception [%s]", data)), e);
        } catch (BadPaddingException e) {
            throw new EncryptionException(ErrorInfo.error(ErrorCode.SYSTEM_ERROR.getValue()
                    , String.format("Encrypted bad padding exception [%s]", data)), e);
        } catch (DecoderException e) {
            throw new EncryptionException(ErrorInfo.error(ErrorCode.SYSTEM_ERROR.getValue()
                    , String.format("Encrypted decoder exception [%s]", data)), e);
        }
    }

    /**
     * 把自定义的密钥转换成需要加解密的密钥，AES密钥只能为192位，对应字节数为24
     *
     * @param encryptionKey 自定义密钥
     * @return 加解密密钥
     */
    @Override
    public Key transformKey(EncryptionKey encryptionKey) {
        if (!Objects.equals(EncryptionKeyType.DEFAULT, encryptionKey.getEncryptionKeyType())) {
            throw new IllegalArgumentException("Encryption key mismatch");
        }

        byte[] encryptionKeys = StrUtil.bytes(encryptionKey.getKey());
        int keySize = 24;
        byte[] keys = new byte[keySize];
        if (encryptionKeys.length < keySize) {
            System.arraycopy(encryptionKeys, 0, keys, 0, encryptionKeys.length);
        } else {
            System.arraycopy(encryptionKeys, 0, keys, 0, keySize);
        }

        //获得原始对称密钥的字节数组并根据字节数组生成密钥
        return new SecretKeySpec(keys, KEY_ALGORITHM);
    }

    private static class DesedeEncryptionHolder {
        private static Desede instance = new Desede();
    }

}
