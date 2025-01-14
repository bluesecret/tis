package io.wangk.peekaboo.common.flow.cmd;

import cn.hutool.core.util.StrUtil;
import io.wangk.peekaboo.common.flow.constant.FlowConstant;
import lombok.AllArgsConstructor;
import org.flowable.bpmn.model.Activity;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.MultiInstanceLoopCharacteristics;
import org.flowable.common.engine.api.FlowableException;
import org.flowable.common.engine.impl.interceptor.Command;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.persistence.entity.ExecutionEntityManager;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.engine.impl.util.ProcessDefinitionUtil;
import org.flowable.engine.impl.util.TaskHelper;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.flowable.task.service.impl.persistence.entity.TaskEntityManager;

import java.util.List;

/**
 * 串行会签任务前后加签的命令对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@AllArgsConstructor
public class AddSequenceMultiInstanceCmd implements Command<List<String>> {

    /**
     * 现有的Assignee列表，并逗号分隔。
     */
    private String originalAssignees;
    /**
     * 当前任务编号。
     */
    private String taskId;
    /**
     * 被加签人。
     */
    private List<String> newAssignees;
    /**
     * 是否为前加签。
     */
    private boolean before;

    @Override
    public List<String> execute(CommandContext commandContext) {
        ProcessEngineConfigurationImpl processEngineConfiguration =
                CommandContextUtil.getProcessEngineConfiguration(commandContext);
        TaskEntityManager taskEntityManager =
                processEngineConfiguration.getTaskServiceConfiguration().getTaskEntityManager();
        ExecutionEntityManager executionEntityManager = processEngineConfiguration.getExecutionEntityManager();
        //根据任务id获取任务实例
        TaskEntity taskEntity = taskEntityManager.findById(taskId);
        //根据执行实例ID获取当前执行实例
        ExecutionEntity currentExecutionEntity = executionEntityManager.findById(taskEntity.getExecutionId());
        //获取多实例的根执行实例
        ExecutionEntity multiExecutionEntity = searchForMultiInstanceActivity(taskEntity, executionEntityManager);
        //判断当前执行实例的节点是否是多实例节点
        BpmnModel bpmnModel = ProcessDefinitionUtil.getBpmnModel(multiExecutionEntity.getProcessDefinitionId());
        Activity miActivityElement = (Activity) bpmnModel.getFlowElement(multiExecutionEntity.getCurrentActivityId());
        MultiInstanceLoopCharacteristics loopCharacteristics = miActivityElement.getLoopCharacteristics();
        if (loopCharacteristics == null) {
            throw new FlowableException("此节点不是多实例节点");
        }
        //判断是否是串行行多实例
        if (!loopCharacteristics.isSequential()) {
            throw new FlowableException("此节点为串行节点");
        }
        RuntimeService runtimeService = processEngineConfiguration.getRuntimeService();
        //获取多实例用户的变量
        List<String> collectionUsers = StrUtil.split(this.originalAssignees, ",");
        //获取当前任务在多实例中的次序
        Integer loopCounter = currentExecutionEntity.getVariableLocal("loopCounter", Integer.class);
        //操作多实例用户的变量
        if (before) {
            collectionUsers.addAll(loopCounter, this.newAssignees);
            runtimeService.setVariable(taskEntity.getProcessInstanceId(), FlowConstant.MULTI_ASSIGNEE_LIST_VAR, collectionUsers);
            //更新任务办理人
            TaskHelper.changeTaskAssignee(taskEntity, newAssignees.get(0));
        } else {
            collectionUsers.addAll(loopCounter + 1, this.newAssignees);
            runtimeService.setVariable(taskEntity.getProcessInstanceId(), FlowConstant.MULTI_ASSIGNEE_LIST_VAR, collectionUsers);
        }
        //增加多实例实例数内置变量
        Integer nrOfInstances = (Integer) multiExecutionEntity.getVariableLocal(FlowConstant.NUMBER_OF_INSTANCES_VAR);
        multiExecutionEntity.setVariableLocal(FlowConstant.NUMBER_OF_INSTANCES_VAR, nrOfInstances + newAssignees.size());
        return collectionUsers;
    }

    protected ExecutionEntity searchForMultiInstanceActivity(
            TaskEntity taskEntity, ExecutionEntityManager executionEntityManager) {
        ExecutionEntity miExecution = null;
        ExecutionEntity taskExecution =
                executionEntityManager.findById(taskEntity.getExecutionId());
        ExecutionEntity parentExecution =
                executionEntityManager.findById(taskExecution.getParentId());
        if (taskEntity.getTaskDefinitionKey().equals(parentExecution.getActivityId()) && parentExecution.isMultiInstanceRoot()) {
            miExecution = parentExecution;
        }
        if (miExecution == null) {
            throw new FlowableException("Multiple multi instance executions found for activity id " + taskEntity.getTaskDefinitionKey());
        }
        return miExecution;
    }
}
