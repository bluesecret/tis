package io.wangk.peekaboo.common.flow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.wangk.peekaboo.common.core.annotation.MyDataSourceResolver;
import io.wangk.peekaboo.common.core.base.dao.BaseDaoMapper;
import io.wangk.peekaboo.common.core.base.service.BaseService;
import io.wangk.peekaboo.common.core.constant.ApplicationConstant;
import io.wangk.peekaboo.common.core.object.TokenData;
import io.wangk.peekaboo.common.core.util.ContextUtil;
import io.wangk.peekaboo.common.core.util.DefaultDataSourceResolver;
import io.wangk.peekaboo.common.flow.dao.FlowApiRequestLogMapper;
import io.wangk.peekaboo.common.flow.model.FlowApiRequestLog;
import io.wangk.peekaboo.common.flow.service.FlowApiRequestLogService;
import io.wangk.peekaboo.common.sequence.wrapper.IdGeneratorWrapper;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@MyDataSourceResolver(
        resolver = DefaultDataSourceResolver.class,
        intArg = ApplicationConstant.COMMON_FLOW_AND_ONLINE_DATASOURCE_TYPE)
@Service("flowApiRequestLogService")
public class FlowApiRequestLogServiceImpl extends BaseService<FlowApiRequestLog, Long> implements FlowApiRequestLogService {

    @Autowired
    private FlowApiRequestLogMapper flowApiRequestLogMapper;
    @Autowired
    private IdGeneratorWrapper idGenerator;

    @Override
    protected BaseDaoMapper<FlowApiRequestLog> mapper() {
        return flowApiRequestLogMapper;
    }

    @Override
    public FlowApiRequestLog saveNewFromProcessInstance(ProcessInstance instance) {
        String flowRequestId = this.getRequestId();
        FlowApiRequestLog log = null;
        if (flowRequestId != null) {
            log = this.createBy(flowRequestId, instance);
            flowApiRequestLogMapper.insert(log);
        }
        return log;
    }

    @Override
    public FlowApiRequestLog saveNewFromProcessInstanceAndTask(ProcessInstance instance, Task task) {
        String flowRequestId = this.getRequestId();
        FlowApiRequestLog log = null;
        if (flowRequestId != null) {
            log = this.createBy(flowRequestId, instance);
            log.setTaskId(task.getId());
            log.setTaskKey(task.getTaskDefinitionKey());
            flowApiRequestLogMapper.insert(log);
        }
        return log;
    }

    @Override
    public boolean exists() {
        String flowRequestId = this.getRequestId();
        if (flowRequestId == null) {
            return false;
        }
        return flowApiRequestLogMapper.exists(
                new LambdaQueryWrapper<FlowApiRequestLog>().eq(FlowApiRequestLog::getRequestId, flowRequestId));
    }

    private String getRequestId() {
        return ContextUtil.getHttpRequest().getHeader(ApplicationConstant.FLOW_REQUEST_ID);
    }

    private FlowApiRequestLog createBy(String flowRequestId, ProcessInstance instance) {
        FlowApiRequestLog o = new FlowApiRequestLog();
        TokenData tokenData = TokenData.takeFromRequest();
        if (tokenData != null) {
            o.setCreateUserId(tokenData.getUserId());
            o.setCreateTime(new Date());
        }
        o.setId(idGenerator.nextLongId());
        o.setRequestId(flowRequestId);
        o.setProcessInstanceId(instance.getProcessInstanceId());
        o.setBusinessKey(instance.getBusinessKey());
        return o;
    }
}
