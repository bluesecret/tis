package io.wangk.peekaboo.common.flow.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.page.PageMethod;
import io.wangk.peekaboo.common.core.annotation.DisableDataFilter;
import io.wangk.peekaboo.common.core.annotation.MyDataSourceResolver;
import io.wangk.peekaboo.common.core.base.dao.BaseDaoMapper;
import io.wangk.peekaboo.common.core.base.service.BaseService;
import io.wangk.peekaboo.common.core.constant.ApplicationConstant;
import io.wangk.peekaboo.common.core.constant.GlobalDeletedFlag;
import io.wangk.peekaboo.common.core.object.*;
import io.wangk.peekaboo.common.core.util.DefaultDataSourceResolver;
import io.wangk.peekaboo.common.core.util.MyPageUtil;
import io.wangk.peekaboo.common.flow.constant.FlowTaskStatus;
import io.wangk.peekaboo.common.flow.constant.FlowConstant;
import io.wangk.peekaboo.common.flow.dao.FlowWorkOrderExtMapper;
import io.wangk.peekaboo.common.flow.dao.FlowWorkOrderMapper;
import io.wangk.peekaboo.common.flow.dto.FlowWorkOrderDto;
import io.wangk.peekaboo.common.flow.model.FlowEntry;
import io.wangk.peekaboo.common.flow.model.FlowWorkOrder;
import io.wangk.peekaboo.common.flow.model.FlowWorkOrderExt;
import io.wangk.peekaboo.common.flow.util.FlowOperationHelper;
import io.wangk.peekaboo.common.flow.vo.FlowWorkOrderVo;
import io.wangk.peekaboo.common.flow.service.FlowApiService;
import io.wangk.peekaboo.common.flow.service.FlowEntryService;
import io.wangk.peekaboo.common.flow.service.FlowWorkOrderService;
import io.wangk.peekaboo.common.flow.util.BaseFlowIdentityExtHelper;
import io.wangk.peekaboo.common.flow.util.FlowCustomExtFactory;
import io.wangk.peekaboo.common.redis.util.CommonRedisUtil;
import io.wangk.peekaboo.common.sequence.wrapper.IdGeneratorWrapper;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@MyDataSourceResolver(
        resolver = DefaultDataSourceResolver.class,
        intArg = ApplicationConstant.COMMON_FLOW_AND_ONLINE_DATASOURCE_TYPE)
@Service("flowWorkOrderService")
public class FlowWorkOrderServiceImpl extends BaseService<FlowWorkOrder, Long> implements FlowWorkOrderService {

    @Autowired
    private FlowWorkOrderMapper flowWorkOrderMapper;
    @Autowired
    private FlowWorkOrderExtMapper flowWorkOrderExtMapper;
    @Autowired
    private IdGeneratorWrapper idGenerator;
    @Autowired
    private FlowCustomExtFactory flowCustomExtFactory;
    @Autowired
    private FlowApiService flowApiService;
    @Autowired
    private FlowEntryService flowEntryService;
    @Autowired
    private CommonRedisUtil commonRedisUtil;
    @Autowired
    private FlowOperationHelper flowOperationHelper;

    /**
     * 返回当前Service的主表Mapper对象。
     *
     * @return 主表Mapper对象。
     */
    @Override
    protected BaseDaoMapper<FlowWorkOrder> mapper() {
        return flowWorkOrderMapper;
    }

    /**
     * 保存新增对象。
     *
     * @param instance      流程实例对象。
     * @param dataId        流程实例的BusinessKey。
     * @param onlineTableId 在线数据表的主键Id。
     * @param tableName     面向静态表单所使用的表名。
     * @return 新增的工作流工单对象。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public FlowWorkOrder saveNew(ProcessInstance instance, Object dataId, Long onlineTableId, String tableName) {
        // 正常插入流程工单数据。
        FlowWorkOrder flowWorkOrder = this.createWith(instance);
        flowWorkOrder.setWorkOrderCode(this.generateWorkOrderCode(instance.getProcessDefinitionKey()));
        flowWorkOrder.setBusinessKey(dataId.toString());
        flowWorkOrder.setOnlineTableId(onlineTableId);
        flowWorkOrder.setTableName(tableName);
        flowWorkOrder.setFlowStatus(FlowTaskStatus.SUBMITTED);
        this.doSafeSave(flowWorkOrder, instance);
        return flowWorkOrder;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public FlowWorkOrder saveNewWithDraft(
            ProcessInstance instance, Long onlineTableId, String tableName, String masterData, String slaveData) {
        FlowWorkOrder flowWorkOrder = this.createWith(instance);
        flowWorkOrder.setWorkOrderCode(this.generateWorkOrderCode(instance.getProcessDefinitionKey()));
        flowWorkOrder.setOnlineTableId(onlineTableId);
        flowWorkOrder.setTableName(tableName);
        flowWorkOrder.setFlowStatus(FlowTaskStatus.DRAFT);
        JSONObject draftData = new JSONObject();
        if (masterData != null) {
            draftData.put(FlowConstant.MASTER_DATA_KEY, masterData);
        }
        if (slaveData != null) {
            draftData.put(FlowConstant.SLAVE_DATA_KEY, slaveData);
        }
        FlowWorkOrderExt flowWorkOrderExt =
                BeanUtil.copyProperties(flowWorkOrder, FlowWorkOrderExt.class);
        flowWorkOrderExt.setId(idGenerator.nextLongId());
        flowWorkOrderExt.setDraftData(JSON.toJSONString(draftData));
        flowWorkOrderExtMapper.insert(flowWorkOrderExt);
        this.doSafeSave(flowWorkOrder, instance);
        return flowWorkOrder;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateDraft(Long workOrderId, String masterData, String slaveData) {
        JSONObject draftData = new JSONObject();
        if (masterData != null) {
            draftData.put(FlowConstant.MASTER_DATA_KEY, masterData);
        }
        if (slaveData != null) {
            draftData.put(FlowConstant.SLAVE_DATA_KEY, slaveData);
        }
        FlowWorkOrderExt flowWorkOrderExt = new FlowWorkOrderExt();
        flowWorkOrderExt.setDraftData(JSON.toJSONString(draftData));
        flowWorkOrderExt.setUpdateTime(new Date());
        flowWorkOrderExtMapper.update(flowWorkOrderExt,
                new LambdaQueryWrapper<FlowWorkOrderExt>().eq(FlowWorkOrderExt::getWorkOrderId, workOrderId));
    }

    /**
     * 删除指定数据。
     *
     * @param workOrderId 主键Id。
     * @return 成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean remove(Long workOrderId) {
        return flowWorkOrderMapper.deleteById(workOrderId) == 1;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void removeByProcessInstanceId(String processInstanceId) {
        FlowWorkOrder filter = new FlowWorkOrder();
        filter.setProcessInstanceId(processInstanceId);
        super.removeBy(filter);
    }

    @Override
    public List<FlowWorkOrder> getFlowWorkOrderList(FlowWorkOrder filter, String orderBy) {
        this.normalizeFilter(filter);
        return flowWorkOrderMapper.getFlowWorkOrderList(filter, orderBy);
    }

    @Override
    public MyPageData<FlowWorkOrderVo> getFlowWorkOrderListWithDataIds(
            FlowWorkOrder filter, List<String> dataIds, MyPageParam pageParam, String orderBy) {
        this.normalizeFilter(filter);
        int batchSize = 1000;
        if (dataIds.size() <= batchSize * 2) {
            int totalIdCount = dataIds.size();
            int fromIndex = 0;
            int toIndex = Math.min(batchSize, totalIdCount);
            List<String> dataIdList = new LinkedList<>();
            while (toIndex > fromIndex) {
                List<String> subDataIds = dataIds.subList(fromIndex, toIndex);
                dataIdList.add("'" + CollUtil.join(subDataIds, "','") + "'");
                fromIndex = toIndex;
                toIndex = Math.min(batchSize + fromIndex, totalIdCount);
            }
            if (pageParam != null) {
                PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize(), pageParam.getCount());
            }
            List<FlowWorkOrder> dataList = flowWorkOrderMapper.getFlowWorkOrderListWithDataIds(filter, dataIdList, orderBy);
            return MyPageUtil.makeResponseData(dataList, FlowWorkOrderVo.class);
        }
        Set<String> dataIdSet = new HashSet<>(dataIds);
        List<FlowWorkOrder> dataList = flowWorkOrderMapper.getFlowWorkOrderList(filter, orderBy);
        dataList = dataList.stream()
                .filter(o -> CollUtil.contains(dataIdSet, o.getBusinessKey())).collect(Collectors.toList());
        if (pageParam == null) {
            return MyPageUtil.makeResponseData(dataList, FlowWorkOrderVo.class);
        }
        long totalCount = dataList.size();
        dataList = dataList.stream()
                .skip((long) pageParam.getPageSize() * (pageParam.getPageNum() - 1))
                .limit(pageParam.getPageSize()).collect(Collectors.toList());
        return MyPageUtil.makeResponseData(dataList, totalCount, FlowWorkOrderVo.class);
    }

    @Override
    public List<FlowWorkOrder> getFlowWorkOrderListWithRelation(FlowWorkOrder filter, String orderBy) {
        List<FlowWorkOrder> resultList = this.getFlowWorkOrderList(filter, orderBy);
        this.buildRelationForDataList(resultList, MyRelationParam.dictOnly());
        return resultList;
    }

    @Override
    public FlowWorkOrder getFlowWorkOrderByProcessInstanceId(String processInstanceId) {
        FlowWorkOrder filter = new FlowWorkOrder();
        filter.setProcessInstanceId(processInstanceId);
        return flowWorkOrderMapper.selectOne(new QueryWrapper<>(filter));
    }

    @Override
    public boolean existByBusinessKey(String tableName, Object businessKey, boolean unfinished) {
        LambdaQueryWrapper<FlowWorkOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FlowWorkOrder::getBusinessKey, businessKey.toString());
        queryWrapper.eq(FlowWorkOrder::getTableName, tableName);
        if (unfinished) {
            queryWrapper.notIn(FlowWorkOrder::getFlowStatus,
                    FlowTaskStatus.FINISHED, FlowTaskStatus.CANCELLED, FlowTaskStatus.STOPPED);
        }
        return flowWorkOrderMapper.selectCount(queryWrapper) > 0;
    }

    @Override
    public boolean existUnfinished(String processDefinitionKey, Object businessKey) {
        LambdaQueryWrapper<FlowWorkOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FlowWorkOrder::getBusinessKey, businessKey.toString());
        queryWrapper.eq(FlowWorkOrder::getProcessDefinitionKey, processDefinitionKey);
        queryWrapper.notIn(FlowWorkOrder::getFlowStatus,
                FlowTaskStatus.FINISHED, FlowTaskStatus.CANCELLED, FlowTaskStatus.STOPPED);
        return flowWorkOrderMapper.selectCount(queryWrapper) > 0;
    }

    @DisableDataFilter
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateFlowStatusByProcessInstanceId(String processInstanceId, Integer flowStatus) {
        if (flowStatus == null) {
            return;
        }
        FlowWorkOrder flowWorkOrder = new FlowWorkOrder();
        flowWorkOrder.setFlowStatus(flowStatus);
        if (FlowTaskStatus.FINISHED != flowStatus) {
            flowWorkOrder.setUpdateTime(new Date());
            flowWorkOrder.setUpdateUserId(TokenData.takeFromRequest().getUserId());
        }
        LambdaQueryWrapper<FlowWorkOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FlowWorkOrder::getProcessInstanceId, processInstanceId);
        flowWorkOrderMapper.update(flowWorkOrder, queryWrapper);
    }

    @DisableDataFilter
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateLatestApprovalStatusByProcessInstanceId(String processInstanceId, Integer approvalStatus) {
        if (approvalStatus == null) {
            return;
        }
        FlowWorkOrder flowWorkOrder = this.getFlowWorkOrderByProcessInstanceId(processInstanceId);
        flowWorkOrder.setLatestApprovalStatus(approvalStatus);
        flowWorkOrder.setUpdateTime(new Date());
        flowWorkOrder.setUpdateUserId(TokenData.takeFromRequest().getUserId());
        flowWorkOrderMapper.updateById(flowWorkOrder);
        if (flowWorkOrder.getOnlineTableId() != null) {
            // 处理在线表单工作流的自定义状态更新。
            flowCustomExtFactory.getOnlineBusinessDataExtHelper().updateFlowStatus(flowWorkOrder);
        } else {
            // 处理路由表单工作里的自定义状态更新。
            flowCustomExtFactory.getBusinessDataExtHelper().updateFlowStatus(flowWorkOrder);
        }
    }

    @Override
    public boolean hasDataPermOnFlowWorkOrder(String processInstanceId) {
        // 开启数据权限，并进行验证。
        boolean originalFlag = GlobalThreadLocal.setDataFilter(true);
        long count;
        try {
            FlowWorkOrder filter = new FlowWorkOrder();
            filter.setProcessInstanceId(processInstanceId);
            count = flowWorkOrderMapper.selectCount(new QueryWrapper<>(filter));
        } finally {
            // 恢复之前的数据权限标记
            GlobalThreadLocal.setDataFilter(originalFlag);
        }
        return count > 0;
    }

    @Override
    public void fillUserShowNameByLoginName(List<FlowWorkOrderVo> dataList) {
        BaseFlowIdentityExtHelper identityExtHelper = flowCustomExtFactory.getFlowIdentityExtHelper();
        Set<String> loginNameSet = dataList.stream()
                .map(FlowWorkOrderVo::getSubmitUsername).collect(Collectors.toSet());
        if (CollUtil.isEmpty(loginNameSet)) {
            return;
        }
        Map<String, String> userNameMap = identityExtHelper.mapUserShowNameByLoginName(loginNameSet);
        dataList.forEach(workOrder -> {
            if (StrUtil.isNotBlank(workOrder.getSubmitUsername())) {
                workOrder.setUserShowName(userNameMap.get(workOrder.getSubmitUsername()));
            }
        });
    }

    @Override
    public FlowWorkOrderExt getFlowWorkOrderExtByWorkOrderId(Long workOrderId) {
        return flowWorkOrderExtMapper.selectOne(
                new LambdaQueryWrapper<FlowWorkOrderExt>().eq(FlowWorkOrderExt::getWorkOrderId, workOrderId));
    }

    @Override
    public List<FlowWorkOrderExt> getFlowWorkOrderExtByWorkOrderIds(Set<Long> workOrderIds) {
        return flowWorkOrderExtMapper.selectList(
                new LambdaQueryWrapper<FlowWorkOrderExt>().in(FlowWorkOrderExt::getWorkOrderId, workOrderIds));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CallResult removeDraft(FlowWorkOrder flowWorkOrder) {
        CallResult r = flowApiService.stopProcessInstance(flowWorkOrder.getProcessInstanceId(), "撤销草稿", true);
        if (!r.isSuccess()) {
            return r;
        }
        flowWorkOrderMapper.deleteById(flowWorkOrder.getWorkOrderId());
        return CallResult.ok();
    }


    @Override
    public MyPageData<FlowWorkOrderVo> getPagedWorkOrderListAndBuildData(
            String processDefinitionKey,
            FlowWorkOrderDto flowWorkOrderDtoFilter,
            List<String> businessKeys,
            MyPageParam pageParam,
            MyOrderParam orderParam) {
        String orderBy = MyOrderParam.buildOrderBy(orderParam, FlowWorkOrder.class);
        FlowWorkOrder filter = flowOperationHelper.makeWorkOrderFilter(flowWorkOrderDtoFilter, processDefinitionKey);
        MyPageData<FlowWorkOrderVo> resultData;
        if (CollUtil.isEmpty(businessKeys)) {
            PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize(), pageParam.getCount());
            List<FlowWorkOrder> flowWorkOrderList = this.getFlowWorkOrderList(filter, orderBy);
            resultData = MyPageUtil.makeResponseData(flowWorkOrderList, FlowWorkOrderVo.class);
        } else {
            resultData = this.getFlowWorkOrderListWithDataIds(filter, businessKeys, pageParam, orderBy);
        }
        if (CollUtil.isEmpty(resultData.getDataList())) {
            return resultData;
        }
        flowOperationHelper.buildWorkOrderApprovalStatus(processDefinitionKey, resultData.getDataList());
        // 根据工单的提交用户名获取用户的显示名称，便于前端显示。
        // 同时这也是一个如何通过插件方法，将loginName映射到showName的示例，
        this.fillUserShowNameByLoginName(resultData.getDataList());
        // 组装工单中需要返回给前端的流程任务数据。
        flowOperationHelper.buildWorkOrderTaskInfo(resultData.getDataList());
        return resultData;
    }

    private void normalizeFilter(FlowWorkOrder filter) {
        if (filter == null) {
            filter = new FlowWorkOrder();
        }
        TokenData tokenData = TokenData.takeFromRequest();
        filter.setTenantId(tokenData.getTenantId());
        filter.setAppCode(tokenData.getAppCode());
    }

    private FlowWorkOrder createWith(ProcessInstance instance) {
        TokenData tokenData = TokenData.takeFromRequest();
        Date now = new Date();
        FlowWorkOrder flowWorkOrder = new FlowWorkOrder();
        flowWorkOrder.setWorkOrderId(idGenerator.nextLongId());
        flowWorkOrder.setProcessDefinitionKey(instance.getProcessDefinitionKey());
        flowWorkOrder.setProcessDefinitionName(instance.getProcessDefinitionName());
        flowWorkOrder.setProcessDefinitionId(instance.getProcessDefinitionId());
        flowWorkOrder.setProcessInstanceId(instance.getId());
        flowWorkOrder.setSubmitUsername(tokenData.getLoginName());
        flowWorkOrder.setDeptId(tokenData.getDeptId());
        flowWorkOrder.setAppCode(tokenData.getAppCode());
        flowWorkOrder.setTenantId(tokenData.getTenantId());
        flowWorkOrder.setCreateUserId(tokenData.getUserId());
        flowWorkOrder.setUpdateUserId(tokenData.getUserId());
        flowWorkOrder.setCreateTime(now);
        flowWorkOrder.setUpdateTime(now);
        flowWorkOrder.setDeletedFlag(GlobalDeletedFlag.NORMAL);
        return flowWorkOrder;
    }

    private String generateWorkOrderCode(String processDefinitionKey) {
        FlowEntry flowEntry = flowEntryService.getFlowEntryFromCache(processDefinitionKey);
        if (StrUtil.isBlank(flowEntry.getEncodedRule())) {
            return null;
        }
        ColumnEncodedRule rule = JSON.parseObject(flowEntry.getEncodedRule(), ColumnEncodedRule.class);
        if (rule.getIdWidth() == null) {
            rule.setIdWidth(10);
        }
        return commonRedisUtil.generateTransId(
                rule.getPrefix(), rule.getPrecisionTo(), rule.getMiddle(), rule.getIdWidth());
    }
    
    private void recalculateWorkOrderCode(String processDefinitionKey) {
        FlowEntry flowEntry = flowEntryService.getFlowEntryFromCache(processDefinitionKey);
        if (StrUtil.isBlank(flowEntry.getEncodedRule())) {
            return;
        }
        // 获取当前流程定义中，为工单编码字段设置的规则配置信息。
        ColumnEncodedRule rule = JSON.parseObject(flowEntry.getEncodedRule(), ColumnEncodedRule.class);
        if (rule.getIdWidth() == null) {
            rule.setIdWidth(10);
        }
        // 根据当前规则中的数据，计算出该规则在Redis中AtomicLong对象的键。
        String prefix = commonRedisUtil.calculateTransIdPrefix(rule.getPrefix(), rule.getPrecisionTo(), rule.getMiddle());
        // 根据该键(规则前缀)计算出符合该前缀的工单编码的最大值。
        String maxWorkOrderCode = flowWorkOrderMapper.getMaxWorkOrderCodeByPrefix(prefix + "%");
        // 移除前缀部分，剩余部分即为计数器的最大值。
        String maxValue = StrUtil.removePrefix(maxWorkOrderCode, prefix);
        // 用当前的最大值，为该key的AtomicLong对象设置初始值，后面的请求都会在该值上原子性加一了。
        commonRedisUtil.initTransId(prefix, Long.valueOf(maxValue));
    }
    
    private void doSafeSave(FlowWorkOrder flowWorkOrder, ProcessInstance instance) {
        try {
            flowWorkOrderMapper.insert(flowWorkOrder);
        } catch (DuplicateKeyException e) {
            // 数据插入过程中，如果抛出 “数据重复值 (DuplicationKeyException)” 时，会捕捉该异常。
            // 执行 SQL 查询操作，判断本次计算的工单编码是否已经存在。如不存在，该异常则为其他字段值重复所引起，可直接再次抛出。
            if (flowWorkOrderMapper.getCountByWorkOrderCode(flowWorkOrder.getWorkOrderCode()) == 0) {
                throw e;
            }
            log.info("WorkOrderCode [{}] exists and recalculate.", flowWorkOrder.getWorkOrderCode());
            // 如存在该工单编码的数据，则可以理解为负责计算工单编码的 Redis 出现了问题，
            // 需要为该工单字段所关联的 Redis 原子计数器重新设置初始值。
            this.recalculateWorkOrderCode(instance.getProcessDefinitionKey());
            // 重新初始化后，再次执行generateWorkOrderCode方法计算出新的工单编码。
            flowWorkOrder.setWorkOrderCode(this.generateWorkOrderCode(instance.getProcessDefinitionKey()));
            // 并再次提交当前的工单数据。
            flowWorkOrderMapper.insert(flowWorkOrder);
        }
    }
}
