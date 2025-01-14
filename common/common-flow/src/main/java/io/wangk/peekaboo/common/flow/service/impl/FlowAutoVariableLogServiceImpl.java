package io.wangk.peekaboo.common.flow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.wangk.peekaboo.common.core.annotation.MyDataSourceResolver;
import io.wangk.peekaboo.common.core.base.dao.BaseDaoMapper;
import io.wangk.peekaboo.common.core.base.service.BaseService;
import io.wangk.peekaboo.common.core.constant.ApplicationConstant;
import io.wangk.peekaboo.common.core.util.DefaultDataSourceResolver;
import io.wangk.peekaboo.common.core.util.MyCommonUtil;
import io.wangk.peekaboo.common.flow.dao.FlowAutoVariableLogMapper;
import io.wangk.peekaboo.common.flow.model.FlowAutoVariableLog;
import io.wangk.peekaboo.common.flow.service.FlowAutoVariableLogService;
import io.wangk.peekaboo.common.sequence.wrapper.IdGeneratorWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Slf4j
@MyDataSourceResolver(
        resolver = DefaultDataSourceResolver.class,
        intArg = ApplicationConstant.COMMON_FLOW_AND_ONLINE_DATASOURCE_TYPE)
@Service("flowAutoVariableLogService")
public class FlowAutoVariableLogServiceImpl extends BaseService<FlowAutoVariableLog, Long> implements FlowAutoVariableLogService {

    @Autowired
    private FlowAutoVariableLogMapper flowAutoVariableLogMapper;
    @Autowired
    private IdGeneratorWrapper idGenerator;

    @Override
    protected BaseDaoMapper<FlowAutoVariableLog> mapper() {
        return flowAutoVariableLogMapper;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveNew(FlowAutoVariableLog o) {
        o.setId(idGenerator.nextLongId());
        o.setTraceId(MyCommonUtil.getTraceId());
        o.setCreateTime(new Date());
        flowAutoVariableLogMapper.insert(o);
    }

    @Override
    public FlowAutoVariableLog getAutoVariableByProcessInstanceId(String processInstanceId) {
        LambdaQueryWrapper<FlowAutoVariableLog> qw = new LambdaQueryWrapper<>();
        qw.eq(FlowAutoVariableLog::getProcessInstanceId, processInstanceId);
        return flowAutoVariableLogMapper.selectOne(qw);
    }
    
    @Override
    public void deleteByProcessInstanceId(String processInstanceId) {
        LambdaQueryWrapper<FlowAutoVariableLog> qw = new LambdaQueryWrapper<>();
        qw.eq(FlowAutoVariableLog::getProcessInstanceId, processInstanceId);
        flowAutoVariableLogMapper.delete(qw);
    }
}
