package io.wangk.peekaboo.common.flow.object;

import lombok.Data;

import java.util.List;

/**
 * 流程驳回数据。主要用于RejectType为1，既重新提交会驳回人的驳回类型。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
public class FlowRejectData {

    /**
     * 驳回用户名。
     */
    private String sourceUser;
    /**
     * 从哪个任务驳回的任务定义标识。
     */
    private String sourceTaskKey;
    /**
     * 驳回到的目标任务定义标识。
     */
    private List<String> targetTaskKeys;
}
