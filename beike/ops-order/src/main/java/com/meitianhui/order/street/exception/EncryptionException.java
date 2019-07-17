package com.meitianhui.order.street.exception;

/**
 * <p> 加密异常 </p>
 *
 * @author Tortoise
 * @since 2019/3/27 12:40
 */
public class EncryptionException extends BaseException {

    private static final long serialVersionUID = 1L;

    public EncryptionException(ErrorInfo errorInfo) {
        super(errorInfo);
    }

    public EncryptionException(ErrorInfo errorInfo, Throwable cause) {
        super(errorInfo, cause);
    }

}
