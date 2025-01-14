package io.wangk.peekaboo.common.flow.util;

import cn.hutool.core.util.BooleanUtil;
import com.alibaba.fastjson.JSON;
import io.wangk.peekaboo.common.core.exception.MyRuntimeException;
import io.wangk.peekaboo.common.flow.constant.FlowConstant;
import io.wangk.peekaboo.common.flow.model.FlowTransProducer;
import io.wangk.peekaboo.common.flow.object.AutoTaskConfig;
import io.wangk.peekaboo.common.flow.service.FlowTransProducerService;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.impl.persistence.entity.ExecutionEntityImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * 流程监听器发送spring事件的帮助类。
 * 注意：流程的监听器不是bean对象，不能发送和捕捉事件，因此需要借助该类完成。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Slf4j
@Component
public class ListenerEventPublishHelper {

    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private FlowTransProducerService flowTransProducerService;
    @Autowired
    private AutoFlowHelper autoFlowHelper;

    public <T> void publishEvent(T data) {
        eventPublisher.publishEvent(data);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void doHandle(FlowTransProducer producerData) {
        AutoTaskConfig taskConfig = JSON.parseObject(producerData.getAutoTaskConfig(), AutoTaskConfig.class);
        if (BooleanUtil.isTrue(taskConfig.getAutoTrigger())) {
            Map<String, Object> transVariableMap = new HashMap<>(1);
            transVariableMap.put(FlowConstant.AUTO_FLOW_TRANS_PRODUCER_VAR, producerData);
            Executors.newSingleThreadExecutor().submit(() -> this.triggerReceiveTask(producerData, transVariableMap));
        } else {
            // 如果不是自动触发，这里需要手动执行任务，而不是通过接收任务的结束监听器触发。
            ExecutionEntityImpl d = new ExecutionEntityImpl();
            d.setId(producerData.getExecutionId());
            d.setProcessInstanceId(producerData.getProcessInstanceId());
            autoFlowHelper.executeTask(producerData, taskConfig, d);
        }
    }

    private void triggerReceiveTask(FlowTransProducer producerData, Map<String, Object> transVariableMap) {
        try {
            runtimeService.trigger(producerData.getExecutionId(), null, transVariableMap);
        } catch (Exception e) {
            log.error("Failed to commit automatic business data [** " + JSON.toJSONString(producerData) + " **]", e);
            producerData.setErrorReason(e.getMessage());
            flowTransProducerService.updateById(producerData);
            throw new MyRuntimeException(e.getMessage());
        }
    }
}
