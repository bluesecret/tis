package io.wangk.peekaboo.common.flow.online.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.tags.Tag;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.page.PageMethod;
import io.wangk.peekaboo.common.core.annotation.DisableDataFilter;
import io.wangk.peekaboo.common.core.annotation.MyRequestBody;
import io.wangk.peekaboo.common.core.constant.ErrorCodeEnum;
import io.wangk.peekaboo.common.core.object.*;
import io.wangk.peekaboo.common.core.util.MyModelUtil;
import io.wangk.peekaboo.common.core.util.MyPageUtil;
import io.wangk.peekaboo.common.log.annotation.OperationLog;
import io.wangk.peekaboo.common.log.model.constant.SysOperationLogType;
import io.wangk.peekaboo.common.online.config.OnlineProperties;
import io.wangk.peekaboo.common.online.dto.OnlineFilterDto;
import io.wangk.peekaboo.common.online.model.*;
import io.wangk.peekaboo.common.online.model.constant.FieldKind;
import io.wangk.peekaboo.common.online.service.*;
import io.wangk.peekaboo.common.online.model.constant.FieldFilterType;
import io.wangk.peekaboo.common.online.model.constant.RelationType;
import io.wangk.peekaboo.common.online.util.OnlineOperationHelper;
import io.wangk.peekaboo.common.flow.online.service.FlowOnlineOperationService;
import io.wangk.peekaboo.common.flow.constant.FlowTaskStatus;
import io.wangk.peekaboo.common.flow.constant.FlowConstant;
import io.wangk.peekaboo.common.flow.constant.FlowApprovalType;
import io.wangk.peekaboo.common.flow.util.FlowOperationHelper;
import io.wangk.peekaboo.common.flow.dto.FlowWorkOrderDto;
import io.wangk.peekaboo.common.flow.dto.FlowTaskCommentDto;
import io.wangk.peekaboo.common.flow.exception.FlowOperationException;
import io.wangk.peekaboo.common.flow.model.constant.FlowMessageType;
import io.wangk.peekaboo.common.flow.model.*;
import io.wangk.peekaboo.common.flow.service.*;
import io.wangk.peekaboo.common.flow.vo.*;
import io.wangk.peekaboo.common.redis.cache.SessionCacheHelper;
import io.wangk.peekaboo.common.satoken.annotation.SaTokenDenyAuth;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 工作流在线表单流程操作接口。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Tag(name = "工作流在线表单流程操作接口")
@Slf4j
@RestController
@RequestMapping("${common-flow.urlPrefix}/flowOnlineOperation")
@ConditionalOnProperty(name = "common-flow.operationEnabled", havingValue = "true")
public class FlowOnlineOperationController {

    @Autowired
    private FlowEntryService flowEntryService;
    @Autowired
    private FlowApiService flowApiService;
    @Autowired
    private FlowOperationHelper flowOperationHelper;
    @Autowired
    private FlowOnlineOperationService flowOnlineOperationService;
    @Autowired
    private FlowWorkOrderService flowWorkOrderService;
    @Autowired
    private FlowMessageService flowMessageService;
    @Autowired
    private FlowTransProducerService flowTransProducerService;
    @Autowired
    private OnlineFormService onlineFormService;
    @Autowired
    private OnlinePageService onlinePageService;
    @Autowired
    private OnlineOperationService onlineOperationService;
    @Autowired
    private OnlineTableService onlineTableService;
    @Autowired
    private OnlineDatasourceService onlineDatasourceService;
    @Autowired
    private OnlineOperationHelper onlineOperationHelper;
    @Autowired
    private OnlineProperties onlineProperties;
    @Autowired
    private SessionCacheHelper sessionCacheHelper;

    private static final String ONE_TO_MANY_VAR_SUFFIX = "List";

    /**
     * 根据指定流程的主版本，发起一个流程实例，同时作为第一个任务节点的执行人，执行第一个用户任务。
     * 该接口无需数据权限过滤，因此用DisableDataFilter注解标注。如果当前系统没有支持数据权限过滤，该注解不会有任何影响。
     * 注：流程设计页面的"启动"按钮，调用该接口可以启动任何流程用于流程配置后的测试验证。
     *
     * @param processDefinitionKey 流程定义标识。
     * @param flowTaskCommentDto   审批意见。
     * @param taskVariableData     流程任务变量数据。
     * @param masterData           流程审批相关的主表数据。
     * @param slaveData            流程审批相关的多个从表数据。
     * @param copyData             传阅数据，格式为type和id，type的值参考FlowConstant中的常量值。
     * @return 应答结果对象。
     */
    @DisableDataFilter
    @SaCheckPermission("flowEntry.all")
    @OperationLog(type = SysOperationLogType.START_FLOW)
    @PostMapping("/startPreview")
    public ResponseResult<Void> startPreview(
            @MyRequestBody(required = true) String processDefinitionKey,
            @MyRequestBody(required = true) FlowTaskCommentDto flowTaskCommentDto,
            @MyRequestBody JSONObject taskVariableData,
            @MyRequestBody(required = true) JSONObject masterData,
            @MyRequestBody JSONObject slaveData,
            @MyRequestBody JSONObject copyData) {
        return this.startAndTake(
                processDefinitionKey, flowTaskCommentDto, taskVariableData, masterData, slaveData, copyData);
    }

    /**
     * 通过业务主表主键的方式启动流程。
     *
     * @param processDefinitionKey 流程定义标识。
     * @param id                   业务表主键Id。
     * @param taskVariableData     流程任务变量数据。
     * @return 应答结果对象。
     */
    @DisableDataFilter
    @OperationLog(type = SysOperationLogType.START_FLOW)
    @PostMapping("/startWithBusinessKey")
    public ResponseResult<Void> startWithBusinessKey(
            @MyRequestBody(required = true) String processDefinitionKey,
            @MyRequestBody(required = true) String id,
            @MyRequestBody JSONObject taskVariableData) {
        ResponseResult<Tuple2<FlowEntryPublish, OnlineDatasource>> verifyResult =
                this.verifyAndGetFlowEntryPublishAndDatasource(processDefinitionKey, false);
        if (!verifyResult.isSuccess()) {
            return ResponseResult.errorFrom(verifyResult);
        }
        OnlineDatasource datasource = verifyResult.getData().getSecond();
        if (!onlineOperationService.existId(datasource.getMasterTable(), id)) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        if (flowWorkOrderService.existUnfinished(processDefinitionKey, id)) {
            String errorMessage = "数据验证失败，该业务数据Id存在尚未完成审批的流程实例，同一业务数据主键不能同时重复提交审批！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        String processDefinitionId = verifyResult.getData().getFirst().getProcessDefinitionId();
        flowOnlineOperationService.startWithBusinessKey(
                processDefinitionId, datasource.getMasterTableId(), id, taskVariableData);
        return ResponseResult.success();
    }

    /**
     * 根据指定流程的主版本，发起一个流程实例，同时作为第一个任务节点的执行人，执行第一个用户任务。
     * 该接口无需数据权限过滤，因此用DisableDataFilter注解标注。如果当前系统没有支持数据权限过滤，该注解不会有任何影响。
     *
     * @param processDefinitionKey 流程定义标识。
     * @param flowTaskCommentDto   审批意见。
     * @param taskVariableData     流程任务变量数据。
     * @param masterData           流程审批相关的主表数据。
     * @param slaveData            流程审批相关的多个从表数据。
     * @param copyData             传阅数据，格式为type和id，type的值参考FlowConstant中的常量值。
     * @return 应答结果对象。
     */
    @DisableDataFilter
    @SaTokenDenyAuth
    @OperationLog(type = SysOperationLogType.START_FLOW)
    @PostMapping("/startAndTakeUserTask/{processDefinitionKey}")
    public ResponseResult<Void> startAndTakeUserTask(
            @PathVariable("processDefinitionKey") String processDefinitionKey,
            @MyRequestBody(required = true) FlowTaskCommentDto flowTaskCommentDto,
            @MyRequestBody JSONObject taskVariableData,
            @MyRequestBody(required = true) JSONObject masterData,
            @MyRequestBody JSONObject slaveData,
            @MyRequestBody JSONObject copyData) {
        return this.startAndTake(
                processDefinitionKey, flowTaskCommentDto, taskVariableData, masterData, slaveData, copyData);
    }

    /**
     * 启动流程并创建工单，同时将当前录入的数据存入草稿。
     *
     * @param processDefinitionKey 流程定义标识。
     * @param processInstanceId    流程实例Id。第一次保存时，该值为null。
     * @param masterData           流程审批相关的主表数据。
     * @param slaveData            流程审批相关的多个从表数据。
     * @return 应答结果对象，草稿的待办任务对象。
     */
    @DisableDataFilter
    @SaTokenDenyAuth
    @PostMapping("/startAndSaveDraft/{processDefinitionKey}")
    public ResponseResult<FlowTaskVo> startAndSaveDraft(
            @PathVariable("processDefinitionKey") String processDefinitionKey,
            @MyRequestBody String processInstanceId,
            @MyRequestBody JSONObject masterData,
            @MyRequestBody JSONObject slaveData) {
        String errorMessage;
        if (MapUtil.isEmpty(masterData) && MapUtil.isEmpty(slaveData)) {
            errorMessage = "数据验证失败，业务数据不能全部为空！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        ResponseResult<Tuple2<FlowEntryPublish, OnlineDatasource>> verifyResult =
                this.verifyAndGetFlowEntryPublishAndDatasource(processDefinitionKey, true);
        if (!verifyResult.isSuccess()) {
            return ResponseResult.errorFrom(verifyResult);
        }
        FlowEntryPublish flowEntryPublish = verifyResult.getData().getFirst();
        OnlineTable masterTable = verifyResult.getData().getSecond().getMasterTable();
        // 自动填充创建人数据。
        for (OnlineColumn column : masterTable.getColumnMap().values()) {
            if (ObjectUtil.equals(column.getFieldKind(), FieldKind.CREATE_USER_ID)) {
                masterData.put(column.getColumnName(), TokenData.takeFromRequest().getUserId());
            } else if (ObjectUtil.equals(column.getFieldKind(), FieldKind.CREATE_DEPT_ID)) {
                masterData.put(column.getColumnName(), TokenData.takeFromRequest().getDeptId());
            }
        }
        FlowWorkOrder flowWorkOrder;
        if (processInstanceId == null) {
            flowWorkOrder = flowOnlineOperationService.saveNewDraftAndStartProcess(
                    flowEntryPublish.getProcessDefinitionId(), masterTable.getTableId(), masterData, slaveData);
        } else {
            ResponseResult<FlowWorkOrder> flowWorkOrderResult =
                    flowOperationHelper.verifyAndGetFlowWorkOrderWithDraft(processDefinitionKey, processInstanceId);
            if (!flowWorkOrderResult.isSuccess()) {
                return ResponseResult.errorFrom(flowWorkOrderResult);
            }
            flowWorkOrder = flowWorkOrderResult.getData();
            flowWorkOrderService.updateDraft(flowWorkOrderResult.getData().getWorkOrderId(),
                    JSON.toJSONString(masterData), JSON.toJSONString(slaveData));
        }
        List<Task> taskList = flowApiService.getProcessInstanceActiveTaskList(flowWorkOrder.getProcessInstanceId());
        List<FlowTaskVo> flowTaskVoList = flowApiService.convertToFlowTaskList(taskList);
        return ResponseResult.success(flowTaskVoList.get(0));
    }

    /**
     * 提交流程的用户任务。
     * 该接口无需数据权限过滤，因此用DisableDataFilter注解标注。如果当前系统没有支持数据权限过滤，该注解不会有任何影响。
     *
     * @param processInstanceId  流程实例Id。
     * @param taskId             流程任务Id。
     * @param flowTaskCommentDto 流程审批数据。
     * @param taskVariableData   流程任务变量数据。
     * @param masterData         流程审批相关的主表数据。
     * @param slaveData          流程审批相关的多个从表数据。
     * @param copyData           传阅数据，格式为type和id，type的值参考FlowConstant中的常量值。
     * @return 应答结果对象。
     */
    @DisableDataFilter
    @OperationLog(type = SysOperationLogType.SUBMIT_TASK)
    @PostMapping("/submitUserTask")
    public ResponseResult<Void> submitUserTask(
            @MyRequestBody(required = true) String processInstanceId,
            @MyRequestBody(required = true) String taskId,
            @MyRequestBody(required = true) FlowTaskCommentDto flowTaskCommentDto,
            @MyRequestBody JSONObject taskVariableData,
            @MyRequestBody JSONObject masterData,
            @MyRequestBody JSONObject slaveData,
            @MyRequestBody JSONObject copyData) {
        String errorMessage;
        // 验证流程任务的合法性。
        Task task = flowApiService.getProcessInstanceActiveTask(processInstanceId, taskId);
        ResponseResult<TaskInfoVo> taskInfoResult = flowOperationHelper.verifyAndGetRuntimeTaskInfo(task);
        if (!taskInfoResult.isSuccess()) {
            return ResponseResult.errorFrom(taskInfoResult);
        }
        TaskInfoVo taskInfo = taskInfoResult.getData();
        // 验证在线表单及其关联数据源的合法性。
        ResponseResult<OnlineDatasource> datasourceResult = this.verifyAndGetOnlineDatasource(taskInfo.getFormId());
        if (!datasourceResult.isSuccess()) {
            return ResponseResult.errorFrom(datasourceResult);
        }
        CallResult assigneeVerifyResult = flowApiService.verifyAssigneeOrCandidateAndClaim(task);
        if (!assigneeVerifyResult.isSuccess()) {
            return ResponseResult.errorFrom(assigneeVerifyResult);
        }
        OnlineDatasource datasource = datasourceResult.getData();
        ProcessInstance instance = flowApiService.getProcessInstance(processInstanceId);
        String dataId = instance.getBusinessKey();
        // 这里把传阅数据放到任务变量中，是为了避免给流程数据操作方法增加额外的方法调用参数。
        if (MapUtil.isNotEmpty(copyData)) {
            if (taskVariableData == null) {
                taskVariableData = new JSONObject();
            }
            taskVariableData.put(FlowConstant.COPY_DATA_KEY, copyData);
        }
        onlineOperationHelper.enableOnlineExtendExecutor(datasource.getDatasourceId());
        FlowTaskComment flowTaskComment = BeanUtil.copyProperties(flowTaskCommentDto, FlowTaskComment.class);
        if (StrUtil.isBlank(dataId)) {
            return this.submitNewTask(processInstanceId, taskId,
                    flowTaskComment, taskVariableData, datasource, masterData, slaveData);
        }
        try {
            if (StrUtil.equals(flowTaskComment.getApprovalType(), FlowApprovalType.TRANSFER)
                    && StrUtil.isBlank(flowTaskComment.getDelegateAssignee())) {
                errorMessage = "数据验证失败，加签或转办任务指派人不能为空！！";
                return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
            }
            // 如果本次请求中包含从表数据，则一同插入。
            ResponseResult<Map<OnlineDatasourceRelation, List<JSONObject>>> slaveDataListResult =
                    onlineOperationHelper.buildSlaveDataList(datasource.getDatasourceId(), slaveData);
            if (!slaveDataListResult.isSuccess()) {
                return ResponseResult.errorFrom(slaveDataListResult);
            }
            flowOnlineOperationService.updateAndTakeTask(
                    task, flowTaskComment, taskVariableData, datasource, masterData, dataId, slaveDataListResult.getData());
        } catch (FlowOperationException e) {
            log.error("Failed to call [FlowOnlineOperationService.updateAndTakeTask]", e);
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, e.getMessage());
        }
        return ResponseResult.success();
    }

    /**
     * 查看指定流程实例的草稿数据。
     * NOTE: 白名单接口。
     *
     * @param processDefinitionKey 流程定义标识。
     * @param processInstanceId    流程实例Id。
     * @return 流程实例的草稿数据。
     */
    @DisableDataFilter
    @GetMapping("/viewDraftData")
    public ResponseResult<JSONObject> viewDraftData(
            @RequestParam String processDefinitionKey, @RequestParam String processInstanceId) {
        String errorMessage;
        ResponseResult<FlowWorkOrder> flowWorkOrderResult =
                flowOperationHelper.verifyAndGetFlowWorkOrderWithDraft(processDefinitionKey, processInstanceId);
        if (!flowWorkOrderResult.isSuccess()) {
            return ResponseResult.errorFrom(flowWorkOrderResult);
        }
        FlowWorkOrder flowWorkOrder = flowWorkOrderResult.getData();
        if (flowWorkOrder.getOnlineTableId() == null) {
            errorMessage = "数据验证失败，当前工单不是在线表单工单！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        FlowWorkOrderExt flowWorkOrderExt =
                flowWorkOrderService.getFlowWorkOrderExtByWorkOrderId(flowWorkOrder.getWorkOrderId());
        if (StrUtil.isBlank(flowWorkOrderExt.getDraftData())) {
            return ResponseResult.success(null);
        }
        Long tableId = flowWorkOrder.getOnlineTableId();
        OnlineTable masterTable = onlineTableService.getOnlineTableFromCache(tableId);
        JSONObject draftData = JSON.parseObject(flowWorkOrderExt.getDraftData());
        JSONObject masterData = draftData.getJSONObject(FlowConstant.MASTER_DATA_KEY);
        JSONObject slaveData = draftData.getJSONObject(FlowConstant.SLAVE_DATA_KEY);
        OnlineDatasource datasource =
                onlineDatasourceService.getOnlineDatasourceByMasterTableId(tableId);
        List<OnlineDatasourceRelation> slaveRelationList = null;
        if (slaveData != null) {
            ResponseResult<List<OnlineDatasourceRelation>> relationListResult =
                    onlineOperationHelper.verifyAndGetRelationList(datasource.getDatasourceId(), null);
            if (!relationListResult.isSuccess()) {
                return ResponseResult.errorFrom(relationListResult);
            }
            slaveRelationList = relationListResult.getData();
        }
        datasource.setMasterTable(masterTable);
        JSONObject jsonData = this.buildDraftData(datasource, masterData, slaveRelationList, slaveData);
        return ResponseResult.success(jsonData);
    }

    /**
     * 获取当前流程实例的详情数据。包括主表数据、一对一从表数据、一对多从表数据列表等。
     * 该接口无需数据权限过滤，因此用DisableDataFilter注解标注。如果当前系统没有支持数据权限过滤，该注解不会有任何影响。
     *
     * @param processInstanceId 当前运行时的流程实例Id。
     * @param taskId            流程任务Id。
     * @return 当前流程实例的详情数据。
     */
    @DisableDataFilter
    @GetMapping("/viewUserTask")
    public ResponseResult<JSONObject> viewUserTask(
            @RequestParam String processInstanceId, @RequestParam String taskId) {
        // 验证流程任务的合法性。
        Task task = flowApiService.getProcessInstanceActiveTask(processInstanceId, taskId);
        ProcessInstance instance = flowApiService.getProcessInstance(processInstanceId);
        // 如果业务主数据为空，则直接返回。
        if (StrUtil.isBlank(instance.getBusinessKey())) {
            return ResponseResult.success(null);
        }
        ResponseResult<TaskInfoVo> taskInfoResult = flowOperationHelper.verifyAndGetRuntimeTaskInfo(task);
        if (!taskInfoResult.isSuccess()) {
            return ResponseResult.errorFrom(taskInfoResult);
        }
        TaskInfoVo taskInfo = taskInfoResult.getData();
        // 验证在线表单及其关联数据源的合法性。
        ResponseResult<OnlineDatasource> datasourceResult = this.verifyAndGetOnlineDatasource(taskInfo.getFormId());
        if (!datasourceResult.isSuccess()) {
            return ResponseResult.errorFrom(datasourceResult);
        }
        ResponseResult<List<OnlineDatasourceRelation>> relationListResult =
                onlineOperationHelper.verifyAndGetRelationList(datasourceResult.getData().getDatasourceId(), null);
        if (!relationListResult.isSuccess()) {
            return ResponseResult.errorFrom(relationListResult);
        }
        JSONObject jsonData = this.buildUserTaskData(
                instance.getBusinessKey(), datasourceResult.getData(), relationListResult.getData());
        return ResponseResult.success(jsonData);
    }

    /**
     * 获取已经结束的流程实例的详情数据。包括主表数据、一对一从表数据、一对多从表数据列表等。
     * 该接口无需数据权限过滤，因此用DisableDataFilter注解标注。如果当前系统没有支持数据权限过滤，该注解不会有任何影响。
     *
     * @param processInstanceId 历史流程实例Id。
     * @param taskId            历史任务Id。如果该值为null，仅有发起人可以查看当前流程数据，否则只有任务的指派人才能查看。
     * @return 历史流程实例的详情数据。
     */
    @DisableDataFilter
    @GetMapping("/viewHistoricProcessInstance")
    public ResponseResult<JSONObject> viewHistoricProcessInstance(
            @RequestParam String processInstanceId, @RequestParam(required = false) String taskId) {
        // 验证流程实例的合法性。
        ResponseResult<HistoricProcessInstance> verifyResult =
                flowOperationHelper.verifyAndGetHistoricProcessInstance(processInstanceId, taskId);
        if (!verifyResult.isSuccess()) {
            return ResponseResult.errorFrom(verifyResult);
        }
        HistoricProcessInstance instance = verifyResult.getData();
        if (StrUtil.isBlank(instance.getBusinessKey())) {
            // 对于没有提交过任何用户任务的场景，可直接返回空数据。
            return ResponseResult.success(new JSONObject());
        }
        FlowEntryPublish flowEntryPublish =
                flowEntryService.getFlowEntryPublishList(CollUtil.newHashSet(instance.getProcessDefinitionId())).get(0);
        TaskInfoVo taskInfoVo = JSON.parseObject(flowEntryPublish.getInitTaskInfo(), TaskInfoVo.class);
        // 验证在线表单及其关联数据源的合法性。
        ResponseResult<OnlineDatasource> datasourceResult = this.verifyAndGetOnlineDatasource(taskInfoVo.getFormId());
        if (!datasourceResult.isSuccess()) {
            return ResponseResult.errorFrom(datasourceResult);
        }
        ResponseResult<List<OnlineDatasourceRelation>> relationListResult =
                onlineOperationHelper.verifyAndGetRelationList(datasourceResult.getData().getDatasourceId(), null);
        if (!relationListResult.isSuccess()) {
            return ResponseResult.errorFrom(relationListResult);
        }
        JSONObject jsonData = this.buildUserTaskData(
                instance.getBusinessKey(), datasourceResult.getData(), relationListResult.getData());
        return ResponseResult.success(jsonData);
    }

    /**
     * 根据消息Id，获取流程Id关联的业务数据。
     * NOTE：白名单接口。
     *
     * @param messageId 抄送消息Id。
     * @return 抄送消息关联的流程实例业务数据。
     */
    @DisableDataFilter
    @GetMapping("/viewCopyBusinessData")
    public ResponseResult<JSONObject> viewCopyBusinessData(@RequestParam Long messageId) {
        String errorMessage;
        // 验证流程任务的合法性。
        FlowMessage flowMessage = flowMessageService.getById(messageId);
        if (flowMessage == null) {
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
        }
        if (flowMessage.getMessageType() != FlowMessageType.COPY_TYPE) {
            errorMessage = "数据验证失败，当前消息不是抄送类型消息！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        if (flowMessage.getOnlineFormData() == null || !flowMessage.getOnlineFormData()) {
            errorMessage = "数据验证失败，当前消息为静态路由表单数据，不能通过该接口获取！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        if (!flowMessageService.isCandidateIdentityOnMessage(messageId)) {
            errorMessage = "数据验证失败，当前用户没有权限访问该消息！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        HistoricProcessInstance instance =
                flowApiService.getHistoricProcessInstance(flowMessage.getProcessInstanceId());
        // 如果业务主数据为空，则直接返回。
        if (StrUtil.isBlank(instance.getBusinessKey())) {
            errorMessage = "数据验证失败，当前消息为所属流程实例没有包含业务主键Id！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        Long formId = Long.valueOf(flowMessage.getBusinessDataShot());
        // 验证在线表单及其关联数据源的合法性。
        ResponseResult<OnlineDatasource> datasourceResult = this.verifyAndGetOnlineDatasource(formId);
        if (!datasourceResult.isSuccess()) {
            return ResponseResult.errorFrom(datasourceResult);
        }
        OnlineDatasource datasource = datasourceResult.getData();
        ResponseResult<List<OnlineDatasourceRelation>> relationListResult =
                onlineOperationHelper.verifyAndGetRelationList(datasource.getDatasourceId(), null);
        if (!relationListResult.isSuccess()) {
            return ResponseResult.errorFrom(relationListResult);
        }
        JSONObject jsonData = this.buildUserTaskData(
                instance.getBusinessKey(), datasource, relationListResult.getData());
        // 将当前消息更新为已读
        flowMessageService.readCopyTask(messageId);
        return ResponseResult.success(jsonData);
    }

    /**
     * 修复业务数据，目前仅在线表单工作流支持的跨库业务数据主动修复接口 。
     *
     * @param processInstanceId 流程实例Id。
     * @param transId           流水号Id。
     * @return 操作应答结果。
     */
    @SaCheckPermission("flowOperation.all")
    @OperationLog(type = SysOperationLogType.FIX_FLOW_BUSINESS_DATA)
    @PostMapping("/fixBusinessData")
    public ResponseResult<Void> fixBusinessData(
            @MyRequestBody(required = true) String processInstanceId,
            @MyRequestBody(required = true) Long transId) {
        String errorMessage;
        FlowTransProducer transProducer = flowTransProducerService.getById(transId);
        if (transProducer == null) {
            errorMessage = "数据操作失败，该数据修复流水号Id不存在！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        if (!StrUtil.equals(processInstanceId, transProducer.getProcessInstanceId())) {
            errorMessage = "数据操作失败，该数据修复流水号Id与流程实例Id不匹配！";
            return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST, errorMessage);
        }
        flowOnlineOperationService.fixBusinessData(transProducer);
        flowTransProducerService.removeById(transProducer.getTransId());
        return ResponseResult.success();
    }

    /**
     * 工作流工单列表。
     *
     * @param processDefinitionKey   流程标识名。
     * @param flowWorkOrderDtoFilter 过滤对象。
     * @param filterDtoList          业务表过滤参数列表。
     * @param pageParam              分页参数。
     * @param ignoreMaskFields       忽略脱敏的字段名集合，多个字段之间逗号分隔。
     * @return 查询结果。
     */
    @SaTokenDenyAuth
    @PostMapping("/listWorkOrder/{processDefinitionKey}")
    public ResponseResult<MyPageData<FlowWorkOrderVo>> listWorkOrder(
            @PathVariable("processDefinitionKey") String processDefinitionKey,
            @MyRequestBody FlowWorkOrderDto flowWorkOrderDtoFilter,
            @MyRequestBody List<OnlineFilterDto> filterDtoList,
            @MyRequestBody MyPageParam pageParam,
            @MyRequestBody String ignoreMaskFields) {
        FlowWorkOrder filter = flowOperationHelper.makeWorkOrderFilter(flowWorkOrderDtoFilter, processDefinitionKey);
        String orderBy = this.makeDefaultOrderBy();
        MyPageData<FlowWorkOrderVo> resultData;
        if (CollUtil.isNotEmpty(filterDtoList)) {
            List<String> dataIdList = this.extractBusinessKeys(processDefinitionKey, filterDtoList);
            if (CollUtil.isEmpty(dataIdList)) {
                return ResponseResult.success(MyPageData.emptyPageData());
            }
            resultData = flowWorkOrderService.getFlowWorkOrderListWithDataIds(filter, dataIdList, pageParam, orderBy);
        } else {
            if (pageParam != null) {
                PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize(), pageParam.getCount());
            }
            List<FlowWorkOrder> flowWorkOrderList = flowWorkOrderService.getFlowWorkOrderList(filter, orderBy);
            resultData = MyPageUtil.makeResponseData(flowWorkOrderList, FlowWorkOrderVo.class);
        }
        flowOperationHelper.buildWorkOrderApprovalStatus(processDefinitionKey, resultData.getDataList());
        // 根据工单的提交用户名获取用户的显示名称，便于前端显示。
        // 同时这也是一个如何通过插件方法，将loginName映射到showName的示例，
        flowWorkOrderService.fillUserShowNameByLoginName(resultData.getDataList());
        // 工单自身的查询中可以受到数据权限的过滤，但是工单集成业务数据时，则无需再对业务数据进行数据权限过滤了。
        GlobalThreadLocal.setDataFilter(false);
        ResponseResult<Void> responseResult = this.makeWorkOrderTaskInfo(resultData.getDataList(), ignoreMaskFields);
        if (!responseResult.isSuccess()) {
            return ResponseResult.errorFrom(responseResult);
        }
        return ResponseResult.success(resultData);
    }

    /**
     * 为数据源主表字段上传文件。
     *
     * @param processDefinitionKey 流程引擎流程定义标识。
     * @param processInstanceId    流程实例Id。
     * @param taskId               流程任务Id。
     * @param datasourceId         数据源Id。
     * @param relationId           数据源关联Id。
     * @param fieldName            数据表字段名。
     * @param asImage              是否为图片文件。
     * @param uploadFile           上传文件对象。
     */
    @DisableDataFilter
    @OperationLog(type = SysOperationLogType.UPLOAD, saveResponse = false)
    @PostMapping("/upload")
    public void upload(
            @RequestParam String processDefinitionKey,
            @RequestParam(required = false) String processInstanceId,
            @RequestParam(required = false) String taskId,
            @RequestParam Long datasourceId,
            @RequestParam(required = false) Long relationId,
            @RequestParam String fieldName,
            @RequestParam Boolean asImage,
            @RequestParam("uploadFile") MultipartFile uploadFile) throws IOException {
        ResponseResult<String> verifyResult =
                this.verifyUploadOrDownload(processDefinitionKey, processInstanceId, taskId, datasourceId);
        if (!verifyResult.isSuccess()) {
            ResponseResult.output(HttpServletResponse.SC_FORBIDDEN, ResponseResult.errorFrom(verifyResult));
            return;
        }
        ResponseResult<OnlineTable> verifyTableResult =
                this.verifyAndGetOnlineTable(datasourceId, relationId, null, null);
        if (!verifyTableResult.isSuccess()) {
            ResponseResult.output(HttpServletResponse.SC_FORBIDDEN, ResponseResult.errorFrom(verifyTableResult));
            return;
        }
        onlineOperationHelper.doUpload(verifyTableResult.getData(), fieldName, asImage, uploadFile);
    }

    /**
     * 下载文件接口。
     * 越权访问限制说明：
     * taskId为空，当前用户必须为当前流程的发起人，否则必须为当前任务的指派人或候选人。
     * relationId为空，下载数据为主表字段，否则为关联的从表字段。
     * 该接口无需数据权限过滤，因此用DisableDataFilter注解标注。如果当前系统没有支持数据权限过滤，该注解不会有任何影响。
     *
     * @param processDefinitionKey 流程引擎流程定义标识。
     * @param processInstanceId    流程实例Id。
     * @param taskId               流程任务Id。
     * @param datasourceId         数据源Id。
     * @param relationId           数据源关联Id。
     * @param dataId               附件所在记录的主键Id。
     * @param fieldName            数据表字段名。
     * @param asImage              是否为图片文件。
     * @param response             Http 应答对象。
     */
    @DisableDataFilter
    @OperationLog(type = SysOperationLogType.DOWNLOAD, saveResponse = false)
    @GetMapping("/download")
    public void download(
            @RequestParam String processDefinitionKey,
            @RequestParam(required = false) String processInstanceId,
            @RequestParam(required = false) String taskId,
            @RequestParam Long datasourceId,
            @RequestParam(required = false) Long relationId,
            @RequestParam(required = false) String dataId,
            @RequestParam String fieldName,
            @RequestParam String filename,
            @RequestParam Boolean asImage,
            HttpServletResponse response) throws IOException {
        ResponseResult<String> verifyResult =
                this.verifyUploadOrDownload(processDefinitionKey, processInstanceId, taskId, datasourceId);
        if (!verifyResult.isSuccess()) {
            ResponseResult.output(HttpServletResponse.SC_FORBIDDEN, ResponseResult.errorFrom(verifyResult));
            return;
        }
        ResponseResult<OnlineTable> verifyTableResult =
                this.verifyAndGetOnlineTable(datasourceId, relationId, verifyResult.getData(), dataId);
        if (!verifyTableResult.isSuccess()) {
            ResponseResult.output(HttpServletResponse.SC_FORBIDDEN, ResponseResult.errorFrom(verifyTableResult));
            return;
        }
        onlineOperationHelper.doDownload(verifyTableResult.getData(), dataId, fieldName, filename, asImage, response);
    }

    /**
     * 获取所有流程对象，同时获取关联的在线表单对象列表。
     *
     * @return 查询结果。
     */
    @GetMapping("/listFlowEntryForm")
    public ResponseResult<List<FlowEntryVo>> listFlowEntryForm() {
        List<FlowEntry> flowEntryList = flowEntryService.getFlowEntryList(null, null);
        List<FlowEntryVo> flowEntryVoList = MyModelUtil.copyCollectionTo(flowEntryList, FlowEntryVo.class);
        if (CollUtil.isNotEmpty(flowEntryVoList)) {
            Set<Long> pageIdSet = flowEntryVoList.stream().map(FlowEntryVo::getPageId).collect(Collectors.toSet());
            List<OnlineForm> formList = onlineFormService.getOnlineFormListByPageIds(pageIdSet);
            formList.forEach(f -> f.setWidgetJson(null));
            Map<Long, List<OnlineForm>> formMap =
                    formList.stream().collect(Collectors.groupingBy(OnlineForm::getPageId));
            for (FlowEntryVo flowEntryVo : flowEntryVoList) {
                List<OnlineForm> flowEntryFormList = formMap.get(flowEntryVo.getPageId());
                flowEntryVo.setFormList(MyModelUtil.beanToMapList(flowEntryFormList));
            }
        }
        return ResponseResult.success(flowEntryVoList);
    }

    /**
     * 获取在线表单工作流Id所关联的权限数据，包括权限字列表和权限资源列表。
     * 注：该接口仅用于微服务间调用使用，无需对前端开放。
     *
     * @param onlineFlowEntryIds 在线表单工作流Id集合。
     * @return 参数中在线表单工作流Id集合所关联的权限数据。
     */
    @GetMapping("/calculatePermData")
    public ResponseResult<List<Map<String, Object>>> calculatePermData(@RequestParam Set<Long> onlineFlowEntryIds) {
        return ResponseResult.success(flowOnlineOperationService.calculatePermData(onlineFlowEntryIds));
    }

    private ResponseResult<Void> startAndTake(
            String processDefinitionKey,
            FlowTaskCommentDto flowTaskCommentDto,
            JSONObject taskVariableData,
            JSONObject masterData,
            JSONObject slaveData,
            JSONObject copyData) {
        ResponseResult<Tuple2<FlowEntryPublish, OnlineDatasource>> verifyResult =
                this.verifyAndGetFlowEntryPublishAndDatasource(processDefinitionKey, true);
        if (!verifyResult.isSuccess()) {
            return ResponseResult.errorFrom(verifyResult);
        }
        FlowEntryPublish flowEntryPublish = verifyResult.getData().getFirst();
        OnlineDatasource datasource = verifyResult.getData().getSecond();
        OnlineTable masterTable = datasource.getMasterTable();
        // 这里把传阅数据放到任务变量中，是为了避免给流程数据操作方法增加额外的方法调用参数。
        if (MapUtil.isNotEmpty(copyData)) {
            if (taskVariableData == null) {
                taskVariableData = new JSONObject();
            }
            taskVariableData.put(FlowConstant.COPY_DATA_KEY, copyData);
        }
        FlowTaskComment flowTaskComment = BeanUtil.copyProperties(flowTaskCommentDto, FlowTaskComment.class);
        onlineOperationHelper.enableOnlineExtendExecutor(datasource.getDatasourceId());
        // 保存在线表单提交的数据，同时启动流程和自动完成第一个用户任务。
        if (slaveData == null) {
            flowOnlineOperationService.saveNewAndStartProcess(
                    flowEntryPublish.getProcessDefinitionId(),
                    flowTaskComment,
                    taskVariableData,
                    masterTable,
                    masterData);
        } else {
            // 如果本次请求中包含从表数据，则一同插入。
            ResponseResult<Map<OnlineDatasourceRelation, List<JSONObject>>> slaveDataListResult =
                    onlineOperationHelper.buildSlaveDataList(datasource.getDatasourceId(), slaveData);
            if (!slaveDataListResult.isSuccess()) {
                return ResponseResult.errorFrom(slaveDataListResult);
            }
            flowOnlineOperationService.saveNewAndStartProcess(
                    flowEntryPublish.getProcessDefinitionId(),
                    flowTaskComment,
                    taskVariableData,
                    masterTable,
                    masterData,
                    slaveDataListResult.getData());
        }
        return ResponseResult.success();
    }

    private ResponseResult<OnlineDatasource> verifyAndGetOnlineDatasource(Long formId) {
        List<OnlineFormDatasource> formDatasourceList = onlineFormService.getFormDatasourceListFromCache(formId);
        if (CollUtil.isEmpty(formDatasourceList)) {
            String errorMessage = "数据验证失败，流程任务绑定的在线表单Id [" + formId + "] 不存在，请修改流程图！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        return onlineOperationHelper.verifyAndGetDatasource(formDatasourceList.get(0).getDatasourceId());
    }

    private ResponseResult<Tuple2<FlowEntryPublish, OnlineDatasource>> verifyAndGetFlowEntryPublishAndDatasource(
            String processDefinitionKey, boolean checkStarter) {
        String errorMessage;
        // 1. 验证流程数据的合法性。
        ResponseResult<FlowEntry> flowEntryResult = flowOperationHelper.verifyAndGetFlowEntry(processDefinitionKey);
        if (!flowEntryResult.isSuccess()) {
            return ResponseResult.errorFrom(flowEntryResult);
        }
        // 2. 验证流程一个用户任务的合法性。
        FlowEntryPublish flowEntryPublish = flowEntryResult.getData().getMainFlowEntryPublish();
        if (BooleanUtil.isFalse(flowEntryPublish.getActiveStatus())) {
            errorMessage = "数据验证失败，当前流程发布对象已被挂起，不能启动新流程！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        ResponseResult<TaskInfoVo> taskInfoResult =
                flowOperationHelper.verifyAndGetInitialTaskInfo(flowEntryPublish, checkStarter);
        if (!taskInfoResult.isSuccess()) {
            return ResponseResult.errorFrom(taskInfoResult);
        }
        TaskInfoVo taskInfo = taskInfoResult.getData();
        // 3. 验证在线表单及其关联数据源的合法性。
        ResponseResult<OnlineDatasource> datasourceResult = this.verifyAndGetOnlineDatasource(taskInfo.getFormId());
        if (!datasourceResult.isSuccess()) {
            return ResponseResult.errorFrom(datasourceResult);
        }
        return ResponseResult.success(new Tuple2<>(flowEntryPublish, datasourceResult.getData()));
    }

    private ResponseResult<OnlineTable> verifyAndGetOnlineTable(
            Long datasourceId, Long relationId, String businessKey, String dataId) {
        ResponseResult<OnlineDatasource> datasourceResult =
                onlineOperationHelper.verifyAndGetDatasource(datasourceId);
        if (!datasourceResult.isSuccess()) {
            return ResponseResult.errorFrom(datasourceResult);
        }
        OnlineTable masterTable = datasourceResult.getData().getMasterTable();
        OnlineTable table = masterTable;
        ResponseResult<OnlineDatasourceRelation> relationResult = null;
        if (relationId != null) {
            relationResult = onlineOperationHelper.verifyAndGetRelation(datasourceId, relationId);
            if (!relationResult.isSuccess()) {
                return ResponseResult.errorFrom(relationResult);
            }
            table = relationResult.getData().getSlaveTable();
        }
        if (StrUtil.hasBlank(businessKey, dataId)) {
            return ResponseResult.success(table);
        }
        String errorMessage;
        // 如果relationId为null，这里就是主表数据。
        if (relationId == null) {
            if (!StrUtil.equals(businessKey, dataId)) {
                errorMessage = "数据验证失败，参数主键Id与流程主表主键Id不匹配！";
                return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
            }
            return ResponseResult.success(table);
        }
        OnlineDatasourceRelation relation = relationResult.getData();
        OnlineTable slaveTable = relation.getSlaveTable();
        Map<String, Object> dataMap =
                onlineOperationService.getMasterData(slaveTable, null, null, dataId);
        if (dataMap == null) {
            errorMessage = "数据验证失败，从表主键Id不存在！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        OnlineColumn slaveColumn = relation.getSlaveColumn();
        Object relationSlaveDataId = dataMap.get(slaveColumn.getColumnName());
        if (relationSlaveDataId == null) {
            errorMessage = "数据验证失败，当前关联的从表字段值为NULL！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        OnlineColumn masterColumn = masterTable.getColumnMap().get(relation.getMasterColumnId());
        if (BooleanUtil.isTrue(masterColumn.getPrimaryKey())
                && !StrUtil.equals(relationSlaveDataId.toString(), businessKey)) {
            errorMessage = "数据验证失败，当前从表主键Id关联的主表Id当前流程的BusinessKey不一致！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        Map<String, Object> masterDataMap =
                onlineOperationService.getMasterData(masterTable, null, null, businessKey);
        if (masterDataMap == null) {
            errorMessage = "数据验证失败，主表主键Id不存在！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        Object relationMasterDataId = masterDataMap.get(masterColumn.getColumnName());
        if (relationMasterDataId == null) {
            errorMessage = "数据验证失败，当前关联的主表字段值为NULL！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        if (!StrUtil.equals(relationMasterDataId.toString(), relationSlaveDataId.toString())) {
            errorMessage = "数据验证失败，当前关联的主表字段值和从表字段值不一致！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        return ResponseResult.success(table);
    }

    private ResponseResult<String> verifyUploadOrDownload(
            String processDefinitionKey, String processInstanceId, String taskId, Long datasourceId) {
        if (!StrUtil.isAllBlank(processInstanceId, taskId)) {
            ResponseResult<Void> verifyResult =
                    flowOperationHelper.verifyUploadOrDownloadPermission(processInstanceId, taskId);
            if (!verifyResult.isSuccess()) {
                return ResponseResult.errorFrom(ResponseResult.errorFrom(verifyResult));
            }
        }
        String errorMessage;
        FlowEntry flowEntry = flowEntryService.getFlowEntryFromCache(processDefinitionKey);
        if (flowEntry == null) {
            errorMessage = "数据验证失败，指定流程Id不存在！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        String businessKey = null;
        if (processInstanceId != null) {
            HistoricProcessInstance instance = flowApiService.getHistoricProcessInstance(processInstanceId);
            if (!StrUtil.equals(flowEntry.getProcessDefinitionKey(), instance.getProcessDefinitionKey())) {
                errorMessage = "数据验证失败，指定流程实例并不属于当前流程！";
                return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
            }
            businessKey = instance.getBusinessKey();
        }
        List<OnlinePageDatasource> datasourceList =
                onlinePageService.getOnlinePageDatasourceListByPageId(flowEntry.getPageId());
        Optional<Long> r = datasourceList.stream()
                .map(OnlinePageDatasource::getDatasourceId).filter(c -> c.equals(datasourceId)).findFirst();
        if (!r.isPresent()) {
            errorMessage = "数据验证失败，当前数据源Id并不属于当前流程！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        return ResponseResult.success(businessKey);
    }

    private ResponseResult<Void> submitNewTask(
            String instanceId,
            String taskId,
            FlowTaskComment comment,
            JSONObject variableData,
            OnlineDatasource datasource,
            JSONObject masterData,
            JSONObject slaveData) {
        OnlineTable masterTable = datasource.getMasterTable();
        // 保存在线表单提交的数据，同时启动流程和自动完成第一个用户任务。
        if (slaveData == null) {
            flowOnlineOperationService.saveNewAndTakeTask(
                    instanceId, taskId, comment, variableData, masterTable, masterData);
        } else {
            // 如果本次请求中包含从表数据，则一同插入。
            ResponseResult<Map<OnlineDatasourceRelation, List<JSONObject>>> slaveDataListResult =
                    onlineOperationHelper.buildSlaveDataList(datasource.getDatasourceId(), slaveData);
            if (!slaveDataListResult.isSuccess()) {
                return ResponseResult.errorFrom(slaveDataListResult);
            }
            flowOnlineOperationService.saveNewAndTakeTask(
                    instanceId, taskId, comment, variableData, masterTable, masterData, slaveDataListResult.getData());
        }
        return ResponseResult.success();
    }

    private JSONObject buildUserTaskData(
            String businessKey, OnlineDatasource datasource, List<OnlineDatasourceRelation> relationList) {
        onlineOperationHelper.enableOnlineExtendExecutor(datasource.getDatasourceId());
        OnlineTable masterTable = datasource.getMasterTable();
        JSONObject jsonData = new JSONObject();
        List<OnlineDatasourceRelation> oneToOneRelationList = relationList.stream()
                .filter(r -> r.getRelationType().equals(RelationType.ONE_TO_ONE)).collect(Collectors.toList());
        Map<String, Object> result =
                onlineOperationService.getMasterData(masterTable, oneToOneRelationList, relationList, businessKey);
        if (MapUtil.isEmpty(result)) {
            return jsonData;
        }
        jsonData.put(datasource.getVariableName(), result);
        List<OnlineDatasourceRelation> oneToManyRelationList = relationList.stream()
                .filter(r -> r.getRelationType().equals(RelationType.ONE_TO_MANY)).collect(Collectors.toList());
        if (CollUtil.isEmpty(oneToManyRelationList)) {
            return jsonData;
        }
        for (OnlineDatasourceRelation relation : oneToManyRelationList) {
            OnlineFilterDto filterDto = new OnlineFilterDto();
            filterDto.setTableName(relation.getSlaveTable().getTableName());
            OnlineColumn slaveColumn = relation.getSlaveTable().getColumnMap().get(relation.getSlaveColumnId());
            filterDto.setColumnName(slaveColumn.getColumnName());
            filterDto.setFilterType(FieldFilterType.EQUAL_FILTER);
            OnlineColumn masterColumn = masterTable.getColumnMap().get(relation.getMasterColumnId());
            Object columnValue = result.get(masterColumn.getColumnName());
            filterDto.setColumnValue(columnValue);
            MyPageData<Map<String, Object>> pageData = onlineOperationService.getSlaveDataList(
                    relation, CollUtil.newLinkedList(filterDto), null, null);
            if (CollUtil.isNotEmpty(pageData.getDataList())) {
                result.put(relation.getVariableName() + ONE_TO_MANY_VAR_SUFFIX, pageData.getDataList());
            }
        }
        return jsonData;
    }

    private JSONObject buildDraftData(
            OnlineDatasource datasource,
            JSONObject masterData,
            List<OnlineDatasourceRelation> relationList,
            JSONObject slaveData) {
        OnlineTable masterTable = datasource.getMasterTable();
        JSONObject jsonData = new JSONObject();
        JSONObject normalizedMasterData = new JSONObject();
        Map<String, OnlineColumn> columnNameAndColumnMap = masterTable.getColumnMap()
                .values().stream().collect(Collectors.toMap(OnlineColumn::getColumnName, c -> c));
        if (masterData != null) {
            for (Map.Entry<String, Object> entry : masterData.entrySet()) {
                OnlineColumn column = columnNameAndColumnMap.get(entry.getKey());
                Object v = onlineOperationHelper.convertToTypeValue(column, entry.getValue().toString());
                normalizedMasterData.put(entry.getKey(), v);
            }
        }
        if (slaveData != null && relationList != null) {
            Map<Long, OnlineDatasourceRelation> relationMap =
                    relationList.stream().collect(Collectors.toMap(OnlineDatasourceRelation::getRelationId, c -> c));
            for (Map.Entry<String, Object> entry : slaveData.entrySet()) {
                OnlineDatasourceRelation relation = relationMap.get(Long.valueOf(entry.getKey()));
                if (relation != null) {
                    this.buildRelationDraftData(relation, entry.getValue(), normalizedMasterData);
                }
            }
        }
        jsonData.put(datasource.getVariableName(), normalizedMasterData);
        return jsonData;
    }

    private void buildRelationDraftData(OnlineDatasourceRelation relation, Object value, JSONObject masterData) {
        if (relation.getRelationType().equals(RelationType.ONE_TO_ONE)) {
            Map<String, OnlineColumn> slaveColumnNameAndColumnMap =
                    relation.getSlaveTable().getColumnMap().values()
                            .stream().collect(Collectors.toMap(OnlineColumn::getColumnName, c -> c));
            JSONObject slaveObject = (JSONObject) value;
            JSONObject normalizedSlaveObject = new JSONObject();
            for (Map.Entry<String, Object> entry2 : slaveObject.entrySet()) {
                OnlineColumn column = slaveColumnNameAndColumnMap.get(entry2.getKey());
                Object v = onlineOperationHelper.convertToTypeValue(column, entry2.getValue().toString());
                normalizedSlaveObject.put(entry2.getKey(), v);
            }
            masterData.put(relation.getVariableName(), normalizedSlaveObject);
        } else if (relation.getRelationType().equals(RelationType.ONE_TO_MANY)) {
            JSONArray slaveArray = (JSONArray) value;
            JSONArray normalizedSlaveArray = new JSONArray();
            for (int i = 0; i <= slaveArray.size() - 1; i++) {
                JSONObject slaveObject = slaveArray.getJSONObject(i);
                JSONObject normalizedSlaveObject = new JSONObject();
                normalizedSlaveObject.putAll(slaveObject);
                normalizedSlaveArray.add(normalizedSlaveObject);
            }
            masterData.put(relation.getVariableName(), normalizedSlaveArray);
        }
    }

    private String makeDefaultOrderBy() {
        MyOrderParam orderParam = new MyOrderParam();
        orderParam.add(new MyOrderParam.OrderInfo("workOrderId", false, null));
        return MyOrderParam.buildOrderBy(orderParam, FlowWorkOrder.class);
    }

    private List<String> extractBusinessKeys(String processDefinitionKey, List<OnlineFilterDto> filterDtoList) {
        OnlineTable masterTable = this.getFlowWorkMasterTable(processDefinitionKey);
        // 如果业务主表中没有任何数据，此处可以直接返回空列表了。
        if (masterTable == null) {
            return ListUtil.empty();
        }
        MyPageData<Map<String, Object>> pageData = onlineOperationService.getMasterDataList(
                masterTable, null, null, filterDtoList, null, null, null);
        if (CollUtil.isEmpty(pageData.getDataList())) {
            return ListUtil.empty();
        }
        String primaryKey = masterTable.getPrimaryKeyColumn().getObjectFieldName();
        return pageData.getDataList().stream()
                .map(data -> data.get(primaryKey).toString()).collect(Collectors.toList());
    }

    private OnlineTable getFlowWorkMasterTable(String processDefinitionKey) {
        FlowWorkOrder filter = new FlowWorkOrder();
        filter.setProcessDefinitionKey(processDefinitionKey);
        PageMethod.startPage(1, 1);
        List<FlowWorkOrder> workOrders = flowWorkOrderService.getFlowWorkOrderList(filter, null);
        if (CollUtil.isEmpty(workOrders)) {
            //当前流程工单没有任何数据。
            return null;
        }
        Long tableId = workOrders.get(0).getOnlineTableId();
        return onlineTableService.getOnlineTableFromCache(tableId);
    }

    private ResponseResult<Void> makeWorkOrderTaskInfo(List<FlowWorkOrderVo> flowWorkOrderVoList, String ignoreMaskFields) {
        if (CollUtil.isEmpty(flowWorkOrderVoList)) {
            return ResponseResult.success();
        }
        Set<String> definitionIdSet =
                flowWorkOrderVoList.stream().map(FlowWorkOrderVo::getProcessDefinitionId).collect(Collectors.toSet());
        List<FlowEntryPublish> flowEntryPublishList = flowEntryService.getFlowEntryPublishList(definitionIdSet);
        Map<String, FlowEntryPublish> flowEntryPublishMap =
                flowEntryPublishList.stream().collect(Collectors.toMap(FlowEntryPublish::getProcessDefinitionId, c -> c));
        for (FlowWorkOrderVo flowWorkOrderVo : flowWorkOrderVoList) {
            FlowEntryPublish flowEntryPublish = flowEntryPublishMap.get(flowWorkOrderVo.getProcessDefinitionId());
            flowWorkOrderVo.setInitTaskInfo(flowEntryPublish.getInitTaskInfo());
        }
        Long tableId = flowWorkOrderVoList.get(0).getOnlineTableId();
        OnlineTable masterTable = onlineTableService.getOnlineTableFromCache(tableId);
        ResponseResult<Void> responseResult = 
                this.buildWorkOrderMasterData(flowWorkOrderVoList, masterTable, ignoreMaskFields);
        if (!responseResult.isSuccess()) {
            return ResponseResult.errorFrom(responseResult);
        }
        responseResult = this.buildWorkOrderDraftData(flowWorkOrderVoList, masterTable);
        if (!responseResult.isSuccess()) {
            return ResponseResult.errorFrom(responseResult);
        }
        List<String> unfinishedProcessInstanceIds = flowWorkOrderVoList.stream()
                .filter(c -> !c.getFlowStatus().equals(FlowTaskStatus.FINISHED))
                .map(FlowWorkOrderVo::getProcessInstanceId)
                .collect(Collectors.toList());
        if (CollUtil.isEmpty(unfinishedProcessInstanceIds)) {
            return ResponseResult.success();
        }
        Map<String, List<Task>> taskMap =
                flowApiService.getTaskListByProcessInstanceIds(unfinishedProcessInstanceIds)
                        .stream().collect(Collectors.groupingBy(Task::getProcessInstanceId));
        for (FlowWorkOrderVo flowWorkOrderVo : flowWorkOrderVoList) {
            List<Task> instanceTaskList = taskMap.get(flowWorkOrderVo.getProcessInstanceId());
            if (instanceTaskList != null) {
                JSONArray taskArray = new JSONArray();
                for (Task task : instanceTaskList) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("taskId", task.getId());
                    jsonObject.put("taskName", task.getName());
                    jsonObject.put("taskKey", task.getTaskDefinitionKey());
                    jsonObject.put("assignee", task.getAssignee());
                    taskArray.add(jsonObject);
                }
                flowWorkOrderVo.setRuntimeTaskInfoList(taskArray);
            } else {
                List<String> currentActivityIds =
                        flowApiService.getCurrentActivityIds(flowWorkOrderVo.getProcessInstanceId());
                JSONArray taskArray = new JSONArray();
                for (String activityId : currentActivityIds) {
                    org.flowable.bpmn.model.Task task = flowApiService.getFlowElement(
                            flowWorkOrderVo.getProcessDefinitionId(), activityId, org.flowable.bpmn.model.Task.class);
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("taskName", task.getName());
                    jsonObject.put("taskKey", activityId);
                    taskArray.add(jsonObject);
                }
                flowWorkOrderVo.setRuntimeTaskInfoList(taskArray);
            }
        }
        return ResponseResult.success();
    }

    private ResponseResult<Void> buildWorkOrderDraftData(
            List<FlowWorkOrderVo> flowWorkOrderVoList, OnlineTable masterTable) {
        List<FlowWorkOrderVo> draftWorkOrderList = flowWorkOrderVoList.stream()
                .filter(c -> c.getFlowStatus().equals(FlowTaskStatus.DRAFT)).collect(Collectors.toList());
        if (CollUtil.isEmpty(draftWorkOrderList)) {
            return ResponseResult.success();
        }
        Set<Long> workOrderIdSet = draftWorkOrderList.stream()
                .map(FlowWorkOrderVo::getWorkOrderId).collect(Collectors.toSet());
        List<FlowWorkOrderExt> workOrderExtList =
                flowWorkOrderService.getFlowWorkOrderExtByWorkOrderIds(workOrderIdSet);
        Map<Long, FlowWorkOrderExt> workOrderExtMap = workOrderExtList.stream()
                .collect(Collectors.toMap(FlowWorkOrderExt::getWorkOrderId, c -> c));
        for (FlowWorkOrderVo workOrder : draftWorkOrderList) {
            FlowWorkOrderExt workOrderExt = workOrderExtMap.get(workOrder.getWorkOrderId());
            if (workOrderExt == null) {
                continue;
            }
            JSONObject draftData = JSON.parseObject(workOrderExt.getDraftData());
            JSONObject masterData = draftData.getJSONObject(FlowConstant.MASTER_DATA_KEY);
            JSONObject slaveData = draftData.getJSONObject(FlowConstant.SLAVE_DATA_KEY);
            OnlineDatasource datasource =
                    onlineDatasourceService.getOnlineDatasourceByMasterTableId(masterTable.getTableId());
            List<OnlineDatasourceRelation> slaveRelationList = null;
            if (slaveData != null) {
                ResponseResult<List<OnlineDatasourceRelation>> relationListResult =
                        onlineOperationHelper.verifyAndGetRelationList(datasource.getDatasourceId(), RelationType.ONE_TO_ONE);
                if (!relationListResult.isSuccess()) {
                    return ResponseResult.errorFrom(relationListResult);
                }
                slaveRelationList = relationListResult.getData();
            }
            datasource.setMasterTable(masterTable);
            JSONObject jsonData = this.buildDraftData(datasource, masterData, slaveRelationList, slaveData);
            JSONObject masterAndOneToOneData = jsonData.getJSONObject(datasource.getVariableName());
            if (MapUtil.isNotEmpty(masterAndOneToOneData)) {
                List<Map<String, Object>> dataList = new LinkedList<>();
                dataList.add(masterAndOneToOneData);
                onlineOperationService.buildDataListWithDict(masterTable, slaveRelationList, dataList);
            }
            workOrder.setMasterData(masterAndOneToOneData);
        }
        return ResponseResult.success();
    }

    private ResponseResult<Void> buildWorkOrderMasterData(
            List<FlowWorkOrderVo> flowWorkOrderVoList, OnlineTable masterTable, String ignoreMaskFields) {
        Set<String> businessKeySet = flowWorkOrderVoList.stream()
                .map(FlowWorkOrderVo::getBusinessKey)
                .filter(Objects::nonNull).collect(Collectors.toSet());
        if (CollUtil.isEmpty(businessKeySet)) {
            return ResponseResult.success();
        }
        Set<Serializable> convertedBusinessKeySet =
                onlineOperationHelper.convertToTypeValue(masterTable.getPrimaryKeyColumn(), businessKeySet);
        List<OnlineFilterDto> filterList = new LinkedList<>();
        OnlineFilterDto filterDto = new OnlineFilterDto();
        filterDto.setTableName(masterTable.getTableName());
        filterDto.setColumnName(masterTable.getPrimaryKeyColumn().getColumnName());
        filterDto.setFilterType(FieldFilterType.IN_LIST_FILTER);
        filterDto.setColumnValueList(new HashSet<>(convertedBusinessKeySet));
        filterList.add(filterDto);
        TaskInfoVo taskInfoVo = JSON.parseObject(flowWorkOrderVoList.get(0).getInitTaskInfo(), TaskInfoVo.class);
        // 验证在线表单及其关联数据源的合法性。
        ResponseResult<OnlineDatasource> datasourceResult = this.verifyAndGetOnlineDatasource(taskInfoVo.getFormId());
        if (!datasourceResult.isSuccess()) {
            return ResponseResult.errorFrom(datasourceResult);
        }
        OnlineDatasource datasource = datasourceResult.getData();
        ResponseResult<List<OnlineDatasourceRelation>> relationListResult =
                onlineOperationHelper.verifyAndGetRelationList(datasource.getDatasourceId(), RelationType.ONE_TO_ONE);
        if (!relationListResult.isSuccess()) {
            return ResponseResult.errorFrom(relationListResult);
        }
        MyPageData<Map<String, Object>> pageData = onlineOperationService.getMasterDataList(
                masterTable, relationListResult.getData(), null, filterList, null, null, null);
        List<Map<String, Object>> dataList = pageData.getDataList();
        onlineOperationHelper.maskFieldDataList(dataList, masterTable, relationListResult.getData(), ignoreMaskFields);
        Map<Object, Map<String, Object>> dataMap = dataList.stream()
                .collect(Collectors.toMap(c -> c.get(masterTable.getPrimaryKeyColumn().getColumnName()).toString(), c -> c));
        for (FlowWorkOrderVo flowWorkOrderVo : flowWorkOrderVoList) {
            if (StrUtil.isNotBlank(flowWorkOrderVo.getBusinessKey())) {
                Object dataId = onlineOperationHelper.convertToTypeValue(
                        masterTable.getPrimaryKeyColumn(), flowWorkOrderVo.getBusinessKey());
                Map<String, Object> data = dataMap.get(dataId.toString());
                if (data != null) {
                    flowWorkOrderVo.setMasterData(data);
                }
            }
        }
        return ResponseResult.success();
    }
}
