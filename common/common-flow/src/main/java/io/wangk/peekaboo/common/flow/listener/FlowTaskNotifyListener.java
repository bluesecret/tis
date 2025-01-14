package io.wangk.peekaboo.common.flow.listener;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import io.wangk.peekaboo.common.core.util.ApplicationContextHolder;
import io.wangk.peekaboo.common.flow.constant.FlowConstant;
import io.wangk.peekaboo.common.flow.model.FlowTaskExt;
import io.wangk.peekaboo.common.flow.model.FlowWorkOrder;
import io.wangk.peekaboo.common.flow.object.FlowUserTaskExtData;
import io.wangk.peekaboo.common.flow.service.FlowApiService;
import io.wangk.peekaboo.common.flow.service.FlowTaskExtService;
import io.wangk.peekaboo.common.flow.service.FlowWorkOrderService;
import io.wangk.peekaboo.common.flow.util.BaseFlowNotifyExtHelper;
import io.wangk.peekaboo.common.flow.util.FlowCustomExtFactory;
import io.wangk.peekaboo.common.flow.vo.FlowTaskVo;
import io.wangk.peekaboo.common.flow.vo.FlowUserInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.util.Assert;

import java.util.*;

/**
 * 任务进入待办状态时的通知监听器。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Slf4j
public class FlowTaskNotifyListener implements TaskListener {

    private final transient FlowTaskExtService flowTaskExtService =
            ApplicationContextHolder.getBean(FlowTaskExtService.class);
    private final transient FlowApiService flowApiService =
            ApplicationContextHolder.getBean(FlowApiService.class);
    private final transient FlowCustomExtFactory flowCustomExtFactory =
            ApplicationContextHolder.getBean(FlowCustomExtFactory.class);
    private final transient FlowWorkOrderService flowWorkOrderService =
            ApplicationContextHolder.getBean(FlowWorkOrderService.class);

    @Override
    public void notify(DelegateTask delegateTask) {
        String definitionId = delegateTask.getProcessDefinitionId();
        String instanceId = delegateTask.getProcessInstanceId();
        String taskId = delegateTask.getId();
        String taskKey = delegateTask.getTaskDefinitionKey();
        FlowTaskExt taskExt = flowTaskExtService.getByProcessDefinitionIdAndTaskId(definitionId, taskKey);
        if (StrUtil.isBlank(taskExt.getExtraDataJson())) {
            return;
        }
        FlowUserTaskExtData extData = JSON.parseObject(taskExt.getExtraDataJson(), FlowUserTaskExtData.class);
        if (CollUtil.isEmpty(extData.getFlowNotifyTypeList())) {
            return;
        }
        ProcessInstance instance = flowApiService.getProcessInstance(instanceId);
        Object initiator = flowApiService.getProcessInstanceVariable(instanceId, FlowConstant.PROC_INSTANCE_INITIATOR_VAR);
        boolean isMultiInstanceTask = flowApiService.isMultiInstanceTask(definitionId, taskKey);
        Task task = flowApiService.getProcessInstanceActiveTask(instanceId, taskId);
        List<FlowUserInfoVo> userInfoList =
                flowTaskExtService.getCandidateUserInfoList(instanceId, taskExt, task, isMultiInstanceTask, false);
        if (CollUtil.isEmpty(userInfoList)) {
            log.warn("ProcessDefinition [{}] Task [{}] don't find the candidate users for notification.",
                    instance.getProcessDefinitionName(), task.getName());
            return;
        }
        BaseFlowNotifyExtHelper helper = flowCustomExtFactory.getFlowNotifyExtHelper();
        Assert.notNull(helper, "BaseFlowNotifyExtHelper can't be null");
        FlowTaskVo flowTaskVo = new FlowTaskVo();
        flowTaskVo.setProcessDefinitionId(definitionId);
        flowTaskVo.setProcessDefinitionName(instance.getProcessDefinitionName());
        flowTaskVo.setProcessInstanceId(instanceId);
        flowTaskVo.setProcessInstanceInitiator(initiator.toString());
        flowTaskVo.setProcessInstanceStartTime(instance.getStartTime());
        flowTaskVo.setTaskKey(taskKey);
        flowTaskVo.setTaskName(delegateTask.getName());
        flowTaskVo.setTaskId(delegateTask.getId());
        flowTaskVo.setBusinessKey(instance.getBusinessKey());
        Map<String, Object> variables = flowApiService.getTaskVariables(delegateTask.getId());
        FlowWorkOrder flowWorkOrder = flowWorkOrderService.getFlowWorkOrderByProcessInstanceId(instanceId);
        if (flowWorkOrder != null) {
            flowTaskVo.setWorkOrderCode(flowWorkOrder.getWorkOrderCode());
        }
        Assert.notNull(extData.getNotifyMessage(), "NotifyMessage can't be null");
        for (String notifyType : extData.getFlowNotifyTypeList()) {
            helper.doNotify(notifyType, userInfoList, flowTaskVo, extData.getNotifyMessage(), variables);
        }
    }
}
