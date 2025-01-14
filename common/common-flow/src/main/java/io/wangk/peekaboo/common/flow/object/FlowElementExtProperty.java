package io.wangk.peekaboo.common.flow.object;

import lombok.Data;

/**
 * 流程任务的扩展属性。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
public class FlowElementExtProperty {

    /**
     * 最近的审批状态，该值目前仅仅用于流程线元素，即SequenceElement。
     */
    private Integer latestApprovalStatus;
}
