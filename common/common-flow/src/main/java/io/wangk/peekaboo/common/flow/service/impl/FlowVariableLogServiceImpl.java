package io.wangk.peekaboo.common.flow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.wangk.peekaboo.common.core.annotation.MyDataSourceResolver;
import io.wangk.peekaboo.common.core.base.dao.BaseDaoMapper;
import io.wangk.peekaboo.common.core.base.service.BaseService;
import io.wangk.peekaboo.common.core.constant.ApplicationConstant;
import io.wangk.peekaboo.common.core.util.DefaultDataSourceResolver;
import io.wangk.peekaboo.common.flow.dao.FlowVariableLogMapper;
import io.wangk.peekaboo.common.flow.model.FlowVariableLog;
import io.wangk.peekaboo.common.flow.service.FlowVariableLogService;
import io.wangk.peekaboo.common.sequence.wrapper.IdGeneratorWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@MyDataSourceResolver(
        resolver = DefaultDataSourceResolver.class,
        intArg = ApplicationConstant.COMMON_FLOW_AND_ONLINE_DATASOURCE_TYPE)
@Service("flowVariableLogService")
public class FlowVariableLogServiceImpl extends BaseService<FlowVariableLog, Long> implements FlowVariableLogService {

    @Autowired
    private FlowVariableLogMapper flowVariableLogMapper;
    @Autowired
    private IdGeneratorWrapper idGenerator;

    @Override
    protected BaseDaoMapper<FlowVariableLog> mapper() {
        return flowVariableLogMapper;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveNew(FlowVariableLog o) {
        o.setId(idGenerator.nextLongId());
        o.setCreateTime(new Date());
        flowVariableLogMapper.insert(o);
    }

    @Override
    public Map<String, String> getVariableMap(String processInstanceId, List<String> taskKeys) {
        LambdaQueryWrapper<FlowVariableLog> qw = new LambdaQueryWrapper<>();
        qw.eq(FlowVariableLog::getProcessInstanceId, processInstanceId);
        qw.in(FlowVariableLog::getTaskKey, taskKeys);
        qw.orderByDesc(FlowVariableLog::getId);
        List<FlowVariableLog> dataList = flowVariableLogMapper.selectList(qw);
        return dataList.stream().collect(Collectors.toMap(
                FlowVariableLog::getTaskKey, FlowVariableLog::getVariableData, (existOne, newOne) -> existOne));
    }
}
