package io.wangk.peekaboo.common.flow.constant;

/**
 * 待办任务回退类型。
 *
 * @author wangk
 * @date 2025-01-14
 */
public final class FlowBackType {

    /**
     * 驳回。
     */
    public static final int REJECT = 0;
    /**
     * 撤回。
     */
    public static final int REVOKE = 1;

    /**
     * 私有构造函数，明确标识该常量类的作用。
     */
    private FlowBackType() {
    }
}
