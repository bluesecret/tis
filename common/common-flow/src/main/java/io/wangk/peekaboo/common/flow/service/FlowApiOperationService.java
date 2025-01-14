package io.wangk.peekaboo.common.flow.service;

import com.alibaba.fastjson.JSONObject;
import io.wangk.peekaboo.common.flow.model.FlowTaskComment;
import io.wangk.peekaboo.common.flow.model.FlowWorkOrder;
import io.wangk.peekaboo.common.flow.vo.FlowWorkOrderVo;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;

import java.util.List;

/**
 * 仅供API接口调用的流程中心数据操作封装服务。
 *
 * @author wangk
 * @date 2025-01-14
 */
public interface FlowApiOperationService {

    /**
     * 启动流程实例，并将业务主键Id传给新启动的流程实例。
     *
     * @param processDefinitionId 流程实例Id。
     * @param dataId              业务主键Id。
     * @param variables           流程变量集合。
     * @return 流程工单对象。
     */
    FlowWorkOrder startWithBusinessKey(String processDefinitionId, String dataId, JSONObject variables);

    /**
     * 启动流程实例，同时执行第一个流程用户任务。
     *
     * @param processDefinitionId 流程定义Id。
     * @param dataId              业务主键Id。
     * @param comment             审批意见对象。
     * @param variables           流程变量集合。
     * @return 流程工单对象。
     */
    FlowWorkOrder startAndTakeFirst(String processDefinitionId, String dataId, FlowTaskComment comment, JSONObject variables);

    /**
     * 启动流程实例，同时保存业务草稿数据。
     *
     * @param processDefinitionId 流程定义Id。
     * @param dataId              业务主键Id。
     * @param masterData          主表数据。
     * @param slaveData           从表数据。
     * @return 流程工单对象。
     */
    FlowWorkOrder startAndSaveNewDraft(String processDefinitionId, String dataId, String masterData, String slaveData);

    /**
     * 执行流程图中的第一个用户审批任务。
     *
     * @param instance  流程实例对象。
     * @param task      流程任务对象。
     * @param comment   审批意见对象。
     * @param variables 流程变量集合。
     * @return 流程工单对象。
     */
    FlowWorkOrder takeFirstTask(ProcessInstance instance, Task task, FlowTaskComment comment, JSONObject variables);

    /**
     * 执行指定的用户审批任务。
     *
     * @param instance  流程实例对象。
     * @param task      流程任务对象。
     * @param comment   审批意见对象。
     * @param variables 任务变量数据对象。
     * @return 流程工单对象。
     */
    FlowWorkOrder takeTask(ProcessInstance instance, Task task, FlowTaskComment comment, JSONObject variables);

    /**
     * 构建工单表中的草稿数据。
     *
     * @param draftWorkOrderList 工单列表返回数据。
     */
    void buildDraftData(List<FlowWorkOrderVo> draftWorkOrderList);
}
