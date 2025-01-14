package io.wangk.peekaboo.common.flow.service;

import io.wangk.peekaboo.common.core.base.service.IBaseService;
import io.wangk.peekaboo.common.flow.model.FlowTaskTimeoutJob;
import org.flowable.task.api.Task;

import java.util.List;

/**
 * 超时任务作业操作服务接口。
 *
 * @author wangk
 * @date 2025-01-14
 */
public interface FlowTaskTimeoutJobService extends IBaseService<FlowTaskTimeoutJob, Long> {

    /**
     * 保存超时任务作业对象。
     *
     * @param job 超时任务作业对象。
     */
    void saveNew(FlowTaskTimeoutJob job);

    /**
     * 执行超时任务作业对应的业务逻辑。
     *
     * @param job  超时任务作业对象。
     * @param task 运行时的待办任务对象。
     */
    void executeJob(FlowTaskTimeoutJob job, Task task);

    /**
     * 删除当前流程运行时任务的超时作业数据。这里仅删除未执行过的数据。
     *
     * @param taskId 运行时任务Id。
     */
    void deleteByTaskId(String taskId);

    /**
     * 删除当前流程实例的超时作业数据。这里仅删除非执行失败的数据。
     *
     * @param processInstanceId 流程实例Id。
     */
    void deleteByProcessInstanceId(String processInstanceId);

    /**
     * 获取可执行的Job列表。即状态为0同时execTime小于当前时间的任务。
     *
     * @return 可执行的Job列表。
     */
    List<FlowTaskTimeoutJob> getExecutableList();
}
