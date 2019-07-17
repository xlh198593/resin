package com.meitianhui.order.street.encryption;

import cn.hutool.core.util.StrUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Objects;

/**
 * <p> Aes加密工具类，单例且线程安全</p>
 *
 * @author Tortoise
 * @since 2019/3/27 12:40
 */
@SuppressWarnings({"unused", "Duplicates"})
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Aes extends BaseEncryption {

    public final static EncryptionKey ENCRYPTION_KEY = EncryptionKey.builder().key("43c34c1e27971d3474763a83f696193d").build();

    /**
     * Key生成算法
     */
    private static final String KEY_ALGORITHM = "Aes";

    /**
     * 加密填充算法
     */
    private static final String ALGORITHM = "Aes/ECB/PKCS5Padding";

    public static Aes getInstance() {
        return AesEncryptionHolder.instance;
    }

    @Override
    public String getAlgorithm() {
        return ALGORITHM;
    }

    /**
     * 把自定义的密钥转换成需要加解密的密钥，AES密钥只能为128、192、256位，对应字节数为16、24、32
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
        int[] keySizes = new int[]{16, 24, 32};
        int keySize = keySizes[0];
        if (encryptionKeys.length > keySizes[0] && encryptionKeys.length <= keySizes[1]) {
            keySize = keySizes[1];
        } else if (encryptionKeys.length > keySizes[1]) {
            keySize = keySizes[2];
        }

        byte[] keys = new byte[keySize];
        if (encryptionKeys.length < keySize) {
            System.arraycopy(encryptionKeys, 0, keys, 0, encryptionKeys.length);
        } else {
            System.arraycopy(encryptionKeys, 0, keys, 0, keySize);
        }

        //获得原始对称密钥的字节数组并根据字节数组生成密钥
        return new SecretKeySpec(keys, KEY_ALGORITHM);
    }

    private static class AesEncryptionHolder {
        private static Aes instance = new Aes();
    }

}
