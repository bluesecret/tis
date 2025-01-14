package io.wangk.peekaboo.common.flow.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.page.PageMethod;
import io.wangk.peekaboo.common.core.annotation.DisableDataFilter;
import io.wangk.peekaboo.common.core.annotation.MyRequestBody;
import io.wangk.peekaboo.common.core.constant.ErrorCodeEnum;
import io.wangk.peekaboo.common.core.object.MyOrderParam;
import io.wangk.peekaboo.common.core.object.MyPageData;
import io.wangk.peekaboo.common.core.object.MyPageParam;
import io.wangk.peekaboo.common.core.object.ResponseResult;
import io.wangk.peekaboo.common.core.util.MyModelUtil;
import io.wangk.peekaboo.common.core.util.MyPageUtil;
import io.wangk.peekaboo.common.flow.constant.FlowConstant;
import io.wangk.peekaboo.common.flow.constant.FlowTaskStatus;
import io.wangk.peekaboo.common.flow.dto.FlowTaskCommentDto;
import io.wangk.peekaboo.common.flow.dto.FlowWorkOrderDto;
import io.wangk.peekaboo.common.flow.model.FlowEntry;
import io.wangk.peekaboo.common.flow.model.FlowTaskComment;
import io.wangk.peekaboo.common.flow.model.FlowWorkOrder;
import io.wangk.peekaboo.common.flow.service.FlowApiOperationService;
import io.wangk.peekaboo.common.flow.service.FlowApiService;
import io.wangk.peekaboo.common.flow.service.FlowWorkOrderService;
import io.wangk.peekaboo.common.flow.util.FlowOperationHelper;
import io.wangk.peekaboo.common.flow.vo.*;
import io.wangk.peekaboo.common.log.annotation.OperationLog;
import io.wangk.peekaboo.common.log.model.constant.SysOperationLogType;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 仅供API调用的流程中心操作接口。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Tag(name = "仅供API调用的工作流流程操作接口")
@Slf4j
@RestController
@RequestMapping("${common-flow.urlPrefix}/api/flowOperation")
public class FlowApiOperationController extends FlowOperationController {

    @Autowired
    private FlowApiService flowApiService;
    @Autowired
    private FlowWorkOrderService flowWorkOrderService;
    @Autowired
    private FlowOperationHelper flowOperationHelper;
    @Autowired
    private FlowApiOperationService flowApiOperationService;

    /**
     * 发起审批流程。
     *
     * @param processDefinitionKey 流程定义标识。
     * @param flowTaskCommentDto   审批意见。
     * @param taskVariableData     流程任务变量数据。
     * @param dataId               业务主键Id。
     * @param takeFirstTask        是否自动执行流程中的第一个任务。
     * @param copyData             传阅数据，格式为type和id，type的值参考FlowConstant中的常量值。
     * @return 应答结果对象。
     */
    @DisableDataFilter
    @OperationLog(type = SysOperationLogType.START_FLOW)
    @PostMapping("/start/{processDefinitionKey}")
    public ResponseResult<FlowWorkOrderVo> start(
            @PathVariable("processDefinitionKey") String processDefinitionKey,
            @MyRequestBody FlowTaskCommentDto flowTaskCommentDto,
            @MyRequestBody JSONObject taskVariableData,
            @MyRequestBody(required = true) String dataId,
            @MyRequestBody(required = true) Boolean takeFirstTask,
            @MyRequestBody JSONObject copyData) {
        // 验证流程数据的合法性。
        ResponseResult<FlowEntry> flowEntryResult =
                flowOperationHelper.verifyFullAndGetFlowEntry(processDefinitionKey);
        if (!flowEntryResult.isSuccess()) {
            return ResponseResult.errorFrom(flowEntryResult);
        }
        if (flowWorkOrderService.existUnfinished(processDefinitionKey, dataId)) {
            return ResponseResult.error(ErrorCodeEnum.FLOW_WORK_ORDER_EXIST);
        }
        String processDefinitionId = flowEntryResult.getData().getMainFlowEntryPublish().getProcessDefinitionId();
        // 这里把传阅数据放到任务变量中，是为了避免给流程数据操作方法增加额外的方法调用参数。
        if (MapUtil.isNotEmpty(copyData)) {
            if (taskVariableData == null) {
                taskVariableData = new JSONObject();
            }
            taskVariableData.put(FlowConstant.COPY_DATA_KEY, copyData);
        }
        FlowWorkOrder flowWorkOrder;
        if (BooleanUtil.isTrue(takeFirstTask)) {
            FlowTaskComment comment = MyModelUtil.copyTo(flowTaskCommentDto, FlowTaskComment.class);
            flowWorkOrder = flowApiOperationService.startAndTakeFirst(processDefinitionId, dataId, comment, taskVariableData);
        } else {
            flowWorkOrder = flowApiOperationService.startWithBusinessKey(processDefinitionId, dataId, taskVariableData);
        }
        FlowWorkOrderVo workOrderVo = MyModelUtil.copyTo(flowWorkOrder, FlowWorkOrderVo.class);
        return ResponseResult.success(workOrderVo);
    }

    /**
     * 启动审批流程实例并保存草稿。草稿数据的格式可以自定义。
     *
     * @param processDefinitionKey 流程定义标识。
     * @param processInstanceId    流程实例Id。
     * @param dataId               业务主键Id。
     * @param masterData           主表数据。
     * @param slaveData            从表数据。
     * @return 应答结果对象。
     */
    @DisableDataFilter
    @OperationLog(type = SysOperationLogType.START_FLOW)
    @PostMapping("/startAndSaveDraft/{processDefinitionKey}")
    public ResponseResult<FlowTaskVo> startAndSaveDraft(
            @PathVariable("processDefinitionKey") String processDefinitionKey,
            @MyRequestBody String processInstanceId,
            @MyRequestBody String dataId,
            @MyRequestBody String masterData,
            @MyRequestBody String slaveData) {
        ResponseResult<FlowEntry> flowEntryResult =
                flowOperationHelper.verifyFullAndGetFlowEntry(processDefinitionKey);
        if (!flowEntryResult.isSuccess()) {
            return ResponseResult.errorFrom(flowEntryResult);
        }
        if (flowWorkOrderService.existUnfinished(processDefinitionKey, dataId)) {
            return ResponseResult.error(ErrorCodeEnum.FLOW_WORK_ORDER_EXIST);
        }
        FlowWorkOrder flowWorkOrder;
        if (processInstanceId == null) {
            String processDefinitionId = flowEntryResult.getData().getMainFlowEntryPublish().getProcessDefinitionId();
            flowWorkOrder = flowApiOperationService.startAndSaveNewDraft(processDefinitionId, dataId, masterData, slaveData);
        } else {
            ResponseResult<FlowWorkOrder> flowWorkOrderResult =
                    flowOperationHelper.verifyAndGetFlowWorkOrderWithDraft(processDefinitionKey, processInstanceId);
            if (!flowWorkOrderResult.isSuccess()) {
                return ResponseResult.errorFrom(flowEntryResult);
            }
            flowWorkOrder = flowWorkOrderResult.getData();
            flowWorkOrderService.updateDraft(flowWorkOrder.getWorkOrderId(), masterData, slaveData);
        }
        List<Task> taskList = flowApiService.getProcessInstanceActiveTaskList(flowWorkOrder.getProcessInstanceId());
        List<FlowTaskVo> flowTaskVoList = flowApiService.convertToFlowTaskList(taskList);
        return ResponseResult.success(flowTaskVoList.get(0));
    }

    /**
     * 提交审批任务。
     *
     * @param processDefinitionKey 流程定义标识。
     * @param processInstanceId    流程实例Id。
     * @param taskId               审批任务Id。
     * @param flowTaskCommentDto   审批意见。
     * @param taskVariableData     流程任务变量数据。
     * @param copyData             传阅数据，格式为type和id，type的值参考FlowConstant中的常量值。
     * @return 应答结果对象。
     */
    @DisableDataFilter
    @OperationLog(type = SysOperationLogType.SUBMIT_TASK)
    @PostMapping("/submitUserTask/{processDefinitionKey}")
    public ResponseResult<Void> submitUserTask(
            @PathVariable("processDefinitionKey") String processDefinitionKey,
            @MyRequestBody(required = true) String processInstanceId,
            @MyRequestBody(required = true) String taskId,
            @MyRequestBody(required = true) FlowTaskCommentDto flowTaskCommentDto,
            @MyRequestBody JSONObject taskVariableData,
            @MyRequestBody JSONObject copyData) {
        String errorMessage;
        ResponseResult<Task> taskResult =
                flowOperationHelper.verifySubmitAndGetTask(processInstanceId, taskId, flowTaskCommentDto);
        if (!taskResult.isSuccess()) {
            return ResponseResult.errorFrom(taskResult);
        }
        Task task = taskResult.getData();
        ProcessInstance instance = flowApiService.getProcessInstance(processInstanceId);
        if (!StrUtil.equals(instance.getProcessDefinitionKey(), processDefinitionKey)) {
            errorMessage = "数据验证失败，请求流程标识与流程实例不匹配，请核对！";
            return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, errorMessage);
        }
        FlowTaskComment flowTaskComment = MyModelUtil.copyTo(flowTaskCommentDto, FlowTaskComment.class);
        boolean forUpdate = StrUtil.isNotBlank(instance.getBusinessKey());
        // 这里把传阅数据放到任务变量中，是为了避免给流程数据操作方法增加额外的方法调用参数。
        if (MapUtil.isNotEmpty(copyData)) {
            if (taskVariableData == null) {
                taskVariableData = new JSONObject();
            }
            taskVariableData.put(FlowConstant.COPY_DATA_KEY, copyData);
        }
        if (!forUpdate) {
            flowApiOperationService.takeFirstTask(instance, task, flowTaskComment, taskVariableData);
        } else {
            flowApiOperationService.takeTask(instance, task, flowTaskComment, taskVariableData);
        }
        return ResponseResult.success();
    }

    /**
     * 工作流工单列表。
     *
     * @param processDefinitionKey   流程定义标识。
     * @param flowWorkOrderDtoFilter 过滤对象。
     * @param orderParam             排序参数。
     * @param pageParam              分页参数。
     * @return 查询结果。
     */
    @PostMapping("/listWorkOrder/{processDefinitionKey}")
    public ResponseResult<MyPageData<FlowWorkOrderVo>> listWorkOrder(
            @PathVariable("processDefinitionKey") String processDefinitionKey,
            @MyRequestBody FlowWorkOrderDto flowWorkOrderDtoFilter,
            @MyRequestBody MyOrderParam orderParam,
            @MyRequestBody MyPageParam pageParam) {
        if (pageParam != null) {
            PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        FlowWorkOrder flowWorkOrderFilter =
                flowOperationHelper.makeWorkOrderFilter(flowWorkOrderDtoFilter, processDefinitionKey);
        String orderBy = MyOrderParam.buildOrderBy(orderParam, FlowWorkOrder.class);
        List<FlowWorkOrder> flowWorkOrderList =
                flowWorkOrderService.getFlowWorkOrderList(flowWorkOrderFilter, orderBy);
        MyPageData<FlowWorkOrderVo> resultData =
                MyPageUtil.makeResponseData(flowWorkOrderList, FlowWorkOrderVo.class);
        if (CollUtil.isEmpty(resultData.getDataList())) {
            return ResponseResult.success(resultData);
        }
        flowOperationHelper.buildWorkOrderApprovalStatus(processDefinitionKey, resultData.getDataList());
        // 组装工单中需要返回给前端的流程任务数据。
        flowOperationHelper.buildWorkOrderTaskInfo(resultData.getDataList());
        List<FlowWorkOrderVo> draftWorkOrderList = resultData.getDataList().stream()
                .filter(c -> c.getFlowStatus().equals(FlowTaskStatus.DRAFT)).collect(Collectors.toList());
        flowApiOperationService.buildDraftData(draftWorkOrderList);
        return ResponseResult.success(resultData);
    }

    /**
     * 主动驳回当前的待办任务到开始节点，只用当前待办任务的指派人或者候选者才能完成该操作。
     *
     * @param processInstanceId 流程实例Id。
     * @param taskId            待办任务Id。
     * @param taskComment       驳回备注。
     * @param taskVariableData  流程任务变量数据。
     * @return 操作应答结果。
     */
    @PostMapping("/rejectToStartUserTask/{processDefinitionKey}")
    public ResponseResult<FlowWorkOrderVo> rejectToStartUserTask(
            @PathVariable("processDefinitionKey") String processDefinitionKey,
            @MyRequestBody(required = true) String processInstanceId,
            @MyRequestBody(required = true) String taskId,
            @MyRequestBody(required = true) String taskComment,
            @MyRequestBody JSONObject taskVariableData) {
        ResponseResult<Void> responseResult =
                super.rejectToStartUserTask(processInstanceId, taskId, taskComment, taskVariableData);
        if (!responseResult.isSuccess()) {
            return ResponseResult.errorFrom(responseResult);
        }
        return this.createResponseResultWithFlowWorkOrder(processInstanceId);
    }

    /**
     * 主动驳回当前的待办任务，只用当前待办任务的指派人或者候选者才能完成该操作。
     *
     * @param processInstanceId 流程实例Id。
     * @param taskId            待办任务Id。
     * @param taskComment       驳回备注。
     * @param targetTaskKey     驳回到的目标任务标识。
     * @param taskVariableData  流程任务变量数据。
     * @return 操作应答结果。
     */
    @PostMapping("/rejectRuntimeTask/{processDefinitionKey}")
    public ResponseResult<FlowWorkOrderVo> rejectRuntimeTask(
            @PathVariable("processDefinitionKey") String processDefinitionKey,
            @MyRequestBody(required = true) String processInstanceId,
            @MyRequestBody(required = true) String taskId,
            @MyRequestBody(required = true) String taskComment,
            @MyRequestBody String targetTaskKey,
            @MyRequestBody JSONObject taskVariableData) {
        ResponseResult<Void> responseResult =
                super.rejectRuntimeTask(processInstanceId, taskId, taskComment, targetTaskKey, taskVariableData);
        if (!responseResult.isSuccess()) {
            return ResponseResult.errorFrom(responseResult);
        }
        return this.createResponseResultWithFlowWorkOrder(processInstanceId);
    }

    /**
     * 撤回当前用户提交的，但是尚未被审批的待办任务。只有已办任务的指派人才能完成该操作。
     *
     * @param processInstanceId 流程实例Id。
     * @param taskId            待撤回的已办任务Id。
     * @param taskComment       撤回备注。
     * @param taskVariableData  流程任务变量数据。
     * @return 操作应答结果。
     */
    @PostMapping("/revokeHistoricTask/{processDefinitionKey}")
    public ResponseResult<FlowWorkOrderVo> revokeHistoricTask(
            @PathVariable("processDefinitionKey") String processDefinitionKey,
            @MyRequestBody(required = true) String processInstanceId,
            @MyRequestBody(required = true) String taskId,
            @MyRequestBody(required = true) String taskComment,
            @MyRequestBody JSONObject taskVariableData) {
        ResponseResult<Void> responseResult =
                super.revokeHistoricTask(processInstanceId, taskId, taskComment, taskVariableData);
        if (!responseResult.isSuccess()) {
            return ResponseResult.errorFrom(responseResult);
        }
        return this.createResponseResultWithFlowWorkOrder(processInstanceId);
    }

    private ResponseResult<FlowWorkOrderVo> createResponseResultWithFlowWorkOrder(String processInstanceId) {
        FlowWorkOrder flowWorkOrder = flowWorkOrderService.getFlowWorkOrderByProcessInstanceId(processInstanceId);
        return ResponseResult.success(MyModelUtil.copyTo(flowWorkOrder, FlowWorkOrderVo.class));
    }
}
