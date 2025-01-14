package io.wangk.peekaboo.common.flow.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.wangk.peekaboo.common.core.annotation.MyDataSourceResolver;
import io.wangk.peekaboo.common.core.base.dao.BaseDaoMapper;
import io.wangk.peekaboo.common.core.base.service.BaseService;
import io.wangk.peekaboo.common.core.constant.ApplicationConstant;
import io.wangk.peekaboo.common.core.object.TokenData;
import io.wangk.peekaboo.common.core.util.DefaultDataSourceResolver;
import io.wangk.peekaboo.common.core.util.MyCommonUtil;
import io.wangk.peekaboo.common.flow.dao.FlowTransProducerMapper;
import io.wangk.peekaboo.common.flow.model.FlowTransProducer;
import io.wangk.peekaboo.common.flow.service.FlowTransProducerService;
import io.wangk.peekaboo.common.sequence.wrapper.IdGeneratorWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Slf4j
@MyDataSourceResolver(
        resolver = DefaultDataSourceResolver.class,
        intArg = ApplicationConstant.COMMON_FLOW_AND_ONLINE_DATASOURCE_TYPE)
@Service("flowTransProducerService")
public class FlowTransProducerServiceImpl extends BaseService<FlowTransProducer, Long> implements FlowTransProducerService {

    @Autowired
    private FlowTransProducerMapper flowTransProducerMapper;
    @Autowired
    private IdGeneratorWrapper idGenerator;

    @Override
    protected BaseDaoMapper<FlowTransProducer> mapper() {
        return flowTransProducerMapper;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public FlowTransProducer saveNew(FlowTransProducer data) {
        if (data.getTransId() == null) {
            data.setTransId(idGenerator.nextLongId());
        }
        TokenData tokenData = TokenData.takeFromRequest();
        if (tokenData != null) {
            data.setAppCode(tokenData.getAppCode());
            data.setCreateLoginName(tokenData.getLoginName());
            data.setCreateUsername(tokenData.getShowName());
        }
        data.setCreateTime(new Date());
        data.setTraceId(MyCommonUtil.getTraceId());
        flowTransProducerMapper.insert(data);
        return data;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateById(FlowTransProducer data) {
        return mapper().updateById(data) != 0;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean  removeById(Serializable transId) {
        return mapper().deleteById(transId) != 0;
    }

    @Override
    public List<FlowTransProducer> getListByProcessInstanceIds(Set<String> processInstanceIds) {
        LambdaQueryWrapper<FlowTransProducer> qw = new LambdaQueryWrapper<>();
        qw.in(FlowTransProducer::getProcessInstanceId, processInstanceIds);
        qw.orderByDesc(FlowTransProducer::getTransId);
        return flowTransProducerMapper.selectList(qw);
    }

    @Override
    public FlowTransProducer getByProcessInstanceIdAndTaskId(String processInstanceId, String taskId) {
        LambdaQueryWrapper<FlowTransProducer> qw = new LambdaQueryWrapper<>();
        qw.eq(FlowTransProducer::getProcessInstanceId, processInstanceId);
        qw.eq(FlowTransProducer::getTaskId, taskId);
        String appCode = TokenData.takeFromRequest().getAppCode();
        if (StrUtil.isBlank(appCode)) {
            qw.isNull(FlowTransProducer::getAppCode);
        } else {
            qw.eq(FlowTransProducer::getAppCode, appCode);
        }
        return flowTransProducerMapper.selectOne(qw);
    }

    @Override
    public void deleteByProcessInstanceId(String processInstanceId) {
        LambdaQueryWrapper<FlowTransProducer> qw = new LambdaQueryWrapper<>();
        qw.eq(FlowTransProducer::getProcessInstanceId, processInstanceId);
        String appCode = TokenData.takeFromRequest().getAppCode();
        if (StrUtil.isBlank(appCode)) {
            qw.isNull(FlowTransProducer::getAppCode);
        } else {
            qw.eq(FlowTransProducer::getAppCode, appCode);
        }
        flowTransProducerMapper.delete(qw);
    }
}
