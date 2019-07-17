package com.meitianhui.order.street.encryption;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p> 加解密接口 </p>
 *
 * @author Tortoise
 * @since 2019-01-22
 */
public interface Encryption {

    /**
     * 加密
     *
     * @param encryptionKey EncryptionKey
     * @param data          String
     * @return String
     */
    String encrypt(EncryptionKey encryptionKey, String data);

    /**
     * 解密
     *
     * @param encryptionKey EncryptionKey
     * @param data          String
     * @return String
     */
    String decrypt(EncryptionKey encryptionKey, String data);


    /**
     * <p> 密钥类型 </p>
     *
     * @author Tortoise
     * @since 2018-08-23
     */
    enum EncryptionKeyType {

        /**
         * 默认
         */
        DEFAULT,
        /**
         * 公钥
         */
        PUBLIC,
        /**
         * 私钥
         */
        PRIVATE

    }

    /**
     * <p> 密钥，密钥采用Base64编号 </p>
     *
     * @author Tortoise
     * @since 2018-08-23
     */
    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class EncryptionKey {

        /**
         * 加密类型
         */
        @Builder.Default
        private EncryptionKeyType encryptionKeyType = EncryptionKeyType.DEFAULT;

        /**
         * 密钥
         */
        private String key;

    }

}
