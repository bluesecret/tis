package io.wangk.peekaboo.common.flow.model.constant;

/**
 * 超时任务作业状态。
 *
 * @author wangk
 * @date 2025-01-14
 */
public final class FlowTaskTimeoutJobStatus {

    /**
     * 未执行。
     */
    public static final int NO_EXEC = 0;
    /**
     * 执行成功。
     */
    public static final int SUCCESS = 1;
    /**
     * 执行失败。
     */
    public static final int FAIL = 2;

    /**
     * 私有构造函数，明确标识该常量类的作用。
     */
    private FlowTaskTimeoutJobStatus() {
    }
}
