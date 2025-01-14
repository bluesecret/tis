package io.wangk.peekaboo.common.flow.cmd;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.StartEvent;
import org.flowable.common.engine.api.FlowableException;
import org.flowable.common.engine.api.FlowableIllegalArgumentException;
import org.flowable.common.engine.impl.interceptor.Command;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.engine.HistoryService;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.persistence.entity.HistoricProcessInstanceEntityImpl;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.engine.impl.util.ProcessDefinitionUtil;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ChangeActivityStateBuilder;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.variable.api.history.HistoricVariableInstance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 流程复活命令对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@AllArgsConstructor
public class RestartProcessInstanceCmd implements Command<ProcessInstance> {

    /**
     * 待复活的流程实例编号
     */
    protected String processInstanceId;
    /**
     * 要复活到的节点列表
     */
    protected List<String> activityIds;
    /**
     * 流程任务标识和变量数据的Map。
     */
    protected Map<String, String> variableMap;

    @Override
    public ProcessInstance execute(CommandContext commandContext) {
        ProcessEngineConfiguration engineConfig = CommandContextUtil.getProcessEngineConfiguration(commandContext);
        HistoryService historyService = engineConfig.getHistoryService();
        //校验和查询历史流程实例
        HistoricProcessInstance historicProcessInstance = this.checkAndGetHistoricProcessInstance(historyService);
        //校验待复活节点
        this.checkActivityIds(historicProcessInstance.getProcessDefinitionId());
        //校验流程定义
        ProcessDefinition processDefinition =
                ProcessDefinitionUtil.getProcessDefinition(historicProcessInstance.getProcessDefinitionId());
        if (processDefinition == null) {
            throw new FlowableException("流程定义不存在");
        }
        //获取流程启动节点
        Process process = ProcessDefinitionUtil.getProcess(processDefinition.getId());
        StartEvent initialFlowElement = (StartEvent) process.getInitialFlowElement();
        //重建运行时流程实例的主执行实例
        ExecutionEntity processInstance =
                this.recreateProcessInstance(processDefinition, initialFlowElement, historicProcessInstance);
        //创建子执行实例
        ExecutionEntity childExecution =
                CommandContextUtil.getExecutionEntityManager().createChildExecution(processInstance);
        childExecution.setCurrentFlowElement(initialFlowElement);
        //重建运行时流程变量
        //设置历史流程实例结束节点和结束时间为空
        ((HistoricProcessInstanceEntityImpl) historicProcessInstance).setEndActivityId(null);
        ((HistoricProcessInstanceEntityImpl) historicProcessInstance).setEndTime(null);
        Map<String, Object> gloablVariables = this.collectVariables(historyService, historicProcessInstance);
        //执行动态跳转
        ChangeActivityStateBuilder builder = engineConfig.getRuntimeService()
                .createChangeActivityStateBuilder()
                .processInstanceId(processInstance.getProcessInstanceId())
                .moveSingleExecutionToActivityIds(childExecution.getId(), activityIds)
                .processVariables(gloablVariables);
        for (String activityId : activityIds) {
            String variableData = variableMap.get(activityId);
            if (StrUtil.isNotBlank(variableData)) {
                builder.localVariables(activityId, JSON.parseObject(variableData));
            }
        }
        builder.changeState();
        return engineConfig.getRuntimeService()
                .createProcessInstanceQuery().processInstanceId(processInstance.getId()).singleResult();
    }

    private HistoricProcessInstance checkAndGetHistoricProcessInstance(HistoryService historyService) {
        if (StrUtil.isBlank(processInstanceId)) {
            throw new FlowableIllegalArgumentException("processInstanceId不能为空");
        }
        HistoricProcessInstance historicProcessInstance = historyService
                .createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        if (historicProcessInstance == null) {
            throw new FlowableException("id为" + processInstanceId + "的流程实例不存在");
        } else if (historicProcessInstance.getEndTime() == null) {
            throw new FlowableException("id为" + processInstanceId + "的流程实例没有结束");
        }
        return historicProcessInstance;
    }

    private void checkActivityIds(String processDefinitionId) {
        if (CollUtil.isEmpty(activityIds)) {
            throw new FlowableIllegalArgumentException("activityIds不能为空");
        }
        BpmnModel bpmnModel = ProcessDefinitionUtil.getBpmnModel(processDefinitionId);
        List<String> notExistedFlowElements = new ArrayList<>();
        for (String activityId : activityIds) {
            if (!bpmnModel.getMainProcess().containsFlowElementId(activityId)) {
                notExistedFlowElements.add(activityId);
            }
        }
        if (!CollUtil.isEmpty(notExistedFlowElements)) {
            throw new FlowableIllegalArgumentException("Id为" + String.join("、", notExistedFlowElements) + "节点不存在");
        }
    }

    private ExecutionEntity recreateProcessInstance(
            ProcessDefinition processDefinition,
            StartEvent initialFlowElement,
            HistoricProcessInstance historicProcessInstance) {
        //创建流程实例
        ExecutionEntity executionEntity = CommandContextUtil.getExecutionEntityManager()
                .createProcessInstanceExecution(processDefinition,
                        historicProcessInstance.getId(),
                        historicProcessInstance.getBusinessKey(),
                        historicProcessInstance.getBusinessStatus(),
                        historicProcessInstance.getName(),
                        historicProcessInstance.getCallbackId(),
                        historicProcessInstance.getCallbackType(),
                        historicProcessInstance.getReferenceId(),
                        historicProcessInstance.getReferenceType(),
                        null,
                        historicProcessInstance.getTenantId(),
                        initialFlowElement.getInitiator(),
                        historicProcessInstance.getStartActivityId());
        //重设流程开始事件
        executionEntity.setStartTime(historicProcessInstance.getStartTime());
        return executionEntity;
    }

    private Map<String, Object> collectVariables(
            HistoryService historyService, HistoricProcessInstance historicProcessInstance) {
        Map<String, Object> variables = new HashMap<>(10);
        List<HistoricVariableInstance> historicVariables = historyService
                .createHistoricVariableInstanceQuery()
                .processInstanceId(historicProcessInstance.getId())
                .executionId(historicProcessInstance.getId()).list();
        for (HistoricVariableInstance historicVariable : historicVariables) {
            variables.put(historicVariable.getVariableName(), historicVariable.getValue());
        }
        return variables;
    }
}