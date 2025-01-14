package io.wangk.peekaboo.common.flow.listener;

import cn.hutool.core.util.StrUtil;
import io.wangk.peekaboo.common.core.object.GlobalThreadLocal;
import io.wangk.peekaboo.common.core.util.ApplicationContextHolder;
import io.wangk.peekaboo.common.flow.model.FlowEntry;
import io.wangk.peekaboo.common.flow.model.FlowWorkOrder;
import io.wangk.peekaboo.common.flow.model.constant.FlowEntryType;
import io.wangk.peekaboo.common.flow.service.*;
import io.wangk.peekaboo.common.flow.constant.FlowTaskStatus;
import io.wangk.peekaboo.common.flow.util.FlowCustomExtFactory;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;

/**
 * 流程实例监听器，在流程实例结束的时候，需要完成一些自定义的业务行为。如：
 * 1. 更新流程工单表的审批状态字段。
 * 2. 业务数据同步。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Slf4j
public class FlowFinishedListener implements ExecutionListener {

    private final transient FlowWorkOrderService flowWorkOrderService =
            ApplicationContextHolder.getBean(FlowWorkOrderService.class);
    private final transient FlowCustomExtFactory flowCustomExtFactory =
            ApplicationContextHolder.getBean(FlowCustomExtFactory.class);
    private final transient FlowTaskTimeoutJobService flowTaskTimeoutJobService =
            ApplicationContextHolder.getBean(FlowTaskTimeoutJobService.class);
    private final transient FlowAutoVariableLogService flowAutoVariableLogService =
            ApplicationContextHolder.getBean(FlowAutoVariableLogService.class);
    private final transient FlowEntryService flowEntryService =
            ApplicationContextHolder.getBean(FlowEntryService.class);

    @Override
    public void notify(DelegateExecution execution) {
        if (!StrUtil.equals("end", execution.getEventName())) {
            return;
        }
        boolean enabled = GlobalThreadLocal.setDataFilter(false);
        try {
            String processInstanceId = execution.getProcessInstanceId();
            FlowWorkOrder workOrder = flowWorkOrderService.getFlowWorkOrderByProcessInstanceId(processInstanceId);
            if (workOrder == null) {
                return;
            }
            int flowStatus = FlowTaskStatus.FINISHED;
            if (workOrder.getFlowStatus().equals(FlowTaskStatus.CANCELLED)
                    || workOrder.getFlowStatus().equals(FlowTaskStatus.STOPPED)) {
                flowStatus = workOrder.getFlowStatus();
            }
            workOrder.setFlowStatus(flowStatus);
            // 更新流程工单中的流程状态。
            flowWorkOrderService.updateFlowStatusByProcessInstanceId(processInstanceId, flowStatus);
            if (workOrder.getOnlineTableId() != null) {
                // 处理在线表单工作流的自定义状态更新。
                flowCustomExtFactory.getOnlineBusinessDataExtHelper().updateFlowStatus(workOrder);
            } else {
                // 处理路由表单工作里的自定义状态更新。
                flowCustomExtFactory.getBusinessDataExtHelper().updateFlowStatus(workOrder);
            }
            if (flowStatus == FlowTaskStatus.FINISHED) {
                // 在线表单不支持该功能，仅限于路由表单工作流可用。
                // 可以自行实现审批完成后，将审批中已经通过的数据，同步到业务发布表。
                flowCustomExtFactory.getBusinessDataExtHelper().triggerSync(workOrder);
            }
            flowTaskTimeoutJobService.deleteByProcessInstanceId(execution.getProcessInstanceId());
            FlowEntry flowEntry = flowEntryService.getFlowEntryFromCache(workOrder.getProcessDefinitionKey());
            if (flowEntry.getFlowType().equals(FlowEntryType.AUTO_TYPE)) {
                flowAutoVariableLogService.deleteByProcessInstanceId(processInstanceId);
            }
        } finally {
            GlobalThreadLocal.setDataFilter(enabled);
        }
    }
}
