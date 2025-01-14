package io.wangk.peekaboo.common.flow.listener;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import io.wangk.peekaboo.common.core.util.ApplicationContextHolder;
import io.wangk.peekaboo.common.flow.service.FlowApiService;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;

import java.util.Map;

/**
 * 空审批人审批人检测监听器。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Slf4j
public class FlowEmptyUserExecListener implements ExecutionListener {

    private final transient FlowApiService flowApiService =
            ApplicationContextHolder.getBean(FlowApiService.class);

    @Override
    public void notify(DelegateExecution d) {
        UserTask userTask = flowApiService.getUserTask(d.getProcessDefinitionId(), d.getCurrentActivityId());
        String assignee = userTask.getAssignee();
        if (StrUtil.isBlank(assignee)) {
            return;
        }
        if (!flowApiService.isVariableString(assignee)) {
            return;
        }
        String v = flowApiService.extractVariableName(assignee);
        Map<String, Object> variables = d.getVariables();
        if (MapUtil.isEmpty(variables)) {
            d.setVariable(v, "");
        } else {
            Object o = variables.get(v);
            if (o == null) {
                d.setVariable(v, "");
            }
        }
    }
}
