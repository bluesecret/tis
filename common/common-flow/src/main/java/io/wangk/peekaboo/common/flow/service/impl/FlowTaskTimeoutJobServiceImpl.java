package io.wangk.peekaboo.common.flow.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.wangk.peekaboo.common.core.annotation.MyDataSourceResolver;
import io.wangk.peekaboo.common.core.base.dao.BaseDaoMapper;
import io.wangk.peekaboo.common.core.base.service.BaseService;
import io.wangk.peekaboo.common.core.constant.ApplicationConstant;
import io.wangk.peekaboo.common.core.util.DefaultDataSourceResolver;
import io.wangk.peekaboo.common.flow.constant.FlowApprovalType;
import io.wangk.peekaboo.common.flow.dao.FlowTaskTimeoutJobMapper;
import io.wangk.peekaboo.common.flow.model.FlowTaskComment;
import io.wangk.peekaboo.common.flow.model.FlowTaskTimeoutJob;
import io.wangk.peekaboo.common.flow.model.FlowWorkOrder;
import io.wangk.peekaboo.common.flow.model.constant.FlowTaskTimeoutJobStatus;
import io.wangk.peekaboo.common.flow.object.FlowApiOption;
import io.wangk.peekaboo.common.flow.object.FlowUserTaskExtData;
import io.wangk.peekaboo.common.flow.service.FlowApiService;
import io.wangk.peekaboo.common.flow.service.FlowMessageService;
import io.wangk.peekaboo.common.flow.service.FlowTaskTimeoutJobService;
import io.wangk.peekaboo.common.flow.service.FlowWorkOrderService;
import io.wangk.peekaboo.common.flow.util.BaseFlowIdentityExtHelper;
import io.wangk.peekaboo.common.flow.util.FlowCustomExtFactory;
import io.wangk.peekaboo.common.flow.vo.FlowUserInfoVo;
import io.wangk.peekaboo.common.sequence.wrapper.IdGeneratorWrapper;
import lombok.extern.slf4j.Slf4j;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Slf4j
@MyDataSourceResolver(
        resolver = DefaultDataSourceResolver.class,
        intArg = ApplicationConstant.COMMON_FLOW_AND_ONLINE_DATASOURCE_TYPE)
@Service("flowTaskTimeoutJobService")
public class FlowTaskTimeoutJobServiceImpl extends BaseService<FlowTaskTimeoutJob, Long> implements FlowTaskTimeoutJobService {

    @Autowired
    private FlowTaskTimeoutJobMapper flowTaskTimeoutJobMapper;
    @Autowired
    private FlowWorkOrderService flowWorkOrderService;
    @Autowired
    private FlowApiService flowApiService;
    @Autowired
    private FlowMessageService flowMessageService;
    @Autowired
    private FlowCustomExtFactory flowCustomExtFactory;
    @Autowired
    private IdGeneratorWrapper idGenerator;

    @Override
    protected BaseDaoMapper<FlowTaskTimeoutJob> mapper() {
        return flowTaskTimeoutJobMapper;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveNew(FlowTaskTimeoutJob job) {
        Date now = new Date();
        job.setId(idGenerator.nextLongId());
        job.setCreateTime(now);
        job.setUpdateTime(now);
        job.setExecTime(DateUtil.offsetHour(now, job.getTimeoutHours()));
        job.setStatus(FlowTaskTimeoutJobStatus.NO_EXEC);
        flowTaskTimeoutJobMapper.insert(job);
    }

    @Override
    public void executeJob(FlowTaskTimeoutJob job, Task task) {
        try {
            if (job.getHandleWay().equals(FlowUserTaskExtData.TIMEOUT_SEND_MSG)) {
                FlowWorkOrder workOrder =
                        flowWorkOrderService.getFlowWorkOrderByProcessInstanceId(task.getProcessInstanceId());
                if (workOrder != null) {
                    flowMessageService.saveNewRemindMessage(workOrder);
                }
            } else {
                FlowTaskComment comment = new FlowTaskComment();
                comment.setProcessInstanceId(task.getProcessInstanceId());
                comment.setExecutionId(task.getExecutionId());
                comment.setTaskKey(task.getTaskDefinitionKey());
                comment.setTaskName(task.getName());
                comment.setTaskId(task.getId());
                comment.setApprovalType(FlowApprovalType.TIMEOUT_AUTO_COMPLETE);
                comment.setTaskComment("任务超时自动审批");
                comment.setCreateLoginName(job.getDefaultAssignee());
                BaseFlowIdentityExtHelper flowIdentityExtHelper = flowCustomExtFactory.getFlowIdentityExtHelper();
                List<FlowUserInfoVo> userInfoList =
                        flowIdentityExtHelper.getUserInfoListByUsernameSet(CollUtil.newHashSet(job.getDefaultAssignee()));
                if (CollUtil.isNotEmpty(userInfoList)) {
                    FlowUserInfoVo userInfo = userInfoList.get(0);
                    comment.setCreateUserId(userInfo.getUserId());
                    comment.setCreateUsername(userInfo.getShowName());
                }
                FlowApiOption option = new FlowApiOption();
                option.setDeleteTimeoutTaskJob(false);
                flowApiService.completeTask(task, comment, new JSONObject(), option);
            }
            job.setStatus(FlowTaskTimeoutJobStatus.SUCCESS);
        } catch (Exception e) {
            log.error("Failed to call FlowTaskTimeoutJobServiceImpl.executeJob", e);
            job.setStatus(FlowTaskTimeoutJobStatus.FAIL);
            job.setErrorMessage(e.getClass().getName());
        }
        job.setUpdateTime(new Date());
        flowTaskTimeoutJobMapper.updateById(job);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteByTaskId(String taskId) {
        LambdaQueryWrapper<FlowTaskTimeoutJob> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FlowTaskTimeoutJob::getStatus, FlowTaskTimeoutJobStatus.NO_EXEC);
        queryWrapper.eq(FlowTaskTimeoutJob::getTaskId, taskId);
        flowTaskTimeoutJobMapper.delete(queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteByProcessInstanceId(String processInstanceId) {
        LambdaQueryWrapper<FlowTaskTimeoutJob> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ne(FlowTaskTimeoutJob::getStatus, FlowTaskTimeoutJobStatus.FAIL);
        queryWrapper.eq(FlowTaskTimeoutJob::getProcessInstanceId, processInstanceId);
        flowTaskTimeoutJobMapper.delete(queryWrapper);
    }

    @Override
    public List<FlowTaskTimeoutJob> getExecutableList() {
        LambdaQueryWrapper<FlowTaskTimeoutJob> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FlowTaskTimeoutJob::getStatus, FlowTaskTimeoutJobStatus.NO_EXEC);
        queryWrapper.le(FlowTaskTimeoutJob::getExecTime, new Date());
        return flowTaskTimeoutJobMapper.selectList(queryWrapper);
    }
}
