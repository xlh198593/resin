package com.meitianhui.order.street.encryption;

import cn.hutool.core.util.StrUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Objects;

/**
 * <p> Des加密工具类，单例且线程安全</p>
 *
 * @author Tortoise
 * @since 2019/3/27 12:40
 */
@SuppressWarnings({"unused", "Duplicates"})
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Des extends BaseEncryption {

    /**
     * Key生成算法
     */
    private static final String KEY_ALGORITHM = "Des";

    /**
     * 加密填充算法
     */
    private static final String ALGORITHM = "Des/ECB/PKCS5Padding";

    public static Des getInstance() {
        return DesEncryptionHolder.instance;
    }

    @Override
    public String getAlgorithm() {
        return ALGORITHM;
    }

    /**
     * 把自定义的密钥转换成需要加解密的密钥，AES密钥只能为64位，对应字节数为8
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
        int keySize = 8;
        byte[] keys = new byte[keySize];
        if (encryptionKeys.length < keySize) {
            System.arraycopy(encryptionKeys, 0, keys, 0, encryptionKeys.length);
        } else {
            System.arraycopy(encryptionKeys, 0, keys, 0, keySize);
        }

        //获得原始对称密钥的字节数组并根据字节数组生成密钥
        return new SecretKeySpec(keys, KEY_ALGORITHM);
    }

    private static class DesEncryptionHolder {
        private static Des instance = new Des();
    }

}
