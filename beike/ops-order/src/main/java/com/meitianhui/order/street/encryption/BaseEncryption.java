package com.meitianhui.order.street.encryption;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import com.meitianhui.order.street.exception.EncryptionException;
import com.meitianhui.order.street.exception.ErrorCode;
import com.meitianhui.order.street.exception.ErrorInfo;

import javax.crypto.Cipher;
import java.security.Key;

/**
 * <p> 加密抽象类 </p>
 *
 * @author Tortoise
 * @since 2019/3/27 12:40
 */
public abstract class BaseEncryption implements Encryption {

    /**
     * 加密
     *
     * @param encryptionKey 密钥
     * @param data          待加密的数据
     * @return 加密后的数据
     */
    @Override
    public String encrypt(EncryptionKey encryptionKey, String data) {
        try {
            Cipher cipher = getCipher(encryptionKey, Cipher.ENCRYPT_MODE);
            byte[] bytes = cipher.doFinal(StrUtil.bytes(data));
            return Base64.encode(bytes);
        } catch (Exception e) {
            throw new EncryptionException(ErrorInfo.error(ErrorCode.SYSTEM_ERROR.getValue()
                    , String.format("Encrypted exception [%s]", data)), e);
        }
    }

    /**
     * 解密
     *
     * @param encryptionKey 密钥
     * @param data          待解密的数据
     * @return 解密后的数据
     */
    @Override
    public String decrypt(EncryptionKey encryptionKey, String data) {
        try {
            Cipher cipher = getCipher(encryptionKey, Cipher.DECRYPT_MODE);
            byte[] bytes = cipher.doFinal(Base64.decode(data));
            return StrUtil.str(bytes, CharsetUtil.CHARSET_UTF_8);
        } catch (Exception e) {
            throw new EncryptionException(ErrorInfo.error(ErrorCode.SYSTEM_ERROR.getValue()
                    , String.format("Decrypt exception [%s]", data)), e);
        }
    }

    /**
     * 获取加密填充算法
     *
     * @return 填充算法
     */
    public abstract String getAlgorithm();

    /**
     * 把自定义的密钥转换成需要加解密的密钥
     *
     * @param encryptionKey 自定义密钥
     * @return 加解密密钥
     */
    public abstract Key transformKey(EncryptionKey encryptionKey);

    /**
     * 获取加解密工具类
     *
     * @param encryptionKey 密钥
     * @return Cipher
     */
    Cipher getCipher(EncryptionKey encryptionKey, int mode) {
        try {
            Cipher cipher = Cipher.getInstance(getAlgorithm());
            Key key = transformKey(encryptionKey);
            cipher.init(mode, key);
            return cipher;
        } catch (Exception e) {
            throw new EncryptionException(ErrorInfo.error(ErrorCode.SYSTEM_ERROR.getValue()
                    , String.format("Invalid key fail mode[%d]", mode)), e);
        }
    }

}
