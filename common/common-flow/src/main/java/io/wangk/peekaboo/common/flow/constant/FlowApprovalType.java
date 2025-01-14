package io.wangk.peekaboo.common.flow.constant;

/**
 * 工作流任务触发BUTTON。
 *
 * @author wangk
 * @date 2025-01-14
 */
public final class FlowApprovalType {

    /**
     * 保存。
     */
    public static final String SAVE = "save";
    /**
     * 同意。
     */
    public static final String AGREE = "agree";
    /**
     * 拒绝。
     */
    public static final String REFUSE = "refuse";
    /**
     * 驳回。
     */
    public static final String REJECT = "reject";
    /**
     * 撤销。
     */
    public static final String REVOKE = "revoke";
    /**
     * 指派。
     */
    public static final String TRANSFER = "transfer";
    /**
     * 多实例会签。
     */
    public static final String MULTI_SIGN = "multi_sign";
    /**
     * 会签同意。
     */
    public static final String MULTI_AGREE = "multi_agree";
    /**
     * 会签拒绝。
     */
    public static final String MULTI_REFUSE = "multi_refuse";
    /**
     * 会签弃权。
     */
    public static final String MULTI_ABSTAIN = "multi_abstain";
    /**
     * 串行多实例前加签。
     */
    public static final String MULTI_BEFORE_CONSIGN = "multi_before_consign";
    /**
     * 串行多实例后加签。
     */
    public static final String MULTI_AFTER_CONSIGN = "multi_after_consign";
    /**
     * 多实例加签。
     */
    public static final String MULTI_CONSIGN = "multi_consign";
    /**
     * 多实例减签。
     */
    public static final String MULTI_MINUS_SIGN = "multi_minus_sign";
    /**
     * 中止。
     */
    public static final String STOP = "stop";
    /**
     * 干预。
     */
    public static final String INTERVENE = "intervene";
    /**
     * 自由跳转。
     */
    public static final String FREE_JUMP = "free_jump";
    /**
     * 流程复活。
     */
    public static final String REUSED = "reused";
    /**
     * 流程复活。
     */
    public static final String REVIVE = "revive";
    /**
     * 超时自动审批。
     */
    public static final String TIMEOUT_AUTO_COMPLETE = "timeout_auto_complete";
    /**
     * 空审批人自动审批。
     */
    public static final String EMPTY_USER_AUTO_COMPLETE = "empty_user_auto_complete";
    /**
     * 空审批人自动退回。
     */
    public static final String EMPTY_USER_AUTO_REJECT = "empty_user_auto_reject";
    /**
     * 自动化任务。
     */
    public static final String AUTO_FLOW_TASK = "auto_flow_task";

    /**
     * 私有构造函数，明确标识该常量类的作用。
     */
    private FlowApprovalType() {
    }
}
