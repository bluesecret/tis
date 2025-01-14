package io.wangk.peekaboo.common.flow.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.wangk.peekaboo.common.flow.constant.FlowAutoActionType;
import io.wangk.peekaboo.common.flow.constant.FlowConstant;
import io.wangk.peekaboo.common.flow.model.FlowTransProducer;
import io.wangk.peekaboo.common.flow.object.AutoTaskConfig;
import io.wangk.peekaboo.common.flow.service.FlowTransProducerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 基于RocketMQ进行业务流数据处理的消息消费者类，
 *
 * @author wangk
 * @date 2025-01-14
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = "${common-flow.autoFlowConsumerTopic}",
        consumeMode = ConsumeMode.ORDERLY,
        consumerGroup = "${common-flow.autoFlowConsumerGroup}")
@ConditionalOnProperty(prefix = "common-flow", name = "enabledAutoFlowConsumer")
public class AutoFlowMqConsumer implements RocketMQListener<MessageExt> {

    @Autowired
    private FlowTransProducerService flowTransProducerService;
    @Autowired
    private AutoFlowHelper autoFlowHelper;

    @Override
    public void onMessage(MessageExt message) {
        String transId = message.getProperty(RocketMQHeaders.TRANSACTION_ID);
        FlowTransProducer transProducer = flowTransProducerService.getById(transId);
        if (transProducer == null) {
            log.info("TransId [{}] doesn't exist in table zz_flow_trans_producer", transId);
            return;
        }
        AutoTaskConfig taskConfig = JSON.parseObject(transProducer.getAutoTaskConfig(), AutoTaskConfig.class);
        taskConfig.setActionType(FlowAutoActionType.CONSUME_MQ);
        transProducer.setAutoTaskConfig(JSON.toJSONString(taskConfig));
        Map<String, Object> variableMap = new HashMap<>(1);
        if (message.getBody() != null && message.getBody().length > 0) {
            try {
                JSONObject messageJson = JSON.parseObject(new String(message.getBody()));
                variableMap.put(FlowConstant.AUTO_FLOW_CONSUME_MESSAGE_VAR, messageJson);
            } catch (Exception e) {
                log.error("Failed to parse message body.", e);
            }
        }
        autoFlowHelper.triggerDirectly(transProducer, variableMap);
    }
}
