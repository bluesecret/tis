package io.wangk.peekaboo.common.flow.listener;

import cn.hutool.core.text.StrFormatter;
import cn.hutool.core.util.BooleanUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import io.wangk.peekaboo.common.core.util.ApplicationContextHolder;
import io.wangk.peekaboo.common.flow.constant.FlowApprovalType;
import io.wangk.peekaboo.common.flow.constant.FlowAutoActionType;
import io.wangk.peekaboo.common.flow.model.FlowTaskComment;
import io.wangk.peekaboo.common.flow.model.FlowTransProducer;
import io.wangk.peekaboo.common.flow.object.AutoTaskConfig;
import io.wangk.peekaboo.common.flow.service.*;
import io.wangk.peekaboo.common.flow.util.ListenerEventPublishHelper;
import io.wangk.peekaboo.common.flow.util.AutoFlowHelper;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;

/**
 * 自动化接收任务开始监听器。
 *
* @author wangk
* @date 2025-01-14
 */
@Slf4j
public class ReceiveTaskStartListener implements ExecutionListener {

    private final transient AutoFlowHelper autoFlowHelper =
            ApplicationContextHolder.getBean(AutoFlowHelper.class);
    private final transient FlowTransProducerService flowTransProducerService =
            ApplicationContextHolder.getBean(FlowTransProducerService.class);
    private final transient FlowTaskCommentService flowTaskCommentService =
            ApplicationContextHolder.getBean(FlowTaskCommentService.class);
    private final transient ListenerEventPublishHelper eventPublishHelper =
            ApplicationContextHolder.getBean(ListenerEventPublishHelper.class);

    @Override
    public void notify(DelegateExecution d) {
        //TODO 在目标表所在数据库也要创建业务执行的流水表，基于TransProduer的TransId做唯一性校验。
        AutoTaskConfig taskConfig = autoFlowHelper.parseAutoTaskConfig(d.getProcessDefinitionId(), d.getCurrentActivityId());
        FlowTransProducer producerData = new FlowTransProducer();
        producerData.setProcessInstanceId(d.getProcessInstanceId());
        producerData.setExecutionId(d.getId());
        producerData.setTaskKey(d.getCurrentActivityId());
        producerData.setDblinkId(taskConfig.getDestDblinkId());
        producerData.setInitMethod("ReceiveTaskStartListener.notify");
        producerData.setTryTimes(1);
        producerData.setAutoTaskConfig(JSON.toJSONString(taskConfig, SerializerFeature.WriteDateUseDateFormat));
        flowTransProducerService.saveNew(producerData);
        if (BooleanUtil.isFalse(taskConfig.getAutoTrigger())) {
            FlowTaskComment comment = new FlowTaskComment();
            comment.setTaskKey(d.getCurrentActivityId());
            comment.setTaskName(taskConfig.getTaskName());
            comment.setProcessInstanceId(d.getProcessInstanceId());
            comment.setExecutionId(d.getId());
            comment.setApprovalType(FlowApprovalType.AUTO_FLOW_TASK);
            String s = StrFormatter.format("执行任务 [{}] 成功开始，任务类型 [{}]",
                    taskConfig.getTaskName(), FlowAutoActionType.getShowNname(taskConfig.getActionType()));
            comment.setTaskComment(s);
            flowTaskCommentService.saveNew(comment);
        }
        eventPublishHelper.publishEvent(producerData);
    }
}
