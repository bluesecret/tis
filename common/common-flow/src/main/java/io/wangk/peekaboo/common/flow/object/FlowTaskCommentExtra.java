package io.wangk.peekaboo.common.flow.object;

import lombok.Data;

/**
 * 流程审批信息对象的附加数据。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
public class FlowTaskCommentExtra {

    /**
     * 驳回后再次提交的源任务的Id。
     */
    private String rejectBackSourceTaskId;

    /**
     * 驳回后再次提交的目标任务标识。
     */
    private String rejectBackTargetTaskKey;

    /**
     * 驳回时的用户名。
     */
    private String rejectBackTargetUser;

    /**
     * 是否驳回后再次提交到之前驳回任务的操作。
     */
    private Boolean rejectBackType = false;

    /**
     * 审批人变量名。
     */
    private String identityVariableName;

    /**
     * 审批人变量值。
     */
    private String identityVariableValue;
}
