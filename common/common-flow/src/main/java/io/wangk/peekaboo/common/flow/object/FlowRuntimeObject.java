package io.wangk.peekaboo.common.flow.object;

import lombok.Data;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;

/**
 * 工作流运行时常用对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
public class FlowRuntimeObject {

    /**
     * 运行时流程实例对象。
     */
    private ProcessInstance instance;
    /**
     * 运行时流程任务对象。
     */
    private Task task;
}
