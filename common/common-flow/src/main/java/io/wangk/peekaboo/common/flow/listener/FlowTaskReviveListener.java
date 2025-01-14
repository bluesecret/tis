package io.wangk.peekaboo.common.flow.listener;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import com.alibaba.fastjson.JSON;
import io.wangk.peekaboo.common.core.util.ApplicationContextHolder;
import io.wangk.peekaboo.common.flow.model.FlowEntryPublish;
import io.wangk.peekaboo.common.flow.model.FlowVariableLog;
import io.wangk.peekaboo.common.flow.object.FlowEntryExtensionData;
import io.wangk.peekaboo.common.flow.service.FlowEntryService;
import io.wangk.peekaboo.common.flow.service.FlowVariableLogService;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.task.service.delegate.DelegateTask;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 支持流程复活的任务监听器。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Slf4j
public class FlowTaskReviveListener implements TaskListener {

    private final transient FlowVariableLogService flowVariableLogService =
            ApplicationContextHolder.getBean(FlowVariableLogService.class);
    private final transient FlowEntryService flowEntryService =
            ApplicationContextHolder.getBean(FlowEntryService.class);

    @Override
    public void notify(DelegateTask delegateTask) {
        Map<String, Object> variables = delegateTask.getVariables();
        if (MapUtil.isNotEmpty(variables)) {
            List<FlowEntryPublish> flowEntryPublishList =
                    flowEntryService.getFlowEntryPublishList(CollUtil.newHashSet(delegateTask.getProcessDefinitionId()));
            FlowEntryExtensionData flowEntryExtensionData =
                    JSON.parseObject(flowEntryPublishList.get(0).getExtensionData(), FlowEntryExtensionData.class);
            FlowVariableLog o = new FlowVariableLog();
            if (flowEntryExtensionData.getKeptReviveDays() > 0) {
                o.setExpiredTime(DateUtil.offset(new Date(), DateField.DAY_OF_MONTH, flowEntryExtensionData.getKeptReviveDays()));
            }
            o.setProcessDefinitionKey(delegateTask.getProcessDefinitionId());
            o.setProcessInstanceId(delegateTask.getProcessInstanceId());
            o.setTaskKey(delegateTask.getTaskDefinitionKey());
            o.setVariableData(JSON.toJSONString(variables));
            flowVariableLogService.saveNew(o);
        }
    }
}
