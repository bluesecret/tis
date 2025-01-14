package io.wangk.peekaboo.common.flow.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.StrFormatter;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpStatus;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import io.wangk.peekaboo.common.core.constant.ApplicationConstant;
import io.wangk.peekaboo.common.core.constant.FieldFilterType;
import io.wangk.peekaboo.common.core.constant.GlobalDeletedFlag;
import io.wangk.peekaboo.common.core.exception.MyRuntimeException;
import io.wangk.peekaboo.common.core.object.TokenData;
import io.wangk.peekaboo.common.core.object.Tuple2;
import io.wangk.peekaboo.common.core.util.MyCommonUtil;
import io.wangk.peekaboo.common.core.util.MyModelUtil;
import io.wangk.peekaboo.common.dbutil.object.*;
import io.wangk.peekaboo.common.dbutil.util.DataSourceUtil;
import io.wangk.peekaboo.common.flow.constant.FlowAutoActionType;
import io.wangk.peekaboo.common.flow.constant.FlowConstant;
import io.wangk.peekaboo.common.flow.model.*;
import io.wangk.peekaboo.common.flow.model.constant.FlowVariableType;
import io.wangk.peekaboo.common.flow.object.*;
import io.wangk.peekaboo.common.flow.service.FlowApiService;
import io.wangk.peekaboo.common.flow.service.FlowAutoVariableLogService;
import io.wangk.peekaboo.common.flow.service.FlowDblinkService;
import io.wangk.peekaboo.common.flow.service.FlowTaskExtService;
import io.wangk.peekaboo.common.sequence.wrapper.IdGeneratorWrapper;
import com.ql.util.express.ExpressRunner;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.impl.persistence.entity.ExecutionEntityImpl;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static io.wangk.peekaboo.common.flow.object.AutoTaskConfig.*;

/**
 * 自动化流程的帮助对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Slf4j
@Component
public class AutoFlowHelper {

    @Autowired
    private FlowApiService flowApiService;
    @Autowired
    private FlowDblinkService flowDblinkService;
    @Autowired
    private FlowTaskExtService flowTaskExtService;
    @Autowired
    private FlowAutoVariableLogService flowAutoVariableLogService;
    @Autowired
    private FlowDataSourceUtil flowDataSourceUtil;
    @Autowired
    private IdGeneratorWrapper idGenerator;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    private static final ThreadLocal<JSONObject> START_AUTO_INIT_VARIABLES = ThreadLocal.withInitial(() -> null);
    private static final List<FlowEntryVariable> SYSTEM_VARIABLES = CollUtil.newLinkedList();

    static {
        SYSTEM_VARIABLES.add(FlowEntryVariable.createAutoVariable("currentTime", "当前时间", FlowVariableType.SYSTEM));
        SYSTEM_VARIABLES.add(FlowEntryVariable.createAutoVariable("initialUserId", "发起用户ID", FlowVariableType.SYSTEM));
        SYSTEM_VARIABLES.add(FlowEntryVariable.createAutoVariable("initialLoginName", "发起用户登录名", FlowVariableType.SYSTEM));
        SYSTEM_VARIABLES.add(FlowEntryVariable.createAutoVariable("initialShowName", "发起用户显示名", FlowVariableType.SYSTEM));
        SYSTEM_VARIABLES.add(FlowEntryVariable.createAutoVariable("initialDeptId", "发起用户部门ID", FlowVariableType.SYSTEM));
        SYSTEM_VARIABLES.add(FlowEntryVariable.createAutoVariable("tokenData", "Token令牌数据", FlowVariableType.SYSTEM));
        SYSTEM_VARIABLES.add(FlowEntryVariable.createAutoVariable("logicNormal", "逻辑删除正常值", FlowVariableType.SYSTEM));
        SYSTEM_VARIABLES.add(FlowEntryVariable.createAutoVariable("logicDelete", "逻辑删除删除值", FlowVariableType.SYSTEM));
    }

    /**
     * 设置初始化变量，仅在启动自动化流程的时候存入。临时存入变量参数到线程本地化存储，以后任务监听器的读取。
     *
     * @param variables 自动化流程启动时的初始变量对象。
     */
    public static void setStartAutoInitVariables(JSONObject variables) {
        START_AUTO_INIT_VARIABLES.set(variables);
    }

    /**
     * 获取自动化流程启动时传入的初始变量数据。
     *
     * @return 自动化流程启动时传入的初始变量数据。
     */
    public static JSONObject getStartAutoInitVariables() {
        return START_AUTO_INIT_VARIABLES.get();
    }

    /**
     * 清空该存储数据，主动释放线程本地化存储资源。
     */
    public static void clearStartAutoInitVariables() {
        START_AUTO_INIT_VARIABLES.remove();
    }

    public static List<FlowEntryVariable> systemFlowEntryVariables() {
        return SYSTEM_VARIABLES;
    }

    /**
     * 获取实时的系统变量。
     *
     * @return 系统变量键值对对象。
     */
    public JSONObject getRealtimeSystemVariables() {
        JSONObject systemVariables = new JSONObject();
        systemVariables.put("currentTime", new Date());
        return systemVariables;
    }

    /**
     * 获取非实时的系统变量。
     *
     * @return 系统变量键值对对象。
     */
    public JSONObject getNonRealtimeSystemVariables() {
        JSONObject systemVariables = this.getRealtimeSystemVariables();
        TokenData tokenData = TokenData.takeFromRequest();
        systemVariables.put("initialUserId", tokenData != null ? tokenData.getUserId() : null);
        systemVariables.put("initialLoginName", tokenData != null ? tokenData.getLoginName() : null);
        systemVariables.put("initialShowName", tokenData != null ? tokenData.getShowName() : null);
        systemVariables.put("initialDeptId", tokenData != null ? tokenData.getDeptId() : null);
        systemVariables.put("tokenData", tokenData != null ? tokenData.getToken() : null);
        systemVariables.put("logicNormal", GlobalDeletedFlag.NORMAL);
        systemVariables.put("logicDelete", GlobalDeletedFlag.DELETED);
        return systemVariables;
    }

    /**
     * 解析自动化任务配置对象。
     *
     * @param processDefinitionId 流程定义Id。
     * @param taskKey             流程任务定义标识。
     * @return 自动化任务的配置对象。
     */
    public AutoTaskConfig parseAutoTaskConfig(String processDefinitionId, String taskKey) {
        FlowTaskExt taskExt = flowTaskExtService.getByProcessDefinitionIdAndTaskId(processDefinitionId, taskKey);
        return JSON.parseObject(taskExt.getAutoConfigJson(), AutoTaskConfig.class);
    }

    /**
     * 解析自动化任务中的任务变量。
     *
     * @param bpmnXml 流程图。
     * @return 当前流程图中的自动化任务输出变量的列表。
     */
    public List<AutoTaskVariable> parseAutoTaskVariables(String bpmnXml) {
        List<AutoTaskVariable> resultList = new LinkedList<>();
        if (StrUtil.isBlank(bpmnXml)) {
            return resultList;
        }
        BpmnModel model = flowApiService.convertToBpmnModel(bpmnXml);
        List<FlowTaskExt> taskExtList = flowTaskExtService.buildAutoTaskExtList(model);
        taskExtList.stream()
                .filter(t -> StrUtil.isNotBlank(t.getAutoConfigJson()))
                .map(t -> JSON.parseObject(t.getAutoConfigJson(), AutoTaskConfig.class))
                .filter(taskConfig -> StrUtil.isNotBlank(taskConfig.getTaskKey()) && taskConfig.getActionType() != null)
                .forEach(taskConfig -> {
                    AutoTaskVariable taskVariable = MyModelUtil.copyTo(taskConfig, AutoTaskVariable.class);
                    if (taskConfig.getActionType().equals(FlowAutoActionType.SELECT_ONE)) {
                        taskVariable.setFieldList(taskConfig.getSelectFieldList());
                        if (CollUtil.isEmpty(taskConfig.getSelectFieldList())) {
                            List<SqlTableColumn> columnList =
                                    flowDataSourceUtil.getTableColumnList(taskConfig.getSrcDblinkId(), taskConfig.getSrcTableName());
                            taskVariable.setFieldList(
                                    columnList.stream().map(SqlTableColumn::getColumnName).collect(Collectors.toList()));
                        }
                        resultList.add(taskVariable);
                    } else if (taskConfig.getActionType().equals(FlowAutoActionType.AGGREGATION_CALC)) {
                        List<String> aliasList = taskConfig.getAggregationDataList()
                                .stream().map(AggregationInfo::getAlias).collect(Collectors.toList());
                        taskVariable.setFieldList(aliasList);
                        resultList.add(taskVariable);
                    }
                    if (taskConfig.getActionType().equals(FlowAutoActionType.HTTP)
                            && taskConfig.getHttpResponnseData() != null
                            && StrUtil.isNotBlank(taskConfig.getHttpResponnseData().getHttpResponseBody())) {
                        taskVariable.setHttpResponseBody(taskConfig.getHttpResponnseData().getHttpResponseBody());
                        resultList.add(taskVariable);
                    }
                    if (taskConfig.getActionType().equals(FlowAutoActionType.SEND_MQ)
                            && BooleanUtil.isFalse(taskConfig.getAutoTrigger())
                            && StrUtil.isNotBlank(taskConfig.getMessageResponnseBody())) {
                        taskVariable.setMessageResponseBody(taskConfig.getMessageResponnseBody());
                        resultList.add(taskVariable);
                    }
                });
        return resultList;
    }

    /**
     * 指定指定的任务。
     *
     * @param transProducer 自动化任务执行时的生产者流水对象。
     * @param taskConfig    自动化任务配置对象。
     * @param d             当前的执行委托对象。
     */
    public void executeTask(FlowTransProducer transProducer, AutoTaskConfig taskConfig, DelegateExecution d) {
        JSONObject variableData = this.getAutomaticVariable(d.getProcessInstanceId());
        FlowDblink destDblink = new FlowDblink();
        destDblink.setDblinkId(taskConfig.getDestDblinkId());
        destDblink.setDblinkType(taskConfig.getDestDblinkType());
        SqlTable destTable = flowDblinkService.getDblinkTable(destDblink, taskConfig.getDestTableName());
        if (taskConfig.getActionType().equals(FlowAutoActionType.ADD_NEW)) {
            List<AutoExecData> execDataList = this.makeInsertAutoExecData(taskConfig, destTable, variableData);
            this.doExecuteSql(execDataList);
        } else if (taskConfig.getActionType().equals(FlowAutoActionType.UPDATE)) {
            AutoExecData execData = this.makeUpdateAutoExecData(taskConfig, destTable, variableData);
            this.doExecuteSql(CollUtil.newArrayList(execData));
        } else if (taskConfig.getActionType().equals(FlowAutoActionType.DELETE)) {
            AutoExecData execData = this.makeDeleteAutoExecData(taskConfig, destTable, variableData);
            this.doExecuteSql(CollUtil.newArrayList(execData));
        } else if (taskConfig.getActionType().equals(FlowAutoActionType.SELECT_ONE)) {
            this.doQueryOne(taskConfig, variableData, d);
        } else if (taskConfig.getActionType().equals(FlowAutoActionType.AGGREGATION_CALC)) {
            this.doAggregationCalculate(taskConfig, variableData, d);
        } else if (taskConfig.getActionType().equals(FlowAutoActionType.NUMBER_CALC)) {
            this.doNumberCalculate(taskConfig, variableData, d);
        } else if (taskConfig.getActionType().equals(FlowAutoActionType.HTTP)) {
            this.doHttpRequest(transProducer.getTransId(), taskConfig, variableData, d);
        } else if (taskConfig.getActionType().equals(FlowAutoActionType.LOAD_CACHE)) {
            this.doLoadRedisCache(taskConfig, variableData);
        } else if (taskConfig.getActionType().equals(FlowAutoActionType.DELETE_CACHE)) {
            this.doDeleteRedisCache(taskConfig, variableData);
        } else if (taskConfig.getActionType().equals(FlowAutoActionType.SEND_MQ)) {
            this.doSendMq(transProducer, taskConfig, variableData);
        } else if (taskConfig.getActionType().equals(FlowAutoActionType.CONSUME_MQ)) {
            this.doConsumeMq(transProducer, taskConfig, d);
        }
    }

    /**
     * 触发指定的执行实例。
     *
     * @param transProducer 生产者执行流水对象。
     */
    public void trigger(FlowTransProducer transProducer) {
        AutoTaskConfig taskConfig = JSON.parseObject(transProducer.getAutoTaskConfig(), AutoTaskConfig.class);
        if (BooleanUtil.isTrue(taskConfig.getAutoTrigger())) {
            this.triggerDirectly(transProducer, null);
        } else {
            ExecutionEntityImpl d = new ExecutionEntityImpl();
            d.setId(transProducer.getExecutionId());
            d.setProcessInstanceId(transProducer.getProcessInstanceId());
            this.executeTask(transProducer, taskConfig, d);
        }
    }

    /**
     * 立即触发指定的执行实例。
     *
     * @param transProducer 生产者执行流水对象。
     */
    public void triggerDirectly(FlowTransProducer transProducer, Map<String, Object> variableMap) {
        Map<String, Object> transVariableMap = new HashMap<>(1);
        transVariableMap.put(FlowConstant.AUTO_FLOW_TRANS_PRODUCER_VAR, transProducer);
        if (MapUtil.isNotEmpty(variableMap)) {
            transVariableMap.putAll(variableMap);
        }
        runtimeService.trigger(transProducer.getExecutionId(), null, transVariableMap);
    }

    private void doSendMq(FlowTransProducer transProducer, AutoTaskConfig taskConfig, JSONObject variableData) {
        String message = MyCommonUtil.replaceAllWithVariableData(taskConfig.getMessageData(), variableData);
        Message<String> m = MessageBuilder.withPayload(message)
                .setHeader(FlowConstant.AUTO_FLOW_MESSAGE_TYPE_KEY, taskConfig.getMessageType())
                .setHeader(FlowConstant.AUTO_FLOW_COMMAND_TYPE_KEY, taskConfig.getMessageCommand())
                .setHeader(RocketMQHeaders.TRANSACTION_ID, transProducer.getTransId())
                .setHeader(RocketMQHeaders.KEYS, transProducer.getTransId()).build();
        SendResult result = rocketMqTemplate.syncSend(taskConfig.getMessageTopic(), m);
        if (result.getSendStatus() != SendStatus.SEND_OK) {
            String error = StrFormatter.format(
                    "Failed to send message for TransId {}, MessageType {}, CommandType{}, sendResult{}",
                    transProducer.getTransId(),
                    taskConfig.getMessageType(),
                    taskConfig.getMessageCommand(),
                    result.getSendStatus());
            log.error(error);
            throw new MyRuntimeException(error);
        }
    }

    private void doConsumeMq(FlowTransProducer transProducer, AutoTaskConfig taskConfig, DelegateExecution d) {
        JSONObject messageResponseJson = (JSONObject) d.getTransientVariable(FlowConstant.AUTO_FLOW_CONSUME_MESSAGE_VAR);
        if (MapUtil.isNotEmpty(messageResponseJson)) {
            this.refreshAutoVariableLog(d, taskConfig.getTaskKey(), messageResponseJson);
        }
    }

    private void doLoadRedisCache(AutoTaskConfig taskConfig, JSONObject variableData) {
        if (BooleanUtil.isFalse(taskConfig.getCacheKeyFromSql())) {
            String cacheKey = MyCommonUtil.replaceAllWithVariableData(taskConfig.getCacheKey(), variableData);
            String cacheData = MyCommonUtil.replaceAllWithVariableData(taskConfig.getCacheValue(), variableData);
            this.executeLoadRedisCache(taskConfig, cacheKey, cacheData);
            return;
        }
        List<Map<String, Object>> srcResultList = this.getSrcTableDataList(taskConfig, variableData);
        if (CollUtil.isEmpty(srcResultList)) {
            return;
        }
        for (Map<String, Object> data : srcResultList) {
            List<String> loadKeys = new LinkedList<>();
            for (String cacheKeyFieldName : taskConfig.getCacheKeyFieldNames()) {
                Object v = data.get(cacheKeyFieldName);
                loadKeys.add(v == null ? "null" : v.toString());
            }
            String cacheKey = CollUtil.join(loadKeys, StrUtil.COLON);
            JSONObject cacheDataJson = new JSONObject();
            taskConfig.getCacheValueFieldNames().forEach(fieldName -> {
                Object fieldData = data.get(fieldName);
                if (fieldData != null) {
                    cacheDataJson.put(fieldName, fieldData);
                }
            });
            this.executeLoadRedisCache(taskConfig, cacheKey, cacheDataJson.toJSONString());
        }
    }

    private void executeLoadRedisCache(AutoTaskConfig taskConfig, String cacheKey, String cacheData) {
        if (StrUtil.isNotBlank(taskConfig.getCacheKeyPrefix())) {
            cacheKey = taskConfig.getCacheKeyPrefix() + cacheKey;
        }
        if (taskConfig.getCacheExpireMinutes() != null) {
            redissonClient.getBucket(cacheKey).set(cacheData, taskConfig.getCacheExpireMinutes(), TimeUnit.MINUTES);
        } else {
            redissonClient.getBucket(cacheKey).set(cacheData);
        }
    }

    private void doDeleteRedisCache(AutoTaskConfig taskConfig, JSONObject variableData) {
        if (BooleanUtil.isFalse(taskConfig.getCacheKeyFromSql())) {
            String cacheKey = MyCommonUtil.replaceAllWithVariableData(taskConfig.getCacheKey(), variableData);
            this.executeDeleteRedisCache(taskConfig, cacheKey);
            return;
        }
        List<Map<String, Object>> srcResultList = this.getSrcTableDataList(taskConfig, variableData);
        if (CollUtil.isEmpty(srcResultList)) {
            return;
        }
        for (Map<String, Object> data : srcResultList) {
            List<String> deleteKeys = new LinkedList<>();
            for (String fieldName : taskConfig.getCacheKeyFieldNames()) {
                Object v = data.get(fieldName);
                deleteKeys.add(v == null ? "null" : v.toString());
            }
            String cacheKey = CollUtil.join(deleteKeys, StrUtil.COLON);
            this.executeDeleteRedisCache(taskConfig, cacheKey);
        }
    }

    private void executeDeleteRedisCache(AutoTaskConfig taskConfig, String cacheKey) {
        if (StrUtil.isNotBlank(taskConfig.getCacheKeyPrefix())) {
            cacheKey = taskConfig.getCacheKeyPrefix() + cacheKey;
        }
        redissonClient.getBucket(cacheKey).deleteAsync();
    }

    private void doExecuteSql(List<AutoExecData> autoExecDataList) {
        if (CollUtil.isEmpty(autoExecDataList)) {
            return;
        }
        Connection connection = null;
        try {
            connection = flowDataSourceUtil.getConnection(autoExecDataList.get(0).getDblinkId());
            connection.setAutoCommit(false);
            for (AutoExecData autoExecData : autoExecDataList) {
                flowDataSourceUtil.execute(connection, autoExecData.getSql(), autoExecData.getParams());
            }
            connection.commit();
        } catch (Exception e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    log.error(e.getMessage(), e);
                }
            }
            log.error(e.getMessage(), e);
            throw new MyRuntimeException(e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

    private List<AutoExecData> makeInsertAutoExecData(
            AutoTaskConfig taskConfig, SqlTable destTable, JSONObject variableData) {
        List<AutoExecData> resultList = new LinkedList<>();
        SqlTableColumn primaryKeyColumn = destTable.getColumnList().stream()
                .filter(c -> BooleanUtil.isTrue(c.getPrimaryKey())).findFirst().orElse(null);
        Map<String, SqlTableColumn> destColumnMap =
                destTable.getColumnList().stream().collect(Collectors.toMap(SqlTableColumn::getColumnName, c -> c));
        ValueInfo pkValueInfo = null;
        if (primaryKeyColumn != null) {
            pkValueInfo = taskConfig.getInsertDataList().stream()
                    .filter(valueInfo -> valueInfo.getDestColumnName().equals(primaryKeyColumn.getColumnName()))
                    .findFirst().orElse(null);
        }
        //查询源数据。
        List<Map<String, Object>> srcResultList = this.getSrcTableDataList(taskConfig, variableData);
        if (CollUtil.isEmpty(srcResultList)) {
            srcResultList.add(new HashMap<>(1));
        }
        for (Map<String, Object> srcResult : srcResultList) {
            List<Object> params = new LinkedList<>();
            StringBuilder sqlBuilder = new StringBuilder(1024);
            sqlBuilder.append("INSERT INTO ").append(taskConfig.getDestTableName()).append("(");
            if (primaryKeyColumn != null) {
                Object param = this.calculatePrimaryKeyParam(primaryKeyColumn, pkValueInfo, srcResult);
                if (param != null) {
                    sqlBuilder.append(primaryKeyColumn.getColumnName()).append(",");
                    params.add(param);
                }
            }
            for (ValueInfo valueInfo : taskConfig.getInsertDataList()) {
                if (pkValueInfo == null || !pkValueInfo.equals(valueInfo)) {
                    sqlBuilder.append(valueInfo.getDestColumnName()).append(",");
                    SqlTableColumn column = destColumnMap.get(valueInfo.getDestColumnName());
                    String destFieldType = flowDataSourceUtil.convertToJavaType(column, taskConfig.getDestDblinkType());
                    Object value = this.calculateValue(
                            valueInfo.getType(), valueInfo.getSrcValue(), destFieldType, srcResult, variableData);
                    params.add(value);
                }
            }
            sqlBuilder.setCharAt(sqlBuilder.length() - 1, ')');
            sqlBuilder.append(" VALUES(");
            params.forEach(p -> sqlBuilder.append("?,"));
            sqlBuilder.setCharAt(sqlBuilder.length() - 1, ')');
            AutoExecData execData = new AutoExecData();
            execData.setDblinkId(taskConfig.getDestDblinkId());
            execData.setSql(sqlBuilder.toString());
            execData.setParams(params);
            resultList.add(execData);
        }
        return resultList;
    }

    private AutoExecData makeUpdateAutoExecData(AutoTaskConfig taskConfig, SqlTable destTable, JSONObject variableData) {
        Map<String, SqlTableColumn> destColumnMap =
                destTable.getColumnList().stream().collect(Collectors.toMap(SqlTableColumn::getColumnName, c -> c));
        List<Object> params = new LinkedList<>();
        StringBuilder sqlBuilder = new StringBuilder(1024);
        sqlBuilder.append("UPDATE ").append(taskConfig.getDestTableName()).append(" SET ");
        for (ValueInfo valueInfo : taskConfig.getUpdateDataList()) {
            sqlBuilder.append(valueInfo.getDestColumnName()).append(" = ?,");
            SqlTableColumn column = destColumnMap.get(valueInfo.getDestColumnName());
            String destFieldType = flowDataSourceUtil.convertToJavaType(column, taskConfig.getDestDblinkType());
            Object value = this.calculateValue(
                    valueInfo.getType(), valueInfo.getSrcValue(), destFieldType, null, variableData);
            params.add(value);
        }
        sqlBuilder.setCharAt(sqlBuilder.length() - 1, ' ');
        Tuple2<String, List<Object>> result = this.calculateWhereClause(taskConfig, destTable, variableData);
        sqlBuilder.append(result.getFirst());
        CollUtil.addAll(params, result.getSecond());
        AutoExecData execData = new AutoExecData();
        execData.setDblinkId(taskConfig.getDestDblinkId());
        execData.setSql(sqlBuilder.toString());
        execData.setParams(params);
        return execData;
    }

    private AutoExecData makeDeleteAutoExecData(AutoTaskConfig taskConfig, SqlTable destTable, JSONObject variableData) {
        if (StrUtil.isNotBlank(taskConfig.getLogicDeleteField())) {
            List<ValueInfo> updateDataList = new LinkedList<>();
            ValueInfo logicDeleteValueInfo = new ValueInfo();
            logicDeleteValueInfo.setDestColumnName(taskConfig.getLogicDeleteField());
            logicDeleteValueInfo.setType(AutoTaskConfig.FIXED_VALUE);
            logicDeleteValueInfo.setSrcValue(String.valueOf(GlobalDeletedFlag.DELETED));
            updateDataList.add(logicDeleteValueInfo);
            taskConfig.setUpdateDataList(updateDataList);
            return this.makeUpdateAutoExecData(taskConfig, destTable, variableData);
        }
        StringBuilder sqlBuilder = new StringBuilder(1024);
        sqlBuilder.append("DELETE FROM ").append(taskConfig.getDestTableName());
        Tuple2<String, List<Object>> result = this.calculateWhereClause(taskConfig, destTable, variableData);
        sqlBuilder.append(result.getFirst());
        AutoExecData execData = new AutoExecData();
        execData.setDblinkId(taskConfig.getDestDblinkId());
        execData.setSql(sqlBuilder.toString());
        execData.setParams(result.getSecond());
        return execData;
    }

    private void doQueryOne(AutoTaskConfig taskConfig, JSONObject variableData, DelegateExecution d) {
        List<Map<String, Object>> srcResultList = this.getSrcTableDataList(taskConfig, variableData);
        Map<String, Object> srcResult = null;
        if (CollUtil.isNotEmpty(srcResultList)) {
            srcResult = srcResultList.get(0);
        }
        this.refreshAutoVariableLog(d, taskConfig.getTaskKey(), srcResult);
    }

    private void doAggregationCalculate(AutoTaskConfig taskConfig, JSONObject variableData, DelegateExecution d) {
        List<String> aggregationFieldList = new LinkedList<>();
        for (AggregationInfo info : taskConfig.getAggregationDataList()) {
            StringBuilder fieldBuilder = new StringBuilder(128);
            fieldBuilder.append(info.getAggregationFunction())
                    .append("(").append(info.getAggregationColumn()).append(") ").append(info.getAlias());
            aggregationFieldList.add(fieldBuilder.toString());
        }
        taskConfig.setSelectFieldList(aggregationFieldList);
        List<Map<String, Object>> srcResultList = this.getSrcTableDataList(taskConfig, variableData);
        Map<String, Object> srcResult = null;
        if (CollUtil.isNotEmpty(srcResultList)) {
            srcResult = srcResultList.get(0);
        }
        this.refreshAutoVariableLog(d, taskConfig.getTaskKey(), srcResult);
    }

    private void doNumberCalculate(AutoTaskConfig taskConfig, JSONObject variableData, DelegateExecution d) {
        List<String> variableNames = MyCommonUtil.extractVariableNames(taskConfig.getCalculateFormula());
        List<Object> values = this.extractVariableValues(variableNames, variableData);
        String express = this.normalizeNumberFormula(taskConfig.getCalculateFormula(), variableNames, values);
        ExpressRunner runner = new ExpressRunner();
        try {
            Object result = runner.execute(express, null, null, false, false);
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put(FlowConstant.AUTO_FLOW_DATA_RESULT_KEY, result);
            this.refreshAutoVariableLog(d, taskConfig.getTaskKey(), resultMap);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new MyRuntimeException(e);
        }
    }

    private void doHttpRequest(Long transId, AutoTaskConfig taskConfig, JSONObject variableData, DelegateExecution d) {
        AutoHttpRequestInfo req = taskConfig.getHttpRequestInfo();
        AutoHttpResponseData resp = taskConfig.getHttpResponnseData();
        String body = this.buildRequestBody(req, variableData);
        HttpHeaders headers = this.buildHttpHeaders(req, variableData);
        headers.add(ApplicationConstant.HTTP_HEADER_TRANS_ID, transId.toString());
        HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(req.getUrl());
        if (CollUtil.isNotEmpty(req.getUrlParamList())) {
            for (AutoTaskConfig.ValueInfo valueInfo : req.getUrlParamList()) {
                String paramValue = this.calculateValue(valueInfo.getType(), valueInfo.getSrcValue(), variableData);
                uriBuilder.queryParam(valueInfo.getKey(), paramValue);
            }
        }
        ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(
                uriBuilder.encode().toUriString(), HttpMethod.valueOf(req.getHttpMethod()), httpEntity, JSONObject.class);
        try {
            this.handleHttpResponseFail(req, resp, responseEntity);
            this.refreshAutoVariableLog(d, taskConfig.getTaskKey(), responseEntity.getBody());
        } catch (MyRuntimeException e) {
            if (!resp.getFailHandleType().equals(AutoHttpResponseData.CONTINUE_ON_FAIL)) {
                throw e;
            }
            log.error(e.getMessage(), e);
        }
    }

    private Object calculatePrimaryKeyParam(
            SqlTableColumn primaryKeyColumn, ValueInfo pkValueInfo, Map<String, Object> srcResult) {
        // 说明目标表的主键值来自于源表的字段值。
        if (pkValueInfo != null) {
            return srcResult.get(pkValueInfo.getSrcValue());
        }
        if (BooleanUtil.isFalse(primaryKeyColumn.getAutoIncrement())) {
            return primaryKeyColumn.getStringPrecision() == null ? idGenerator.nextLongId() : idGenerator.nextStringId();
        }
        return null;
    }

    private List<Map<String, Object>> getSrcTableDataList(AutoTaskConfig taskConfig, JSONObject variableData) {
        if (ObjectUtil.isEmpty(taskConfig.getSrcDblinkId()) || StrUtil.isBlank(taskConfig.getSrcTableName())) {
            return new LinkedList<>();
        }
        this.appendLogicDeleteFilter(taskConfig);
        FlowDblink srcDblink = new FlowDblink();
        srcDblink.setDblinkId(taskConfig.getSrcDblinkId());
        srcDblink.setDblinkType(taskConfig.getSrcDblinkType());
        SqlTable srcTable = flowDblinkService.getDblinkTable(srcDblink, taskConfig.getSrcTableName());
        StringBuilder sqlBuilder = new StringBuilder(256);
        String selectFields = CollUtil.isEmpty(taskConfig.getSelectFieldList())
                ? "*" : CollUtil.join(taskConfig.getSelectFieldList(), ",");
        sqlBuilder.append("SELECT ").append(selectFields).append(" FROM ").append(srcTable.getTableName());
        if (taskConfig.getSrcFilterType().equals(AutoTaskConfig.SRC_FILTER_SQL)) {
            Tuple2<String, List<Object>> result = this.calcualteCustomSqlFilter(taskConfig.getSrcFilterSql(), variableData);
            if (StrUtil.isNotBlank(result.getFirst())) {
                sqlBuilder.append(result.getFirst());
            }
            try {
                return flowDataSourceUtil.query(taskConfig.getSrcDblinkId(), sqlBuilder.toString(), result.getSecond());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new MyRuntimeException(e);
            }
        }
        DatasetFilter dataFilter = null;
        if (CollUtil.isNotEmpty(taskConfig.getSrcFilterList())) {
            dataFilter = this.calculateDatasetFilter(
                    srcTable, taskConfig.getSrcDblinkType(), taskConfig.getSrcFilterList(), variableData);
        }
        try {
            Tuple2<String, List<Object>> result =
                    flowDataSourceUtil.buildWhereClauseByFilters(taskConfig.getSrcDblinkId(), dataFilter);
            sqlBuilder.append(result.getFirst());
            return flowDataSourceUtil.query(taskConfig.getSrcDblinkId(), sqlBuilder.toString(), result.getSecond());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new MyRuntimeException(e);
        }
    }

    private void refreshAutoVariableLog(DelegateExecution d, String outputVariableName, Map<String, Object> newResult) {
        if (StrUtil.isBlank(outputVariableName)) {
            return;
        }
        runtimeService.setVariable(d.getId(), outputVariableName, newResult);
        FlowAutoVariableLog autoVariableLog =
                flowAutoVariableLogService.getAutoVariableByProcessInstanceId(d.getProcessInstanceId());
        JSONObject latestVariableData = JSON.parseObject(autoVariableLog.getVariableData());
        latestVariableData.put(outputVariableName, newResult);
        autoVariableLog.setVariableData(JSON.toJSONString(latestVariableData));
        flowAutoVariableLogService.updateById(autoVariableLog);
    }

    private Tuple2<String, List<Object>> calculateWhereClause(AutoTaskConfig taskConfig, SqlTable destTable, JSONObject variableData) {
        if (taskConfig.getDestFilterType().equals(AutoTaskConfig.SRC_FILTER_SQL)) {
            return this.calcualteCustomSqlFilter(taskConfig.getDestFilterSql(), variableData);
        }
        if (CollUtil.isNotEmpty(taskConfig.getDestFilterList())) {
            DatasetFilter dataFilter = this.calculateDatasetFilter(
                    destTable, taskConfig.getDestDblinkType(), taskConfig.getDestFilterList(), variableData);
            return flowDataSourceUtil.buildWhereClauseByFilters(taskConfig.getDestDblinkId(), dataFilter);
        }
        return new Tuple2<>(StrUtil.EMPTY, new LinkedList<>());
    }

    private Tuple2<String, List<Object>> calcualteCustomSqlFilter(String filterSql, JSONObject variableData) {
        Tuple2<String, List<String>> result = MyCommonUtil.findAndReplaceAllVariables(filterSql, "?");
        String whereClause = result.getFirst();
        if (StrUtil.isNotBlank(whereClause)) {
            whereClause = DataSourceUtil.SQL_WHERE + whereClause;
        }
        List<Object> params = this.extractVariableValues(result.getSecond(), variableData);
        return new Tuple2<>(whereClause, params);
    }

    private DatasetFilter calculateDatasetFilter(
            SqlTable table, Integer dblinkType, List<FilterInfo> filterInfoList, JSONObject variableData) {
        Map<String, SqlTableColumn> columnMap = table.getColumnList()
                .stream().collect(Collectors.toMap(SqlTableColumn::getColumnName, c -> c));
        DatasetFilter dataFilter = new DatasetFilter();
        filterInfoList.forEach(filterInfo -> {
            DatasetFilter.FilterInfo filter = new DatasetFilter.FilterInfo();
            filter.setFilterType(filterInfo.getFilterType());
            filter.setParamName(filterInfo.getFilterColumnName());
            SqlTableColumn column = columnMap.get(filterInfo.getFilterColumnName());
            String fieldType = flowDataSourceUtil.convertToJavaType(column, dblinkType);
            if (filterInfo.getFilterType().equals(FieldFilterType.IN)) {
                filter.setParamValueList(flowDataSourceUtil.convertToColumnValues(fieldType, filterInfo.getFilterValue()));
            } else {
                Object convertedValue = calculateValue(
                        filterInfo.getValueType(), filterInfo.getFilterValue(), fieldType, null, variableData);
                filter.setParamValue(convertedValue);
            }
            dataFilter.add(filter);
        });
        return dataFilter;
    }

    private void appendLogicDeleteFilter(AutoTaskConfig taskConfig) {
        if (StrUtil.isBlank(taskConfig.getLogicDeleteField())) {
            return;
        }
        if (taskConfig.getSrcFilterType().equals(SRC_FILTER_SQL)) {
            StringBuilder sb = new StringBuilder(512);
            if (StrUtil.isNotBlank(taskConfig.getSrcFilterSql())) {
                sb.append("(").append(taskConfig.getSrcFilterSql())
                        .append(") AND ")
                        .append(taskConfig.getLogicDeleteField())
                        .append("=")
                        .append(GlobalDeletedFlag.NORMAL);
            } else {
                sb.append(taskConfig.getLogicDeleteField()).append("=").append(GlobalDeletedFlag.NORMAL);
            }
            taskConfig.setSrcFilterSql(sb.toString());
        } else {
            List<FilterInfo> filterInfoList = taskConfig.getSrcFilterList();
            if (filterInfoList == null) {
                filterInfoList = new LinkedList<>();
            }
            FilterInfo logicDeleteFilter = new FilterInfo();
            logicDeleteFilter.setFilterColumnName(taskConfig.getLogicDeleteField());
            logicDeleteFilter.setFilterType(FieldFilterType.EQUAL);
            logicDeleteFilter.setValueType(AutoTaskConfig.FIXED_VALUE);
            logicDeleteFilter.setFilterValue(String.valueOf(GlobalDeletedFlag.NORMAL));
            filterInfoList.add(logicDeleteFilter);
            taskConfig.setSrcFilterList(filterInfoList);
        }
    }

    private String buildRequestBody(AutoHttpRequestInfo req, JSONObject variableData) {
        String body = null;
        if (StrUtil.equals(req.getBodyType(), AutoHttpRequestInfo.BODY_TYPE_RAW)) {
            body = MyCommonUtil.replaceAllWithVariableData(req.getBodyData(), variableData);
        } else {
            StringBuilder sb = new StringBuilder(256);
            if (CollUtil.isNotEmpty(req.getFormDataList())) {
                for (ValueInfo valueInfo : req.getFormDataList()) {
                    String value = this.calculateValue(valueInfo.getType(), valueInfo.getSrcValue(), variableData);
                    sb.append(valueInfo.getKey()).append("=").append(value).append("&");
                }
                body = sb.substring(0, sb.length() - 1);
            }
        }
        return body;
    }

    private HttpHeaders buildHttpHeaders(AutoHttpRequestInfo req, JSONObject variableData) {
        HttpHeaders headers = new HttpHeaders();
        if (CollUtil.isNotEmpty(req.getHeaderList())) {
            for (AutoTaskConfig.ValueInfo valueInfo : req.getHeaderList()) {
                String value = this.calculateValue(valueInfo.getType(), valueInfo.getSrcValue(), variableData);
                headers.add(valueInfo.getKey(), value);
            }
        }
        if (StrUtil.equals(req.getBodyType(), AutoHttpRequestInfo.BODY_TYPE_RAW)) {
            headers.setContentType(MediaType.APPLICATION_JSON);
        } else {
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        }
        return headers;
    }

    private void handleHttpResponseFail(
            AutoHttpRequestInfo req, AutoHttpResponseData resp, ResponseEntity<JSONObject> responseEntity) {
        Set<Integer> successStatusCodes = CollUtil.newHashSet(HttpStatus.HTTP_OK);
        if (StrUtil.isNotBlank(resp.getSuccessStatusCode())) {
            successStatusCodes = StrUtil.split(resp.getSuccessStatusCode(), StrUtil.COMMA)
                    .stream().map(Integer::valueOf).collect(Collectors.toSet());
        }
        // 先判断HttpStatus是否正确。
        if (!CollUtil.contains(successStatusCodes, responseEntity.getStatusCode().value())) {
            String cancelReason = StrFormatter.format(
                    "Failed to Rquest Url [{}] with StatusCode [{}]", req.getUrl(), responseEntity.getStatusCode().value());
            throw new MyRuntimeException(cancelReason);
        }
        // 如果没有配置应答体中标记是否成功的字段，则视为成功。
        if (StrUtil.isBlank(resp.getSuccessBodyField())) {
            return;
        }
        JSONObject responseData = responseEntity.getBody();
        String successFlagField = JSONPath.compile(resp.getSuccessBodyField()).eval(responseData, String.class);
        // 如果应答体中标记是否成功的字段你不存在，或者是应答体中标记为成功的字段值为true，则视为成功。
        if (StrUtil.isBlank(successFlagField)
                || StrUtil.equals(successFlagField, "[]")
                || BooleanUtil.toBooleanObject(successFlagField).equals(Boolean.TRUE)) {
            return;
        }
        // 开始处理失败场景。
        String cancelReason;
        if (StrUtil.isNotBlank(resp.getErrorMessageBodyField())) {
            String errorMsgPath = "$." + resp.getErrorMessageBodyField();
            String errorMsg = JSONPath.compile(errorMsgPath).eval(responseData, String.class);
            cancelReason = StrFormatter.format(
                    "Failed to Rquest Url [{}] with errorMsg [{}]", req.getUrl(), errorMsg);
        } else {
            cancelReason = StrFormatter.format("Failed to Rquest Url [{}]", req.getUrl());
        }
        throw new MyRuntimeException(cancelReason);
    }

    private String normalizeNumberFormula(String calculateFormula, List<String> variableNames, List<Object> values) {
        for (int i = 0; i < variableNames.size(); i++) {
            String variableName = variableNames.get(i);
            Object variableValue = values.get(i);
            calculateFormula = StrUtil.replace(calculateFormula, " + variableName + ", variableValue.toString());
        }
        return calculateFormula;
    }

    private JSONObject getAutomaticVariable(String processInstanceId) {
        JSONObject variableData = getStartAutoInitVariables();
        if (variableData == null) {
            FlowAutoVariableLog v = flowAutoVariableLogService.getAutoVariableByProcessInstanceId(processInstanceId);
            if (v != null) {
                variableData = JSON.parseObject(v.getVariableData());
            }
        }
        JSONObject systemVariables = this.getRealtimeSystemVariables();
        if (variableData == null) {
            return systemVariables;
        }
        variableData.putAll(systemVariables);
        return variableData;
    }

    private List<Object> extractVariableValues(List<String> variableNames, JSONObject variableData) {
        List<Object> resultList = new LinkedList<>();
        if (CollUtil.isEmpty(variableNames) || MapUtil.isEmpty(variableData)) {
            return resultList;
        }
        for (String name : variableNames) {
            Object value = this.verifyAndGetVariableExist(name, variableData);
            if (value != null) {
                resultList.add(value);
            }
        }
        return resultList;
    }

    private Object calculateValue(
            Integer valueType, String value, String fieldType, Map<String, Object> srcResult, JSONObject variableData) {
        if (valueType.equals(AutoTaskConfig.FIXED_VALUE)) {
            return flowDataSourceUtil.convertToColumnValue(fieldType, value);
        } else if (valueType.equals(AutoTaskConfig.COLUMN_VALUE)) {
            return srcResult.get(value);
        }
        Object variableValue = this.verifyAndGetVariableExist(value, variableData);
        return flowDataSourceUtil.convertToColumnValue(fieldType, (Serializable) variableValue);
    }

    private String calculateValue(Integer valueType, String value, JSONObject variableData) {
        if (valueType.equals(AutoTaskConfig.FIXED_VALUE)) {
            return value;
        }
        Object variableValue = this.verifyAndGetVariableExist(value, variableData);
        return variableValue == null ? null : variableValue.toString();
    }

    private Object verifyAndGetVariableExist(String name, JSONObject variableData) {
        String variableName = name;
        if (StrUtil.contains(name, StrUtil.DOT)) {
            variableName = StrUtil.subBefore(name, StrUtil.DOT, false);
        }
        if (!variableData.containsKey(variableName)) {
            throw new MyRuntimeException(StrFormatter.format("变量值 [{}] 不存在！", name));
        }
        if (!StrUtil.contains(name, StrUtil.DOT)) {
            return variableData.get(variableName);
        }
        JSONObject variableObject = variableData.getJSONObject(variableName);
        if (variableObject == null) {
            return null;
        }
        String variablePath = StrUtil.subAfter(name, StrUtil.DOT, false);
        return JSONPath.compile(variablePath).eval(variableObject);
    }
}
