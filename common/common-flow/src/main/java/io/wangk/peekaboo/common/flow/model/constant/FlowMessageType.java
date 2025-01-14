package io.wangk.peekaboo.common.flow.model.constant;

/**
 * 工作流消息类型。
 *
 * @author wangk
 * @date 2025-01-14
 */
public final class FlowMessageType {

    /**
     * 催办消息。
     */
    public static final int REMIND_TYPE = 0;

    /**
     * 抄送消息。
     */
    public static final int COPY_TYPE = 1;

    /**
     * 私有构造函数，明确标识该常量类的作用。
     */
    private FlowMessageType() {
    }
}
