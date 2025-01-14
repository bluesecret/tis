package io.wangk.peekaboo.common.flow.constant;

/**
 * 工作流任务类型。
 *
 * @author wangk
 * @date 2025-01-14
 */
public final class FlowTaskStatus {

    /**
     * 已提交。
     */
    public static final int SUBMITTED = 0;
    /**
     * 审批中。
     */
    public static final int APPROVING = 1;
    /**
     * 被拒绝。
     */
    public static final int REFUSED = 2;
    /**
     * 已结束。
     */
    public static final int FINISHED = 3;
    /**
     * 提前停止。
     */
    public static final Integer STOPPED = 4;
    /**
     * 已取消。
     */
    public static final Integer CANCELLED = 5;
    /**
     * 保存草稿。
     */
    public static final Integer DRAFT = 6;
    /**
     * 流程复活。
     */
    public static final Integer REVIVE = 7;

    /**
     * 私有构造函数，明确标识该常量类的作用。
     */
    private FlowTaskStatus() {
    }
}
