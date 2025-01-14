package io.wangk.peekaboo.common.flow.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import com.alibaba.fastjson.JSONObject;
import io.wangk.peekaboo.common.core.annotation.MyDataSourceResolver;
import io.wangk.peekaboo.common.core.constant.ApplicationConstant;
import io.wangk.peekaboo.common.core.object.CallResult;
import io.wangk.peekaboo.common.core.util.DefaultDataSourceResolver;
import io.wangk.peekaboo.common.flow.constant.FlowApprovalType;
import io.wangk.peekaboo.common.flow.constant.FlowConstant;
import io.wangk.peekaboo.common.flow.constant.FlowTaskStatus;
import io.wangk.peekaboo.common.flow.exception.FlowOperationException;
import io.wangk.peekaboo.common.flow.model.FlowTaskComment;
import io.wangk.peekaboo.common.flow.model.FlowWorkOrder;
import io.wangk.peekaboo.common.flow.model.FlowWorkOrderExt;
import io.wangk.peekaboo.common.flow.service.FlowApiOperationService;
import io.wangk.peekaboo.common.flow.service.FlowApiService;
import io.wangk.peekaboo.common.flow.service.FlowApiRequestLogService;
import io.wangk.peekaboo.common.flow.service.FlowWorkOrderService;
import io.wangk.peekaboo.common.flow.vo.FlowWorkOrderVo;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@MyDataSourceResolver(
        resolver = DefaultDataSourceResolver.class,
        intArg = ApplicationConstant.COMMON_FLOW_AND_ONLINE_DATASOURCE_TYPE)
@Service("flowApiOperationService")
public class FlowApiOperationServiceImpl implements FlowApiOperationService {

    @Autowired
    private FlowApiService flowApiService;
    @Autowired
    private FlowWorkOrderService flowWorkOrderService;
    @Autowired
    private FlowApiRequestLogService flowApiRequestLogService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public FlowWorkOrder startWithBusinessKey(String processDefinitionId, String dataId, JSONObject variables) {
        if (flowApiRequestLogService.exists()) {
            return this.getFlowWorkOrder(processDefinitionId, dataId);
        }
        ProcessInstance instance = flowApiService.start(processDefinitionId, dataId, variables);
        FlowWorkOrder flowWorkOrder = flowWorkOrderService.saveNew(instance, dataId, null, null);
        flowApiRequestLogService.saveNewFromProcessInstance(instance);
        return flowWorkOrder;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public FlowWorkOrder startAndTakeFirst(
            String processDefinitionId, String dataId, FlowTaskComment comment, JSONObject variables) {
        if (flowApiRequestLogService.exists()) {
            return this.getFlowWorkOrder(processDefinitionId, dataId);
        }
        ProcessInstance instance = flowApiService.start(processDefinitionId, dataId);
        FlowWorkOrder flowWorkOrder = flowWorkOrderService.saveNew(instance, dataId, null, null);
        flowApiService.takeFirstTask(instance.getProcessInstanceId(), comment, variables);
        // 这里需要在创建工单后再次更新一下工单状态，在flowApiService.completeTask中的更新，
        // 因为当时没有创建工单对象，更新会不起任何作用，所以这里要补偿一下。
        Integer approvalStatus = MapUtil.getInt(variables, FlowConstant.LATEST_APPROVAL_STATUS_KEY);
        flowWorkOrderService.updateLatestApprovalStatusByProcessInstanceId(instance.getId(), approvalStatus);
        flowApiRequestLogService.saveNewFromProcessInstance(instance);
        return flowWorkOrder;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public FlowWorkOrder startAndSaveNewDraft(
            String processDefinitionId, String dataId, String masterData, String slaveData) {
        if (flowApiRequestLogService.exists()) {
            return this.getFlowWorkOrder(processDefinitionId, dataId);
        }
        ProcessInstance instance = flowApiService.start(processDefinitionId, dataId);
        Map<String, Object> variableMap = flowApiService.initAndGetProcessInstanceVariables(processDefinitionId);
        flowApiService.setProcessInstanceVariables(instance.getProcessInstanceId(), variableMap);
        flowApiRequestLogService.saveNewFromProcessInstance(instance);
        return flowWorkOrderService.saveNewWithDraft(instance, null, null, masterData, slaveData);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public FlowWorkOrder takeFirstTask(
            ProcessInstance instance, Task task, FlowTaskComment comment, JSONObject variables) {
        if (flowApiRequestLogService.exists()) {
            return this.getFlowWorkOrder(instance.getProcessDefinitionId(), instance.getBusinessKey());
        }
        String dataId = instance.getBusinessKey();
        flowApiService.setBusinessKeyForProcessInstance(instance.getProcessInstanceId(), dataId);
        FlowWorkOrder flowWorkOrder =
                flowWorkOrderService.getFlowWorkOrderByProcessInstanceId(instance.getProcessInstanceId());
        flowApiService.completeTask(task, comment, variables, null);
        if (flowWorkOrder == null) {
            flowWorkOrderService.saveNew(instance, dataId, null, null);
        } else {
            flowWorkOrder.setBusinessKey(dataId);
            flowWorkOrder.setUpdateTime(new Date());
            flowWorkOrder.setFlowStatus(FlowTaskStatus.SUBMITTED);
            flowWorkOrderService.updateById(flowWorkOrder);
        }
        flowApiRequestLogService.saveNewFromProcessInstanceAndTask(instance, task);
        return flowWorkOrder;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public FlowWorkOrder takeTask(ProcessInstance instance, Task task, FlowTaskComment comment, JSONObject variables) {
        if (flowApiRequestLogService.exists()) {
            return this.getFlowWorkOrder(instance.getProcessDefinitionId(), instance.getBusinessKey());
        }
        int flowStatus = FlowTaskStatus.APPROVING;
        if (comment.getApprovalType().equals(FlowApprovalType.REFUSE)) {
            flowStatus = FlowTaskStatus.REFUSED;
        } else if (comment.getApprovalType().equals(FlowApprovalType.STOP)) {
            flowStatus = FlowTaskStatus.FINISHED;
        }
        flowWorkOrderService.updateFlowStatusByProcessInstanceId(task.getProcessInstanceId(), flowStatus);
        if (comment.getApprovalType().equals(FlowApprovalType.STOP)) {
            Integer s = MapUtil.getInt(variables, FlowConstant.LATEST_APPROVAL_STATUS_KEY);
            flowWorkOrderService.updateLatestApprovalStatusByProcessInstanceId(task.getProcessInstanceId(), s);
            CallResult stopResult = flowApiService.stopProcessInstance(
                    task.getProcessInstanceId(), comment.getTaskComment(), flowStatus);
            if (!stopResult.isSuccess()) {
                throw new FlowOperationException(stopResult.getErrorMessage());
            }
        } else {
            flowApiService.completeTask(task, comment, variables, null);
        }
        flowApiRequestLogService.saveNewFromProcessInstanceAndTask(instance, task);
        return flowWorkOrderService.getFlowWorkOrderByProcessInstanceId(instance.getProcessInstanceId());
    }

    @Override
    public void buildDraftData(List<FlowWorkOrderVo> draftWorkOrderList) {
        if (CollUtil.isEmpty(draftWorkOrderList)) {
            return;
        }
        Set<Long> workOrderIdSet = draftWorkOrderList.stream()
                .map(FlowWorkOrderVo::getWorkOrderId).collect(Collectors.toSet());
        Map<Long, FlowWorkOrderExt> workOrderExtMap =
                flowWorkOrderService.getFlowWorkOrderExtByWorkOrderIds(workOrderIdSet)
                        .stream().collect(Collectors.toMap(FlowWorkOrderExt::getWorkOrderId, c -> c));
        for (FlowWorkOrderVo workOrder : draftWorkOrderList) {
            FlowWorkOrderExt workOrderExt = workOrderExtMap.get(workOrder.getWorkOrderId());
            if (workOrderExt != null) {
                workOrder.setDraftData(workOrderExt.getDraftData());
            }
        }
    }

    private FlowWorkOrder getFlowWorkOrder(String processDefinitionId, String dataId) {
        ProcessInstance instance = flowApiService.getProcessInstanceByBusinessKey(processDefinitionId, dataId);
        return flowWorkOrderService.getFlowWorkOrderByProcessInstanceId(instance.getProcessInstanceId());
    }
}
