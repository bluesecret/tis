package io.wangk.peekaboo.common.flow.object;

import lombok.Data;

/**
 * 流程接口的选项对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
public class FlowApiOption {

    /**
     * 是否删除任务超时作业。
     */
    private boolean deleteTimeoutTaskJob = false;
}
