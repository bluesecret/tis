package io.wangk.peekaboo.common.flow.listener;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import io.wangk.peekaboo.common.core.object.TokenData;
import io.wangk.peekaboo.common.core.util.ApplicationContextHolder;
import io.wangk.peekaboo.common.core.util.MyCommonUtil;
import io.wangk.peekaboo.common.flow.constant.FlowApprovalType;
import io.wangk.peekaboo.common.flow.constant.FlowConstant;
import io.wangk.peekaboo.common.flow.model.FlowMultiInstanceTrans;
import io.wangk.peekaboo.common.flow.model.FlowTaskComment;
import io.wangk.peekaboo.common.flow.model.FlowTaskExt;
import io.wangk.peekaboo.common.flow.service.FlowMultiInstanceTransService;
import io.wangk.peekaboo.common.flow.service.FlowTaskCommentService;
import io.wangk.peekaboo.common.flow.service.FlowTaskExtService;
import io.wangk.peekaboo.common.flow.vo.FlowUserInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 并行多实例任务的执行监听器。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Slf4j
public class FlowMultiInstanceListener implements ExecutionListener {

    private final transient FlowTaskExtService flowTaskExtService =
            ApplicationContextHolder.getBean(FlowTaskExtService.class);
    private final transient FlowMultiInstanceTransService flowMultiInstanceTransService =
            ApplicationContextHolder.getBean(FlowMultiInstanceTransService.class);
    private final transient FlowTaskCommentService flowTaskCommentService =
            ApplicationContextHolder.getBean(FlowTaskCommentService.class);
    private final transient RuntimeService runtimeService =
            ApplicationContextHolder.getBean(RuntimeService.class);

    @Override
    public void notify(DelegateExecution d) {
        Object approvalType = d.getVariable(FlowConstant.OPERATION_TYPE_VAR);
        if (ObjectUtil.equals(approvalType, FlowApprovalType.MULTI_SIGN)
                || !d.getParentId().equals(d.getProcessInstanceId())) {
            return;
        }
        FlowTaskExt flowTaskExt =
                flowTaskExtService.getByProcessDefinitionIdAndTaskId(d.getProcessDefinitionId(), d.getCurrentActivityId());
        List<FlowUserInfoVo> flowUserInfoVoList =
                flowTaskExtService.getCandidateUserInfoList(d.getProcessInstanceId(), d.getId(), flowTaskExt);
        String assigneeList = flowUserInfoVoList.stream()
                .map(FlowUserInfoVo::getLoginName).collect(Collectors.joining(StrUtil.COMMA));
        String loginName = TokenData.takeFromRequest().getLoginName();
        Assert.isTrue(StrUtil.isNotBlank(assigneeList));
        Map<String, Object> variables = new HashMap<>();
        variables.put(FlowConstant.MULTI_ASSIGNEE_LIST_VAR, StrUtil.split(assigneeList, StrUtil.COMMA));
        variables.put(FlowConstant.MULTI_AGREE_COUNT_VAR, 0);
        variables.put(FlowConstant.MULTI_REFUSE_COUNT_VAR, 0);
        variables.put(FlowConstant.MULTI_ABSTAIN_COUNT_VAR, 0);
        variables.put(FlowConstant.MULTI_SIGN_NUM_OF_INSTANCES_VAR, 0);
        String multiInstanceExecId = MyCommonUtil.generateUuid();
        variables.put(FlowConstant.MULTI_SIGN_TASK_EXECUTION_ID_VAR, multiInstanceExecId);
        runtimeService.setVariables(d.getId(), variables);
        FlowMultiInstanceTrans multiInstanceTrans = new FlowMultiInstanceTrans();
        multiInstanceTrans.setTaskKey(d.getCurrentActivityId());
        multiInstanceTrans.setProcessInstanceId(d.getProcessInstanceId());
        multiInstanceTrans.setExecutionId(d.getId());
        multiInstanceTrans.setMultiInstanceExecId(multiInstanceExecId);
        multiInstanceTrans.setAssigneeList(assigneeList);
        //这里的taskId为虚拟任务Id，只是为了保证与之前表结构的兼容性。
        multiInstanceTrans.setTaskId(multiInstanceExecId);
        flowMultiInstanceTransService.saveNew(multiInstanceTrans);
        FlowTaskComment flowTaskComment = new FlowTaskComment();
        flowTaskComment.setTaskId(multiInstanceExecId);
        flowTaskComment.setTaskKey(d.getCurrentActivityId());
        flowTaskComment.setTaskName(d.getCurrentFlowElement().getName());
        flowTaskComment.setProcessInstanceId(d.getProcessInstanceId());
        flowTaskComment.setExecutionId(d.getId());
        flowTaskComment.setApprovalType(FlowApprovalType.MULTI_SIGN);
        String comment = String.format("用户 [%s] 会签 [%s]。", loginName, assigneeList);
        flowTaskComment.setTaskComment(comment);
        flowTaskCommentService.saveNew(flowTaskComment);
    }
}
