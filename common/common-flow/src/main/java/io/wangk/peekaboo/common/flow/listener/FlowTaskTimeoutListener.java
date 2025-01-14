package io.wangk.peekaboo.common.flow.listener;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import io.wangk.peekaboo.common.core.util.ApplicationContextHolder;
import io.wangk.peekaboo.common.flow.model.FlowTaskExt;
import io.wangk.peekaboo.common.flow.model.FlowTaskTimeoutJob;
import io.wangk.peekaboo.common.flow.object.FlowUserTaskExtData;
import io.wangk.peekaboo.common.flow.service.FlowTaskExtService;
import io.wangk.peekaboo.common.flow.service.FlowTaskTimeoutJobService;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.task.service.delegate.DelegateTask;

/**
 * 流程任务超时监听器。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Slf4j
public class FlowTaskTimeoutListener implements TaskListener {

    private final transient FlowTaskExtService flowTaskExtService =
            ApplicationContextHolder.getBean(FlowTaskExtService.class);
    private final transient FlowTaskTimeoutJobService flowTaskTimeoutJobService =
            ApplicationContextHolder.getBean(FlowTaskTimeoutJobService.class);

    @Override
    public void notify(DelegateTask delegateTask) {
        FlowTaskExt taskExt = flowTaskExtService.getByProcessDefinitionIdAndTaskId(
                delegateTask.getProcessDefinitionId(), delegateTask.getTaskDefinitionKey());
        if (StrUtil.isNotBlank(taskExt.getExtraDataJson())) {
            FlowUserTaskExtData taskExtData = JSON.parseObject(taskExt.getExtraDataJson(), FlowUserTaskExtData.class);
            if (StrUtil.isNotBlank(taskExtData.getTimeoutHandleWay())) {
                FlowTaskTimeoutJob job = new FlowTaskTimeoutJob();
                job.setProcessDefinitionId(delegateTask.getProcessDefinitionId());
                job.setProcessInstanceId(delegateTask.getProcessInstanceId());
                job.setTaskKey(delegateTask.getTaskDefinitionKey());
                job.setTaskId(delegateTask.getId());
                job.setTimeoutHours(taskExtData.getTimeoutHours());
                job.setHandleWay(taskExtData.getTimeoutHandleWay());
                job.setDefaultAssignee(taskExtData.getDefaultAssignee());
                flowTaskTimeoutJobService.saveNew(job);
            }
        }
    }
}
