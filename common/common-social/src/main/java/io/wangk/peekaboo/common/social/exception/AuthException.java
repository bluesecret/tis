package io.wangk.peekaboo.common.social.exception;

/**
 * 第三方授权的异常。
 *
 * @author wangk
 * @date 2025-01-14
 */
public class AuthException extends RuntimeException {

    private String msg;

    /**
     * 构造函数。
     */
    public AuthException() {

    }

    /**
     * 构造函数。
     *
     * @param throwable 引发异常对象。
     */
    public AuthException(Throwable throwable) {
        super(throwable);
    }

    /**
     * 构造函数。
     *
     * @param msg 错误信息。
     */
    public AuthException(String msg) {
        super();
        this.msg = msg;
    }

    /**
     * 构造函数。
     *
     * @param msg       错误信息。
     * @param throwable 引发异常对象。
     */
    public AuthException(String msg, Throwable throwable) {
        super(throwable);
        this.msg = msg;
    }

    @Override
    public String getMessage() {
        return msg;
    }
}
