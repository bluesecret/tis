package io.wangk.peekaboo.common.flow.listener;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.StrFormatter;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import io.wangk.peekaboo.common.core.util.ApplicationContextHolder;
import io.wangk.peekaboo.common.flow.constant.FlowApprovalType;
import io.wangk.peekaboo.common.flow.constant.FlowAutoActionType;
import io.wangk.peekaboo.common.flow.constant.FlowConstant;
import io.wangk.peekaboo.common.flow.model.FlowTaskComment;
import io.wangk.peekaboo.common.flow.model.FlowTransProducer;
import io.wangk.peekaboo.common.flow.object.AutoTaskConfig;
import io.wangk.peekaboo.common.flow.service.FlowApiService;
import io.wangk.peekaboo.common.flow.service.FlowTaskCommentService;
import io.wangk.peekaboo.common.flow.service.FlowTransProducerService;
import io.wangk.peekaboo.common.flow.util.AutoFlowHelper;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 自动化接收任务结束监听器。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Slf4j
public class ReceiveTaskEndListener implements ExecutionListener {

    private final transient FlowApiService flowApiService =
            ApplicationContextHolder.getBean(FlowApiService.class);
    private final transient FlowTransProducerService flowTransProducerService =
            ApplicationContextHolder.getBean(FlowTransProducerService.class);
    private final transient FlowTaskCommentService flowTaskCommentService =
            ApplicationContextHolder.getBean(FlowTaskCommentService.class);
    private final transient AutoFlowHelper autoFlowHelper =
            ApplicationContextHolder.getBean(AutoFlowHelper.class);

    @Override
    public void notify(DelegateExecution d) {
        //先从内存中的临时变量中获取，以便提升正常运行情况下的运行时效率。
        FlowTransProducer transProducer =
                (FlowTransProducer) d.getTransientVariable(FlowConstant.AUTO_FLOW_TRANS_PRODUCER_VAR);
        //如果临时变量中没有存在，则从流程执行实例中查询该变量。
        if (transProducer == null) {
            transProducer = (FlowTransProducer)
                    flowApiService.getExecutionVariable(d.getId(), FlowConstant.AUTO_FLOW_TRANS_PRODUCER_VAR);
        }
        if (transProducer == null) {
            FlowTransProducer filter = new FlowTransProducer();
            filter.setProcessInstanceId(d.getProcessInstanceId());
            filter.setExecutionId(d.getId());
            filter.setTaskKey(d.getCurrentActivityId());
            List<FlowTransProducer> transProducers = flowTransProducerService.getListByFilter(filter);
            if (CollUtil.isNotEmpty(transProducers)) {
                transProducers = transProducers.stream()
                        .sorted(Comparator.comparing(FlowTransProducer::getTransId, Comparator.reverseOrder()))
                        .collect(Collectors.toList());
                transProducer = transProducers.get(0);
            }
        }
        if (transProducer == null) {
            return;
        }
        if (StrUtil.isNotBlank(transProducer.getAutoTaskConfig())) {
            AutoTaskConfig taskConfig = JSON.parseObject(transProducer.getAutoTaskConfig(), AutoTaskConfig.class);
            autoFlowHelper.executeTask(transProducer, taskConfig, d);
            FlowTaskComment comment = new FlowTaskComment();
            comment.setTaskKey(d.getCurrentActivityId());
            comment.setTaskName(taskConfig.getTaskName());
            comment.setProcessInstanceId(d.getProcessInstanceId());
            comment.setExecutionId(d.getId());
            comment.setApprovalType(FlowApprovalType.AUTO_FLOW_TASK);
            String s = StrFormatter.format("执行任务 [{}] 成功，任务类型 [{}]",
                    taskConfig.getTaskName(), FlowAutoActionType.getShowNname(taskConfig.getActionType()));
            comment.setTaskComment(s);
            flowTaskCommentService.saveNew(comment);
        }
        flowTransProducerService.removeById(transProducer.getTransId());
    }
}
