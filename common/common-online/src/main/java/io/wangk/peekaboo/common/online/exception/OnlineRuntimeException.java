package io.wangk.peekaboo.common.online.exception;

import io.wangk.peekaboo.common.core.exception.MyRuntimeException;

/**
 * 在线表单运行时异常。
 *
 * @author wangk
 * @date 2025-01-14
 */
public class OnlineRuntimeException extends MyRuntimeException {

    /**
     * 构造函数。
     */
    public OnlineRuntimeException() {

    }

    /**
     * 构造函数。
     *
     * @param msg 错误信息。
     */
    public OnlineRuntimeException(String msg) {
        super(msg);
    }
}
