package io.wangk.peekaboo.common.flow.listener;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.wangk.peekaboo.common.core.util.ApplicationContextHolder;
import io.wangk.peekaboo.common.flow.constant.FlowApprovalType;
import io.wangk.peekaboo.common.flow.exception.FlowEmptyUserException;
import io.wangk.peekaboo.common.flow.model.FlowTaskComment;
import io.wangk.peekaboo.common.flow.model.FlowTaskExt;
import io.wangk.peekaboo.common.flow.object.FlowUserTaskExtData;
import io.wangk.peekaboo.common.flow.service.FlowApiService;
import io.wangk.peekaboo.common.flow.service.FlowTaskExtService;
import io.wangk.peekaboo.common.flow.vo.FlowUserInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.task.api.Task;
import org.flowable.task.service.delegate.DelegateTask;

import java.util.List;

/**
 * 空审批人处理监听器。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Slf4j
public class FlowEmptyUserHandleListener implements TaskListener {

    private final transient FlowTaskExtService flowTaskExtService =
            ApplicationContextHolder.getBean(FlowTaskExtService.class);
    private final transient FlowApiService flowApiService =
            ApplicationContextHolder.getBean(FlowApiService.class);

    @Override
    public void notify(DelegateTask t) {
        FlowTaskExt flowTaskExt = flowTaskExtService.getByProcessDefinitionIdAndTaskId(
                t.getProcessDefinitionId(), t.getTaskDefinitionKey());
        Task task = flowApiService.getTaskById(t.getId());
        List<FlowUserInfoVo> userInfoVoList =
                flowTaskExtService.getCandidateUserInfoList(t.getProcessInstanceId(), flowTaskExt, task,
                        flowApiService.isMultiInstanceTask(t.getProcessDefinitionId(), t.getTaskDefinitionKey()), false);
        if (CollUtil.isNotEmpty(userInfoVoList)) {
            return;
        }
        FlowUserTaskExtData taskExtData =
                JSON.parseObject(flowTaskExt.getExtraDataJson(), FlowUserTaskExtData.class);
        //如果为空则默认是自动审批
        if (StrUtil.isBlank(taskExtData.getEmptyUserHandleWay())) {
            taskExtData.setEmptyUserHandleWay(FlowUserTaskExtData.EMPTY_USER_AUTO_COMPLETE);
        }
        switch (taskExtData.getEmptyUserHandleWay()) {
            case FlowUserTaskExtData.EMPTY_USER_TO_ASSIGNEE:
                t.setAssignee(taskExtData.getEmptyUserToAssignee());
                break;
            case FlowUserTaskExtData.EMPTY_USER_AUTO_REJECT:
            case FlowUserTaskExtData.EMPTY_USER_AUTO_COMPLETE:
                FlowTaskComment comment = new FlowTaskComment();
                comment.fillWith(task);
                if (taskExtData.getEmptyUserHandleWay().equals(FlowUserTaskExtData.EMPTY_USER_AUTO_REJECT)) {
                    comment.setApprovalType(FlowApprovalType.EMPTY_USER_AUTO_REJECT);
                    comment.setTaskComment("空审批人自动回退审批");
                    throw new FlowEmptyUserException(JSON.toJSONString(comment));
                } else {
                    JSONObject taskVariables = new JSONObject();
                    taskVariables.putAll(flowApiService.getTaskVariables(task.getId()));
                    comment.setApprovalType(FlowApprovalType.EMPTY_USER_AUTO_COMPLETE);
                    comment.setTaskComment("空审批人自动跳过审批");
                    flowApiService.completeTask(task, comment, taskVariables, null);
                }
                break;
            default:
                break;
        }
    }
}
