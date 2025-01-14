package io.wangk.peekaboo.common.flow.service;

import io.wangk.peekaboo.common.core.base.service.IBaseService;
import io.wangk.peekaboo.common.flow.model.FlowApiRequestLog;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;

/**
 * 流程API请求日志操作服务接口。
 *
 * @author wangk
 * @date 2025-01-14
 */
public interface FlowApiRequestLogService extends IBaseService<FlowApiRequestLog, Long> {

    /**
     * 保存请求日志。
     *
     * @param instance 流程实例对象。
     * @return 请求日志对象。
     */
    FlowApiRequestLog saveNewFromProcessInstance(ProcessInstance instance);

    /**
     * 保存请求日志。
     *
     * @param instance 流程实例对象。
     * @param task     流程任务对象。
     * @return 请求日志对象。
     */
    FlowApiRequestLog saveNewFromProcessInstanceAndTask(ProcessInstance instance, Task task);

    /**
     * 当前的流程请求Id是否存在。
     *
     * @return 存在返回true，否则false。
     */
    boolean exists();
}
