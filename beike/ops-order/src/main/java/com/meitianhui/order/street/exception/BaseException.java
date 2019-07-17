package com.meitianhui.order.street.exception;

/**
 * 基础异常
 *
 * @author Tortoise
 * @since 2019/3/27 12:40
 */
public class BaseException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误
     */
    private ErrorInfo errorInfo;

    public BaseException(ErrorInfo errorInfo) {
        super(errorInfo.getMsg());
        this.errorInfo = errorInfo;
    }

    public BaseException(ErrorInfo errorInfo, Throwable cause) {
        super(errorInfo.getMsg(), cause);
        this.errorInfo = errorInfo;
    }

    /**
     * 获取错误
     *
     * @return 错误
     */
    public ErrorInfo getErrorInfo() {
        return errorInfo;
    }
}
