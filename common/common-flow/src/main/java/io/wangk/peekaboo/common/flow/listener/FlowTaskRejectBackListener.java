package io.wangk.peekaboo.common.flow.listener;

import cn.hutool.core.util.ObjectUtil;
import io.wangk.peekaboo.common.core.util.ApplicationContextHolder;
import io.wangk.peekaboo.common.flow.constant.FlowConstant;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.task.service.delegate.DelegateTask;

import java.util.Map;

/**
 * 任务驳回后重新提交到该任务的通知监听器。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Slf4j
public class FlowTaskRejectBackListener implements TaskListener {

    private final transient RuntimeService runtimeService =
            ApplicationContextHolder.getBean(RuntimeService.class);

    @Override
    public void notify(DelegateTask delegateTask) {
        Map<String, Object> variables = delegateTask.getVariables();
        Object v = variables.get(FlowConstant.REJECT_BACK_TO_SOURCE_DATA_VAR);
        if (ObjectUtil.isNotEmpty(v)) {
            delegateTask.setAssignee(v.toString());
            runtimeService.removeVariableLocal(delegateTask.getExecutionId(), FlowConstant.REJECT_BACK_TO_SOURCE_DATA_VAR);
        }
    }
}
