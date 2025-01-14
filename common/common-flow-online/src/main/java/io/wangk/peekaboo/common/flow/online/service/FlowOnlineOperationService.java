package io.wangk.peekaboo.common.flow.online.service;

import com.alibaba.fastjson.JSONObject;
import io.wangk.peekaboo.common.online.model.OnlineDatasource;
import io.wangk.peekaboo.common.online.model.OnlineDatasourceRelation;
import io.wangk.peekaboo.common.online.model.OnlineTable;
import io.wangk.peekaboo.common.flow.model.FlowWorkOrder;
import io.wangk.peekaboo.common.flow.model.FlowTaskComment;
import io.wangk.peekaboo.common.flow.model.FlowTransProducer;
import org.flowable.task.api.Task;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 流程操作服务接口。
 *
 * @author wangk
 * @date 2025-01-14
 */
public interface FlowOnlineOperationService {

    /**
     * 启动流程实例，并将业务主键Id传给新启动的流程实例。
     *
     * @param processDefinitionId 流程实例Id。
     * @param onlineTableId       在线表单数据表Id。
     * @param dataId              业务主键Id。
     * @param taskVariableData    流程任务的变量数据。
     */
    void startWithBusinessKey(String processDefinitionId, Long onlineTableId, String dataId, JSONObject taskVariableData);

    /**
     * 保存在线表单的数据，同时启动流程。如果当前用户是第一个用户任务的Assignee，
     * 或者第一个用户任务的Assignee是流程发起人变量，该方法还会自动Take第一个任务。
     *
     * @param processDefinitionId 流程定义Id。
     * @param flowTaskComment     流程审批批注对象。
     * @param taskVariableData    流程任务的变量数据。
     * @param table               表对象。
     * @param data                表数据。
     */
    void saveNewAndStartProcess(
            String processDefinitionId,
            FlowTaskComment flowTaskComment,
            JSONObject taskVariableData,
            OnlineTable table,
            JSONObject data);

    /**
     * 保存在线表单的数据，同时启动流程。如果当前用户是第一个用户任务的Assignee，
     * 或者第一个用户任务的Assignee是流程发起人变量，该方法还会自动Take第一个任务。
     *
     * @param processDefinitionId 流程定义Id。
     * @param flowTaskComment     流程审批批注对象。
     * @param taskVariableData    流程任务的变量数据。
     * @param masterTable         主表对象。
     * @param masterData          主表数据。
     * @param slaveDataListMap    关联从表数据Map。
     */
    void saveNewAndStartProcess(
            String processDefinitionId,
            FlowTaskComment flowTaskComment,
            JSONObject taskVariableData,
            OnlineTable masterTable,
            JSONObject masterData,
            Map<OnlineDatasourceRelation, List<JSONObject>> slaveDataListMap);

    /**
     * 保存在线表单的草稿数据，同时启动一个流程实例。
     *
     * @param processDefinitionId 流程定义Id。
     * @param tableId             在线表单主表Id。
     * @param masterData          主表数据。
     * @param slaveData           所有关联从表数据。
     * @return 流程工单对象。
     */
    FlowWorkOrder saveNewDraftAndStartProcess(
            String processDefinitionId, Long tableId, JSONObject masterData, JSONObject slaveData);

    /**
     * 保存在线表单的数据，同时Take用户任务。
     *
     * @param processInstanceId 流程实例Id。
     * @param taskId            流程任务Id。
     * @param flowTaskComment   流程审批批注对象。
     * @param taskVariableData  流程任务的变量数据。
     * @param table             表对象。
     * @param data              表数据。
     */
    void saveNewAndTakeTask(
            String processInstanceId,
            String taskId,
            FlowTaskComment flowTaskComment,
            JSONObject taskVariableData,
            OnlineTable table,
            JSONObject data);

    /**
     * 保存在线表单的数据，同时Take用户任务。
     *
     * @param processInstanceId 流程实例Id。
     * @param taskId            流程任务Id。
     * @param flowTaskComment   流程审批批注对象。
     * @param taskVariableData  流程任务的变量数据。
     * @param masterTable       主表对象。
     * @param masterData        主表数据。
     * @param slaveDataListMap  关联从表数据Map。
     */
    void saveNewAndTakeTask(
            String processInstanceId,
            String taskId,
            FlowTaskComment flowTaskComment,
            JSONObject taskVariableData,
            OnlineTable masterTable,
            JSONObject masterData,
            Map<OnlineDatasourceRelation, List<JSONObject>> slaveDataListMap);

    /**
     * 保存业务表数据，同时接收流程任务。
     *
     * @param task             流程任务。
     * @param flowTaskComment  流程审批批注对象。
     * @param taskVariableData 流程任务的变量数据。
     * @param datasource       主表所在数据源。
     * @param masterData       主表数据。
     * @param masterDataId     主表数据主键。
     * @param slaveDataListMap 从表数据。
     */
    void updateAndTakeTask(
            Task task,
            FlowTaskComment flowTaskComment,
            JSONObject taskVariableData,
            OnlineDatasource datasource,
            JSONObject masterData,
            String masterDataId,
            Map<OnlineDatasourceRelation, List<JSONObject>> slaveDataListMap);

    /**
     * 修复业务数据。
     *
     * @param flowTransProducer 流程操作流水生产者对象。
     */
    void fixBusinessData(FlowTransProducer flowTransProducer);

    /**
     * 获取在线表单工作流Id所关联的权限数据，包括权限字列表和权限资源列表。
     *
     * @param onlineFormEntryIds 在线表单工作流Id集合。
     * @return 参数中在线表单工作流Id集合所关联的权限数据。
     */
    List<Map<String, Object>> calculatePermData(Set<Long> onlineFormEntryIds);
}
