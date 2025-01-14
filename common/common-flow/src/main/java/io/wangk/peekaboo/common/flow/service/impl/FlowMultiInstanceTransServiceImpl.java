package io.wangk.peekaboo.common.flow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.wangk.peekaboo.common.core.annotation.MyDataSourceResolver;
import io.wangk.peekaboo.common.core.base.dao.BaseDaoMapper;
import io.wangk.peekaboo.common.core.base.service.BaseService;
import io.wangk.peekaboo.common.core.constant.ApplicationConstant;
import io.wangk.peekaboo.common.core.object.TokenData;
import io.wangk.peekaboo.common.core.util.DefaultDataSourceResolver;
import io.wangk.peekaboo.common.flow.dao.FlowMultiInstanceTransMapper;
import io.wangk.peekaboo.common.flow.model.FlowMultiInstanceTrans;
import io.wangk.peekaboo.common.flow.service.FlowMultiInstanceTransService;
import io.wangk.peekaboo.common.sequence.wrapper.IdGeneratorWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 会签任务操作流水数据操作服务接口。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Slf4j
@MyDataSourceResolver(
        resolver = DefaultDataSourceResolver.class,
        intArg = ApplicationConstant.COMMON_FLOW_AND_ONLINE_DATASOURCE_TYPE)
@Service("flowMultiInstanceTransService")
public class FlowMultiInstanceTransServiceImpl
        extends BaseService<FlowMultiInstanceTrans, Long> implements FlowMultiInstanceTransService {

    @Autowired
    private FlowMultiInstanceTransMapper flowMultiInstanceTransMapper;
    @Autowired
    private IdGeneratorWrapper idGenerator;

    /**
     * 返回当前Service的主表Mapper对象。
     *
     * @return 主表Mapper对象。
     */
    @Override
    protected BaseDaoMapper<FlowMultiInstanceTrans> mapper() {
        return flowMultiInstanceTransMapper;
    }

    /**
     * 保存新增对象。
     *
     * @param flowMultiInstanceTrans 新增对象。
     * @return 返回新增对象。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public FlowMultiInstanceTrans saveNew(FlowMultiInstanceTrans flowMultiInstanceTrans) {
        flowMultiInstanceTrans.setId(idGenerator.nextLongId());
        TokenData tokenData = TokenData.takeFromRequest();
        flowMultiInstanceTrans.setCreateUserId(tokenData.getUserId());
        flowMultiInstanceTrans.setCreateLoginName(tokenData.getLoginName());
        flowMultiInstanceTrans.setCreateUsername(tokenData.getShowName());
        flowMultiInstanceTrans.setCreateTime(new Date());
        flowMultiInstanceTransMapper.insert(flowMultiInstanceTrans);
        return flowMultiInstanceTrans;
    }

    @Override
    public FlowMultiInstanceTrans getByExecutionId(String executionId, String taskId) {
        LambdaQueryWrapper<FlowMultiInstanceTrans> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FlowMultiInstanceTrans::getExecutionId, executionId);
        queryWrapper.eq(FlowMultiInstanceTrans::getTaskId, taskId);
        return flowMultiInstanceTransMapper.selectOne(queryWrapper);
    }

    @Override
    public FlowMultiInstanceTrans getWithAssigneeListByMultiInstanceExecId(String multiInstanceExecId) {
        if (multiInstanceExecId == null) {
            return null;
        }
        LambdaQueryWrapper<FlowMultiInstanceTrans> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FlowMultiInstanceTrans::getMultiInstanceExecId, multiInstanceExecId);
        queryWrapper.isNotNull(FlowMultiInstanceTrans::getAssigneeList);
        return flowMultiInstanceTransMapper.selectOne(queryWrapper);
    }
}
