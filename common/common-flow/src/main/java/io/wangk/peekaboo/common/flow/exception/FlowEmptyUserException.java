package io.wangk.peekaboo.common.flow.exception;

import org.flowable.common.engine.api.FlowableException;

/**
 * 流程空用户异常。
 *
 * @author wangk
 * @date 2025-01-14
 */
public class FlowEmptyUserException extends FlowableException {

    /**
     * 构造函数。
     *
     * @param msg 错误信息。
     */
    public FlowEmptyUserException(String msg) {
        super(msg);
    }
}
