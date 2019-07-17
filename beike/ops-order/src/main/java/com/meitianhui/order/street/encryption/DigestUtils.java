package com.meitianhui.order.street.encryption;

import cn.hutool.core.util.HexUtil;
import com.meitianhui.order.street.exception.EncryptionException;
import com.meitianhui.order.street.exception.ErrorCode;
import com.meitianhui.order.street.exception.ErrorInfo;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * <p> 摘要加密工具类 </p>
 *
 * @author Tortoise
 * @since 2019/3/27 12:40
 */
@SuppressWarnings("unused")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DigestUtils {

    /**
     * MD5加密
     *
     * @param data 待加密数据
     * @return 加密后的数据
     */
    public static String md5(String data) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(data.getBytes());
            return HexUtil.encodeHexStr(md.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new EncryptionException(ErrorInfo.error(ErrorCode.SYSTEM_ERROR.getValue()
                    , String.format("Failed to get the Message MD5 digest instance [%s]", data)), e);
        }

    }

    /**
     * MD5加密
     *
     * @param data 待加密数据
     * @return 加密后的数据
     */
    public static String md5(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(data);
            return HexUtil.encodeHexStr(md.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new EncryptionException(ErrorInfo.error(ErrorCode.SYSTEM_ERROR.getValue()
                    , String.format("Failed to get the Message MD5 digest instance [%s]", Arrays.toString(data))), e);
        }
    }

    /**
     * SHA加密
     *
     * @param data 待加密数据
     * @return 加密后的数据
     */
    public static String sha(String data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA");
            md.update(data.getBytes());
            return HexUtil.encodeHexStr(md.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new EncryptionException(ErrorInfo.error(ErrorCode.SYSTEM_ERROR.getValue()
                    , String.format("Failed to get the Message SHA digest instance [%s]", data)), e);
        }
    }

    /**
     * SHA加密
     *
     * @param data 待加密数据
     * @return 加密后的数据
     */
    public static String sha(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA");
            md.update(data);
            return HexUtil.encodeHexStr(md.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new EncryptionException(ErrorInfo.error(ErrorCode.SYSTEM_ERROR.getValue()
                    , String.format("Failed to get the Message SHA digest instance [%s]", Arrays.toString(data))), e);
        }
    }

}
