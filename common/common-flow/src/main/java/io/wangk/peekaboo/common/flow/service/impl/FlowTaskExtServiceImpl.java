package io.wangk.peekaboo.common.flow.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.StrFormatter;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.wangk.peekaboo.common.core.annotation.MyDataSourceResolver;
import io.wangk.peekaboo.common.core.base.dao.BaseDaoMapper;
import io.wangk.peekaboo.common.core.base.service.BaseService;
import io.wangk.peekaboo.common.core.constant.ApplicationConstant;
import io.wangk.peekaboo.common.core.exception.MyRuntimeException;
import io.wangk.peekaboo.common.core.object.Tuple2;
import io.wangk.peekaboo.common.core.util.DefaultDataSourceResolver;
import io.wangk.peekaboo.common.flow.constant.FlowApprovalType;
import io.wangk.peekaboo.common.flow.constant.FlowAutoActionType;
import io.wangk.peekaboo.common.flow.constant.FlowConstant;
import io.wangk.peekaboo.common.flow.object.AutoTaskConfig;
import io.wangk.peekaboo.common.flow.object.FlowElementExtProperty;
import io.wangk.peekaboo.common.flow.object.FlowTaskMultiSignAssign;
import io.wangk.peekaboo.common.flow.object.FlowUserTaskExtData;
import io.wangk.peekaboo.common.flow.service.*;
import io.wangk.peekaboo.common.flow.dao.*;
import io.wangk.peekaboo.common.flow.model.*;
import io.wangk.peekaboo.common.flow.util.BaseFlowIdentityExtHelper;
import io.wangk.peekaboo.common.flow.util.FlowCustomExtFactory;
import io.wangk.peekaboo.common.flow.vo.FlowUserInfoVo;
import io.wangk.peekaboo.common.flow.vo.TaskInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.*;
import org.flowable.bpmn.model.Process;
import org.flowable.task.api.TaskInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@MyDataSourceResolver(
        resolver = DefaultDataSourceResolver.class,
        intArg = ApplicationConstant.COMMON_FLOW_AND_ONLINE_DATASOURCE_TYPE)
@Service("flowTaskExtService")
public class FlowTaskExtServiceImpl extends BaseService<FlowTaskExt, String> implements FlowTaskExtService {

    @Autowired
    private FlowTaskExtMapper flowTaskExtMapper;
    @Autowired
    private FlowEntryVariableService flowEntryVariableService;
    @Autowired
    private FlowCustomExtFactory flowCustomExtFactory;
    @Autowired
    private FlowApiService flowApiService;
    @Autowired
    private FlowMultiInstanceTransService flowMultiInstanceTransService;
    @Autowired
    private FlowTaskCommentService flowTaskCommentService;

    private static final String ID = "id";
    private static final String TYPE = "type";
    private static final String LABEL = "label";
    private static final String NAME = "name";
    private static final String VALUE = "value";

    /**
     * 返回当前Service的主表Mapper对象。
     *
     * @return 主表Mapper对象。
     */
    @Override
    protected BaseDaoMapper<FlowTaskExt> mapper() {
        return flowTaskExtMapper;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveBatch(List<FlowTaskExt> flowTaskExtList) {
        if (CollUtil.isNotEmpty(flowTaskExtList)) {
            flowTaskExtMapper.insertList(flowTaskExtList);
        }
    }

    @Override
    public FlowTaskExt getByProcessDefinitionIdAndTaskId(String processDefinitionId, String taskId) {
        FlowTaskExt filter = new FlowTaskExt();
        filter.setProcessDefinitionId(processDefinitionId);
        filter.setTaskId(taskId);
        return flowTaskExtMapper.selectOne(new QueryWrapper<>(filter));
    }

    @Override
    public List<FlowTaskExt> getByProcessDefinitionId(String processDefinitionId) {
        FlowTaskExt filter = new FlowTaskExt();
        filter.setProcessDefinitionId(processDefinitionId);
        return flowTaskExtMapper.selectList(new QueryWrapper<>(filter));
    }

    @Override
    public List<FlowUserInfoVo> getCandidateUserInfoList(
            String processInstanceId,
            FlowTaskExt flowTaskExt,
            TaskInfo taskInfo,
            boolean isMultiInstanceTask,
            boolean historic) {
        List<FlowUserInfoVo> resultUserMapList = new LinkedList<>();
        if (!isMultiInstanceTask && this.buildTransferUserList(taskInfo, resultUserMapList)) {
            return resultUserMapList;
        }
        Set<String> loginNameSet = new HashSet<>();
        this.buildFlowUserInfoListByDeptAndRoleIds(flowTaskExt, loginNameSet, resultUserMapList);
        BaseFlowIdentityExtHelper flowIdentityExtHelper = flowCustomExtFactory.getFlowIdentityExtHelper();
        Set<String> usernameSet = new HashSet<>();
        switch (flowTaskExt.getGroupType()) {
            case FlowConstant.GROUP_TYPE_ASSIGNEE:
                if (!isMultiInstanceTask) {
                    usernameSet.add(taskInfo.getAssignee());
                }
                break;
            case FlowConstant.GROUP_TYPE_DEPT_POST_LEADER:
                String deptPostLeaderId = flowApiService.getExecutionVariableStringWithSafe(
                        taskInfo.getExecutionId(), FlowConstant.GROUP_TYPE_DEPT_POST_LEADER_VAR);
                List<FlowUserInfoVo> userInfoList =
                        flowIdentityExtHelper.getUserInfoListByDeptPostIds(CollUtil.newHashSet(deptPostLeaderId));
                this.buildUserMapList(userInfoList, loginNameSet, resultUserMapList);
                break;
            case FlowConstant.GROUP_TYPE_UP_DEPT_POST_LEADER:
                String upDeptPostLeaderId = flowApiService.getExecutionVariableStringWithSafe(
                        taskInfo.getExecutionId(), FlowConstant.GROUP_TYPE_UP_DEPT_POST_LEADER_VAR);
                List<FlowUserInfoVo> upUserInfoList =
                        flowIdentityExtHelper.getUserInfoListByDeptPostIds(CollUtil.newHashSet(upDeptPostLeaderId));
                this.buildUserMapList(upUserInfoList, loginNameSet, resultUserMapList);
                break;
            default:
                break;
        }
        List<String> candidateUsernames = flowApiService.getCandidateUsernames(flowTaskExt, taskInfo.getId());
        if (CollUtil.isNotEmpty(candidateUsernames)) {
            usernameSet.addAll(candidateUsernames);
        }
        if (isMultiInstanceTask) {
            List<String> assigneeList = this.getAssigneeList(taskInfo.getExecutionId(), taskInfo.getId());
            if (CollUtil.isNotEmpty(assigneeList)) {
                usernameSet.addAll(assigneeList);
            }
        }
        if (CollUtil.isNotEmpty(usernameSet)) {
            List<FlowUserInfoVo> userInfoList = flowIdentityExtHelper.getUserInfoListByUsernameSet(usernameSet);
            this.buildUserMapList(userInfoList, loginNameSet, resultUserMapList);
        }
        Tuple2<Set<String>, Set<String>> tuple2 =
                flowApiService.getDeptPostIdAndPostIds(flowTaskExt, processInstanceId, historic);
        Set<String> postIdSet = tuple2.getSecond();
        Set<String> deptPostIdSet = tuple2.getFirst();
        if (CollUtil.isNotEmpty(postIdSet)) {
            List<FlowUserInfoVo> userInfoList = flowIdentityExtHelper.getUserInfoListByPostIds(postIdSet);
            this.buildUserMapList(userInfoList, loginNameSet, resultUserMapList);
        }
        if (CollUtil.isNotEmpty(deptPostIdSet)) {
            List<FlowUserInfoVo> userInfoList = flowIdentityExtHelper.getUserInfoListByDeptPostIds(deptPostIdSet);
            this.buildUserMapList(userInfoList, loginNameSet, resultUserMapList);
        }
        return reorderSequenceMultiInstanceUserList(flowTaskExt, usernameSet, resultUserMapList);
    }

    @Override
    public List<FlowUserInfoVo> getCandidateUserInfoList(
            String processInstanceId,
            String executionId,
            FlowTaskExt flowTaskExt) {
        List<FlowUserInfoVo> resultUserMapList = new LinkedList<>();
        Set<String> loginNameSet = new HashSet<>();
        this.buildFlowUserInfoListByDeptAndRoleIds(flowTaskExt, loginNameSet, resultUserMapList);
        Set<String> usernameSet = new HashSet<>();
        BaseFlowIdentityExtHelper flowIdentityExtHelper = flowCustomExtFactory.getFlowIdentityExtHelper();
        switch (flowTaskExt.getGroupType()) {
            case FlowConstant.GROUP_TYPE_DEPT_POST_LEADER:
                String deptPostLeaderId = flowApiService.getExecutionVariableStringWithSafe(
                        executionId, FlowConstant.GROUP_TYPE_DEPT_POST_LEADER_VAR);
                List<FlowUserInfoVo> userInfoList =
                        flowIdentityExtHelper.getUserInfoListByDeptPostIds(CollUtil.newHashSet(deptPostLeaderId));
                this.buildUserMapList(userInfoList, loginNameSet, resultUserMapList);
                break;
            case FlowConstant.GROUP_TYPE_UP_DEPT_POST_LEADER:
                String upDeptPostLeaderId = flowApiService.getExecutionVariableStringWithSafe(
                        executionId, FlowConstant.GROUP_TYPE_UP_DEPT_POST_LEADER_VAR);
                List<FlowUserInfoVo> upUserInfoList =
                        flowIdentityExtHelper.getUserInfoListByDeptPostIds(CollUtil.newHashSet(upDeptPostLeaderId));
                this.buildUserMapList(upUserInfoList, loginNameSet, resultUserMapList);
                break;
            default:
                break;
        }
        List<String> candidateUsernames;
        if (StrUtil.isBlank(flowTaskExt.getCandidateUsernames())) {
            candidateUsernames = Collections.emptyList();
        } else {
            if (!StrUtil.equals(flowTaskExt.getCandidateUsernames(), "${" + FlowConstant.TASK_APPOINTED_ASSIGNEE_VAR + "}")) {
                candidateUsernames = StrUtil.split(flowTaskExt.getCandidateUsernames(), ",");
            } else {
                Object v = flowApiService.getExecutionVariableStringWithSafe(executionId, FlowConstant.TASK_APPOINTED_ASSIGNEE_VAR);
                candidateUsernames = v == null ? null : StrUtil.split(v.toString(), ",");
            }
        }
        if (CollUtil.isNotEmpty(candidateUsernames)) {
            usernameSet.addAll(candidateUsernames);
        }
        if (CollUtil.isNotEmpty(usernameSet)) {
            List<FlowUserInfoVo> userInfoList = flowIdentityExtHelper.getUserInfoListByUsernameSet(usernameSet);
            this.buildUserMapList(userInfoList, loginNameSet, resultUserMapList);
        }
        Tuple2<Set<String>, Set<String>> tuple2 =
                flowApiService.getDeptPostIdAndPostIds(flowTaskExt, processInstanceId, false);
        Set<String> postIdSet = tuple2.getSecond();
        Set<String> deptPostIdSet = tuple2.getFirst();
        if (CollUtil.isNotEmpty(postIdSet)) {
            List<FlowUserInfoVo> userInfoList = flowIdentityExtHelper.getUserInfoListByPostIds(postIdSet);
            this.buildUserMapList(userInfoList, loginNameSet, resultUserMapList);
        }
        if (CollUtil.isNotEmpty(deptPostIdSet)) {
            List<FlowUserInfoVo> userInfoList = flowIdentityExtHelper.getUserInfoListByDeptPostIds(deptPostIdSet);
            this.buildUserMapList(userInfoList, loginNameSet, resultUserMapList);
        }
        return resultUserMapList;
    }

    @Override
    public void verifyAutoTaskConfig(FlowTaskExt taskExt) {
        if (StrUtil.isBlank(taskExt.getAutoConfigJson())) {
            return;
        }
        AutoTaskConfig taskConfig = JSON.parseObject(taskExt.getAutoConfigJson(), AutoTaskConfig.class);
        this.verifyDataOperationTask(taskConfig);
        String errorMessage;
        if (taskConfig.getActionType().equals(FlowAutoActionType.SELECT_ONE)) {
            this.verifySrcTableNameEmpty(taskConfig);
        } else if (taskConfig.getActionType().equals(FlowAutoActionType.AGGREGATION_CALC)) {
            this.verifySrcTableNameEmpty(taskConfig);
            if (CollUtil.isEmpty(taskConfig.getAggregationDataList())) {
                errorMessage = StrFormatter.format("任务 [{}] 的聚合计算配置不能为空！", taskConfig.getTaskName());
                throw new MyRuntimeException(errorMessage);
            }
        } else if (taskConfig.getActionType().equals(FlowAutoActionType.NUMBER_CALC)) {
            if (StrUtil.isBlank(taskConfig.getCalculateFormula())) {
                errorMessage = StrFormatter.format("任务 [{}] 的数值计算公式不能为空！", taskConfig.getTaskName());
                throw new MyRuntimeException(errorMessage);
            }
        } else if (taskConfig.getActionType().equals(FlowAutoActionType.HTTP)) {
            this.verifyHttpConfig(taskConfig);
        } else if (taskConfig.getActionType().equals(FlowAutoActionType.LOAD_CACHE)
                || taskConfig.getActionType().equals(FlowAutoActionType.DELETE_CACHE)) {
            this.verifyRedisCacheConfig(taskConfig);
        } else if (taskConfig.getActionType().equals(FlowAutoActionType.SEND_MQ)) {
            this.verifyMqConfig(taskConfig);
        }
    }

    @Override
    public FlowTaskExt buildTaskExtByUserTask(UserTask userTask) {
        FlowTaskExt flowTaskExt = new FlowTaskExt();
        flowTaskExt.setTaskId(userTask.getId());
        String formKey = userTask.getFormKey();
        if (StrUtil.isNotBlank(formKey)) {
            TaskInfoVo taskInfoVo = JSON.parseObject(formKey, TaskInfoVo.class);
            flowTaskExt.setGroupType(taskInfoVo.getGroupType());
        }
        JSONObject extraDataJson = this.buildFlowTaskExtensionData(userTask);
        if (extraDataJson != null) {
            flowTaskExt.setExtraDataJson(extraDataJson.toJSONString());
        }
        Map<String, List<ExtensionElement>> extensionMap = userTask.getExtensionElements();
        if (MapUtil.isEmpty(extensionMap)) {
            return flowTaskExt;
        }
        List<JSONObject> operationList = this.buildOperationListExtensionElement(extensionMap);
        if (CollUtil.isNotEmpty(operationList)) {
            flowTaskExt.setOperationListJson(JSON.toJSONString(operationList));
        }
        List<JSONObject> variableList = this.buildVariableListExtensionElement(extensionMap);
        if (CollUtil.isNotEmpty(variableList)) {
            flowTaskExt.setVariableListJson(JSON.toJSONString(variableList));
        }
        JSONObject assigneeListObject = this.buildAssigneeListExtensionElement(extensionMap);
        if (assigneeListObject != null) {
            flowTaskExt.setAssigneeListJson(JSON.toJSONString(assigneeListObject));
        }
        List<JSONObject> deptPostList = this.buildDeptPostListExtensionElement(extensionMap);
        if (deptPostList != null) {
            flowTaskExt.setDeptPostListJson(JSON.toJSONString(deptPostList));
        }
        List<JSONObject> copyList = this.buildCopyListExtensionElement(extensionMap);
        if (copyList != null) {
            flowTaskExt.setCopyListJson(JSON.toJSONString(copyList));
        }
        JSONObject candidateGroupObject = this.buildUserCandidateGroupsExtensionElement(extensionMap);
        if (candidateGroupObject != null) {
            String type = candidateGroupObject.getString(TYPE);
            String value = candidateGroupObject.getString(VALUE);
            switch (type) {
                case "DEPT":
                    flowTaskExt.setDeptIds(value);
                    break;
                case "ROLE":
                    flowTaskExt.setRoleIds(value);
                    break;
                case "USERS":
                    flowTaskExt.setCandidateUsernames(value);
                    break;
                default:
                    break;
            }
        }
        return flowTaskExt;
    }

    @Override
    public List<FlowTaskExt> buildTaskExtList(BpmnModel bpmnModel) {
        List<Process> processList = bpmnModel.getProcesses();
        List<FlowTaskExt> flowTaskExtList = new LinkedList<>();
        for (Process process : processList) {
            for (FlowElement element : process.getFlowElements()) {
                this.doBuildTaskExtList(element, flowTaskExtList);
            }
        }
        return flowTaskExtList;
    }

    @Override
    public List<FlowTaskExt> buildAutoTaskExtList(BpmnModel bpmnModel) {
        List<FlowTaskExt> flowTaskExtList = new LinkedList<>();
        for (Process process : bpmnModel.getProcesses()) {
            for (FlowElement element : process.getFlowElements()) {
                if (element instanceof ServiceTask || element instanceof ReceiveTask) {
                    this.doBuildTaskExtList(element, flowTaskExtList);
                }
            }
        }
        return flowTaskExtList;
    }

    @Override
    public List<JSONObject> buildOperationListExtensionElement(Map<String, List<ExtensionElement>> extensionMap) {
        List<ExtensionElement> formOperationElements =
                this.getMyExtensionElementList(extensionMap, "operationList", "formOperation");
        if (CollUtil.isEmpty(formOperationElements)) {
            return Collections.emptyList();
        }
        List<JSONObject> resultList = new LinkedList<>();
        for (ExtensionElement e : formOperationElements) {
            JSONObject operationJsonData = new JSONObject();
            operationJsonData.put(ID, e.getAttributeValue(null, ID));
            operationJsonData.put(LABEL, e.getAttributeValue(null, LABEL));
            operationJsonData.put(TYPE, e.getAttributeValue(null, TYPE));
            operationJsonData.put("showOrder", e.getAttributeValue(null, "showOrder"));
            operationJsonData.put("latestApprovalStatus", e.getAttributeValue(null, "latestApprovalStatus"));
            String multiSignAssignee = e.getAttributeValue(null, "multiSignAssignee");
            if (StrUtil.isNotBlank(multiSignAssignee)) {
                operationJsonData.put("multiSignAssignee",
                        JSON.parseObject(multiSignAssignee, FlowTaskMultiSignAssign.class));
            }
            resultList.add(operationJsonData);
        }
        return resultList;
    }

    @Override
    public List<JSONObject> buildVariableListExtensionElement(Map<String, List<ExtensionElement>> extensionMap) {
        List<ExtensionElement> formVariableElements =
                this.getMyExtensionElementList(extensionMap, "variableList", "formVariable");
        if (CollUtil.isEmpty(formVariableElements)) {
            return Collections.emptyList();
        }
        Set<Long> variableIdSet = new HashSet<>();
        for (ExtensionElement e : formVariableElements) {
            String id = e.getAttributeValue(null, ID);
            variableIdSet.add(Long.parseLong(id));
        }
        List<FlowEntryVariable> variableList = flowEntryVariableService.getInList(variableIdSet);
        List<JSONObject> resultList = new LinkedList<>();
        for (FlowEntryVariable variable : variableList) {
            resultList.add((JSONObject) JSON.toJSON(variable));
        }
        return resultList;
    }

    @Override
    public FlowElementExtProperty buildFlowElementExt(FlowElement element) {
        JSONObject propertiesData = this.buildFlowElementExtToJson(element);
        return propertiesData == null ? null : propertiesData.toJavaObject(FlowElementExtProperty.class);
    }

    @Override
    public JSONObject buildFlowElementExtToJson(FlowElement element) {
        Map<String, List<ExtensionElement>> extensionMap = element.getExtensionElements();
        List<ExtensionElement> propertiesElements =
                this.getMyExtensionElementList(extensionMap, "properties", "property");
        if (CollUtil.isEmpty(propertiesElements)) {
            return null;
        }
        JSONObject propertiesData = new JSONObject();
        for (ExtensionElement e : propertiesElements) {
            String name = e.getAttributeValue(null, NAME);
            String value = e.getAttributeValue(null, VALUE);
            propertiesData.put(name, value);
        }
        return propertiesData;
    }

    private List<FlowUserInfoVo> reorderSequenceMultiInstanceUserList(
            FlowTaskExt flowTaskExt, Set<String> usernameSet, List<FlowUserInfoVo> resultUserMapList) {
        if (CollUtil.isEmpty(resultUserMapList) || StrUtil.isBlank(flowTaskExt.getExtraDataJson())) {
            return resultUserMapList;
        }
        FlowUserTaskExtData taskExtData = JSON.parseObject(flowTaskExt.getExtraDataJson(), FlowUserTaskExtData.class);
        if (BooleanUtil.isFalse(taskExtData.getSequenceMultiInstance())) {
            return resultUserMapList;
        }
        List<FlowUserInfoVo> resultList = new LinkedList<>();
        Map<String, FlowUserInfoVo> resultMap =
                resultUserMapList.stream().collect(Collectors.toMap(FlowUserInfoVo::getLoginName, c -> c));
        for (String username : usernameSet) {
            FlowUserInfoVo userInfoVo = resultMap.get(username);
            if (userInfoVo != null) {
                resultList.add(userInfoVo);
            }
        }
        return resultList;
    }

    private void verifyDataOperationTask(AutoTaskConfig taskConfig) {
        String errorMessage;
        if (taskConfig.getActionType().equals(FlowAutoActionType.ADD_NEW)) {
            this.verifyDestTableNameEmpty(taskConfig);
            if (CollUtil.isEmpty(taskConfig.getInsertDataList())) {
                errorMessage = StrFormatter.format("任务 [{}] 的数据新增配置不能为空！", taskConfig.getTaskName());
                throw new MyRuntimeException(errorMessage);
            }
        } else if (taskConfig.getActionType().equals(FlowAutoActionType.UPDATE)) {
            this.verifyDestTableNameEmpty(taskConfig);
            if (CollUtil.isEmpty(taskConfig.getUpdateDataList())) {
                errorMessage = StrFormatter.format("任务 [{}] 的数据更新配置不能为空！", taskConfig.getTaskName());
                throw new MyRuntimeException(errorMessage);
            }
        } else if (taskConfig.getActionType().equals(FlowAutoActionType.DELETE)) {
            this.verifyDestTableNameEmpty(taskConfig);
        }
    }

    private void verifyDestTableNameEmpty(AutoTaskConfig taskConfig) {
        if (StrUtil.isBlank(taskConfig.getDestTableName())) {
            String errorMessage = StrFormatter.format("任务 [{}] 的目标表不能为空！", taskConfig.getTaskName());
            throw new MyRuntimeException(errorMessage);
        }
    }

    private void verifySrcTableNameEmpty(AutoTaskConfig taskConfig) {
        if (StrUtil.isBlank(taskConfig.getSrcTableName())) {
            String errorMessage = StrFormatter.format("任务 [{}] 的源表不能为空！", taskConfig.getTaskName());
            throw new MyRuntimeException(errorMessage);
        }
    }

    private void verifyHttpConfig(AutoTaskConfig taskConfig) {
        if (StrUtil.isBlank(taskConfig.getHttpRequestInfo().getUrl())) {
            String errorMessage = StrFormatter.format("任务 [{}] 的URL不能为空！", taskConfig.getTaskName());
            throw new MyRuntimeException(errorMessage);
        }
    }

    private void verifyRedisCacheConfig(AutoTaskConfig taskConfig) {
        String errorMessage;
        if (BooleanUtil.isFalse(taskConfig.getCacheKeyFromSql())) {
            if (StrUtil.isBlank(taskConfig.getCacheKey())) {
                errorMessage = StrFormatter.format("任务 [{}] 的缓存键配置不能为空！", taskConfig.getTaskName());
                throw new MyRuntimeException(errorMessage);
            }
            if (taskConfig.getActionType().equals(FlowAutoActionType.LOAD_CACHE)
                    && StrUtil.isBlank(taskConfig.getCacheValue())) {
                errorMessage = StrFormatter.format("任务 [{}] 的缓存值配置不能为空！", taskConfig.getTaskName());
                throw new MyRuntimeException(errorMessage);
            }
            return;
        }
        if (CollUtil.isEmpty(taskConfig.getCacheKeyFieldNames())) {
            errorMessage = StrFormatter.format("任务 [{}] 的缓存键字段配置不能为空！", taskConfig.getTaskName());
            throw new MyRuntimeException(errorMessage);
        }
        if (taskConfig.getActionType().equals(FlowAutoActionType.LOAD_CACHE)
                && CollUtil.isEmpty(taskConfig.getCacheKeyFieldNames())) {
            errorMessage = StrFormatter.format("任务 [{}] 的缓存值字段配置不能为空！", taskConfig.getTaskName());
            throw new MyRuntimeException(errorMessage);
        }
        this.verifySrcTableNameEmpty(taskConfig);
    }

    private void verifyMqConfig(AutoTaskConfig taskConfig) {
        String errorMessage;
        if (StrUtil.isBlank(taskConfig.getMessageTopic())) {
            errorMessage = StrFormatter.format("任务 [{}] 的消息主题配置不能为空！", taskConfig.getTaskName());
            throw new MyRuntimeException(errorMessage);
        }
        if (StrUtil.isBlank(taskConfig.getMessageData())) {
            errorMessage = StrFormatter.format("任务 [{}] 的消息数据配置不能为空！", taskConfig.getTaskName());
            throw new MyRuntimeException(errorMessage);
        }
    }

    private void buildUserMapList(
            List<FlowUserInfoVo> userInfoList, Set<String> loginNameSet, List<FlowUserInfoVo> userMapList) {
        if (CollUtil.isEmpty(userInfoList)) {
            return;
        }
        for (FlowUserInfoVo userInfo : userInfoList) {
            if (!loginNameSet.contains(userInfo.getLoginName())) {
                loginNameSet.add(userInfo.getLoginName());
                userMapList.add(userInfo);
            }
        }
    }

    private FlowTaskExt buildTaskExtByAutoTask(Task task) {
        FlowTaskExt flowTaskExt = new FlowTaskExt();
        flowTaskExt.setTaskId(task.getId());
        flowTaskExt.setGroupType(FlowConstant.GROUP_TYPE_AUTO_EXEC);
        Map<String, List<ExtensionElement>> extensionMap = task.getExtensionElements();
        List<ExtensionElement> autoConfigElements = extensionMap.get("taskInfo");
        if (CollUtil.isEmpty(autoConfigElements)) {
            return flowTaskExt;
        }
        ExtensionElement autoConfigElement = autoConfigElements.get(0);
        Map<String, List<ExtensionAttribute>> attributesMap = autoConfigElement.getAttributes();
        if (attributesMap != null) {
            List<ExtensionAttribute> attributes = attributesMap.get("data");
            if (CollUtil.isNotEmpty(attributes)) {
                ExtensionAttribute attribute = attributes.get(0);
                if (StrUtil.isNotBlank(attribute.getValue())) {
                    AutoTaskConfig taskConfig = JSONObject.parseObject(attribute.getValue(), AutoTaskConfig.class);
                    taskConfig.setTaskKey(task.getId());
                    taskConfig.setTaskName(task.getName());
                    flowTaskExt.setAutoConfigJson(JSON.toJSONString(taskConfig));
                }
            }
        }
        return flowTaskExt;
    }

    private void doBuildTaskExtList(FlowElement element, List<FlowTaskExt> flowTaskExtList) {
        if (element instanceof UserTask) {
            FlowTaskExt flowTaskExt = this.buildTaskExtByUserTask((UserTask) element);
            flowTaskExtList.add(flowTaskExt);
        } else if (element instanceof ServiceTask || element instanceof ReceiveTask) {
            FlowTaskExt flowTaskExt = this.buildTaskExtByAutoTask((Task) element);
            flowTaskExtList.add(flowTaskExt);
        } else if (element instanceof SubProcess) {
            SubProcess subProcess = (SubProcess) element;
            for (FlowElement flowElement : subProcess.getFlowElements()) {
                this.doBuildTaskExtList(flowElement, flowTaskExtList);
            }
        }
    }

    private void buildFlowUserInfoListByDeptAndRoleIds(
            FlowTaskExt flowTaskExt, Set<String> loginNameSet, List<FlowUserInfoVo> resultUserMapList) {
        BaseFlowIdentityExtHelper flowIdentityExtHelper = flowCustomExtFactory.getFlowIdentityExtHelper();
        if (StrUtil.isNotBlank(flowTaskExt.getDeptIds())) {
            Set<String> deptIdSet = CollUtil.newHashSet(StrUtil.split(flowTaskExt.getDeptIds(), ','));
            List<FlowUserInfoVo> userInfoList = flowIdentityExtHelper.getUserInfoListByDeptIds(deptIdSet);
            this.buildUserMapList(userInfoList, loginNameSet, resultUserMapList);
        }
        if (StrUtil.isNotBlank(flowTaskExt.getRoleIds())) {
            Set<String> roleIdSet = CollUtil.newHashSet(StrUtil.split(flowTaskExt.getRoleIds(), ','));
            List<FlowUserInfoVo> userInfoList = flowIdentityExtHelper.getUserInfoListByRoleIds(roleIdSet);
            this.buildUserMapList(userInfoList, loginNameSet, resultUserMapList);
        }
    }

    private void buildFlowTaskTimeoutExtensionData(
            Map<String, List<ExtensionAttribute>> attributeMap, JSONObject extraDataJson) {
        List<ExtensionAttribute> timeoutHandleWayAttributes = attributeMap.get(FlowConstant.TASK_TIMEOUT_HANDLE_WAY);
        if (CollUtil.isNotEmpty(timeoutHandleWayAttributes)) {
            String handleWay = timeoutHandleWayAttributes.get(0).getValue();
            extraDataJson.put(FlowConstant.TASK_TIMEOUT_HANDLE_WAY, handleWay);
            List<ExtensionAttribute> timeoutHoursAttributes = attributeMap.get(FlowConstant.TASK_TIMEOUT_HOURS);
            if (CollUtil.isEmpty(timeoutHoursAttributes)) {
                throw new MyRuntimeException("没有设置任务超时小时数！");
            }
            Integer timeoutHours = Integer.valueOf(timeoutHoursAttributes.get(0).getValue());
            extraDataJson.put(FlowConstant.TASK_TIMEOUT_HOURS, timeoutHours);
            if (StrUtil.equals(handleWay, FlowUserTaskExtData.TIMEOUT_AUTO_COMPLETE)) {
                List<ExtensionAttribute> defaultAssigneeAttributes =
                        attributeMap.get(FlowConstant.TASK_TIMEOUT_DEFAULT_ASSIGNEE);
                if (CollUtil.isEmpty(defaultAssigneeAttributes)) {
                    throw new MyRuntimeException("没有设置超时任务处理人！");
                }
                extraDataJson.put(FlowConstant.TASK_TIMEOUT_DEFAULT_ASSIGNEE, defaultAssigneeAttributes.get(0).getValue());
            }
        }
    }

    private void buildFlowTaskEmptyUserExtensionData(
            Map<String, List<ExtensionAttribute>> attributeMap, JSONObject extraDataJson) {
        List<ExtensionAttribute> emptyUserHandleWayAttributes = attributeMap.get(FlowConstant.EMPTY_USER_HANDLE_WAY);
        if (CollUtil.isNotEmpty(emptyUserHandleWayAttributes)) {
            String handleWay = emptyUserHandleWayAttributes.get(0).getValue();
            extraDataJson.put(FlowConstant.EMPTY_USER_HANDLE_WAY, handleWay);
            if (StrUtil.equals(handleWay, FlowUserTaskExtData.EMPTY_USER_TO_ASSIGNEE)) {
                List<ExtensionAttribute> emptyUserToAssigneeAttributes = attributeMap.get(FlowConstant.EMPTY_USER_TO_ASSIGNEE);
                if (CollUtil.isEmpty(emptyUserToAssigneeAttributes)) {
                    throw new MyRuntimeException("没有设置空审批人的指定处理人！");
                }
                extraDataJson.put(FlowConstant.EMPTY_USER_TO_ASSIGNEE, emptyUserToAssigneeAttributes.get(0).getValue());
            }
        }
    }

    private JSONObject buildFlowTaskExtensionData(UserTask userTask) {
        Map<String, List<ExtensionAttribute>> attributeMap = userTask.getAttributes();
        if (attributeMap == null) {
            attributeMap = new HashMap<>();
        }
        JSONObject extraDataJson = this.buildFlowElementExtToJson(userTask);
        if (extraDataJson == null) {
            extraDataJson = new JSONObject();
        }
        this.buildFlowTaskTimeoutExtensionData(attributeMap, extraDataJson);
        this.buildFlowTaskEmptyUserExtensionData(attributeMap, extraDataJson);
        List<ExtensionAttribute> rejectTypeAttributes = attributeMap.get(FlowConstant.USER_TASK_REJECT_TYPE_KEY);
        if (CollUtil.isNotEmpty(rejectTypeAttributes)) {
            extraDataJson.put(FlowConstant.USER_TASK_REJECT_TYPE_KEY, rejectTypeAttributes.get(0).getValue());
        }
        Map<String, List<ExtensionElement>> extensionMap = userTask.getExtensionElements();
        if (MapUtil.isNotEmpty(extensionMap)) {
            List<ExtensionElement> messageElements =
                    this.getMyExtensionElementList(extensionMap, "message", null);
            if (CollUtil.isNotEmpty(messageElements)) {
                ExtensionElement e = messageElements.get(0);
                String sendMessageType = e.getAttributeValue(null, "sendMessageType");
                if (StrUtil.isNotBlank(sendMessageType)) {
                    extraDataJson.put(FlowConstant.USER_TASK_NOTIFY_TYPES_KEY, StrUtil.split(sendMessageType, StrUtil.COMMA));
                }
                String notifyMessage = e.getAttributeValue(null, "notifyMessage");
                if (StrUtil.isNotBlank(notifyMessage)) {
                    extraDataJson.put(FlowConstant.USER_TASK_NOTIFY_MSG_KEY, notifyMessage);
                }
            }
        }
        if (userTask.getLoopCharacteristics() != null) {
            extraDataJson.put("multiInstance", true);
            extraDataJson.put("sequenceMultiInstance", userTask.getLoopCharacteristics().isSequential());
        }
        return extraDataJson;
    }

    private JSONObject buildUserCandidateGroupsExtensionElement(Map<String, List<ExtensionElement>> extensionMap) {
        JSONObject jsonData = null;
        List<ExtensionElement> elementCandidateGroupsList = extensionMap.get("userCandidateGroups");
        if (CollUtil.isEmpty(elementCandidateGroupsList)) {
            return jsonData;
        }
        jsonData = new JSONObject();
        ExtensionElement ee = elementCandidateGroupsList.get(0);
        jsonData.put(TYPE, ee.getAttributeValue(null, TYPE));
        jsonData.put(VALUE, ee.getAttributeValue(null, VALUE));
        return jsonData;
    }

    private JSONObject buildAssigneeListExtensionElement(Map<String, List<ExtensionElement>> extensionMap) {
        JSONObject jsonData = null;
        List<ExtensionElement> elementAssigneeList = extensionMap.get("assigneeList");
        if (CollUtil.isEmpty(elementAssigneeList)) {
            return jsonData;
        }
        ExtensionElement ee = elementAssigneeList.get(0);
        Map<String, List<ExtensionElement>> childExtensionMap = ee.getChildElements();
        if (MapUtil.isEmpty(childExtensionMap)) {
            return jsonData;
        }
        List<ExtensionElement> assigneeElements = childExtensionMap.get("assignee");
        if (CollUtil.isEmpty(assigneeElements)) {
            return jsonData;
        }
        JSONArray assigneeIdArray = new JSONArray();
        for (ExtensionElement e : assigneeElements) {
            assigneeIdArray.add(e.getAttributeValue(null, ID));
        }
        jsonData = new JSONObject();
        String assigneeType = ee.getAttributeValue(null, TYPE);
        jsonData.put("assigneeType", assigneeType);
        jsonData.put("assigneeList", assigneeIdArray);
        return jsonData;
    }

    private List<JSONObject> buildDeptPostListExtensionElement(Map<String, List<ExtensionElement>> extensionMap) {
        List<ExtensionElement> deptPostElements =
                this.getMyExtensionElementList(extensionMap, "deptPostList", "deptPost");
        if (CollUtil.isEmpty(deptPostElements)) {
            return Collections.emptyList();
        }
        List<JSONObject> resultList = new LinkedList<>();
        for (ExtensionElement e : deptPostElements) {
            JSONObject deptPostJsonData = new JSONObject();
            deptPostJsonData.put(ID, e.getAttributeValue(null, ID));
            deptPostJsonData.put(TYPE, e.getAttributeValue(null, TYPE));
            String postId = e.getAttributeValue(null, "postId");
            if (postId != null) {
                deptPostJsonData.put("postId", postId);
            }
            String deptPostId = e.getAttributeValue(null, "deptPostId");
            if (deptPostId != null) {
                deptPostJsonData.put("deptPostId", deptPostId);
            }
            resultList.add(deptPostJsonData);
        }
        return resultList;
    }

    private List<JSONObject> buildCopyListExtensionElement(Map<String, List<ExtensionElement>> extensionMap) {
        List<ExtensionElement> copyElements =
                this.getMyExtensionElementList(extensionMap, "copyItemList", "copyItem");
        if (CollUtil.isEmpty(copyElements)) {
            return Collections.emptyList();
        }
        List<JSONObject> resultList = new LinkedList<>();
        for (ExtensionElement e : copyElements) {
            JSONObject copyJsonData = new JSONObject();
            String type = e.getAttributeValue(null, TYPE);
            copyJsonData.put(TYPE, type);
            if (!StrUtil.equalsAny(type, FlowConstant.GROUP_TYPE_DEPT_POST_LEADER_VAR,
                    FlowConstant.GROUP_TYPE_UP_DEPT_POST_LEADER_VAR,
                    FlowConstant.GROUP_TYPE_USER_VAR,
                    FlowConstant.GROUP_TYPE_ROLE_VAR,
                    FlowConstant.GROUP_TYPE_DEPT_VAR,
                    FlowConstant.GROUP_TYPE_DEPT_POST_VAR,
                    FlowConstant.GROUP_TYPE_ALL_DEPT_POST_VAR,
                    FlowConstant.GROUP_TYPE_SIBLING_DEPT_POST_VAR,
                    FlowConstant.GROUP_TYPE_SELF_DEPT_POST_VAR,
                    FlowConstant.GROUP_TYPE_UP_DEPT_POST_VAR)) {
                throw new MyRuntimeException("Invalid TYPE [" + type + " ] for CopyItenList Extension!");
            }
            String id = e.getAttributeValue(null, ID);
            if (StrUtil.isNotBlank(id)) {
                copyJsonData.put(ID, id);
            }
            resultList.add(copyJsonData);
        }
        return resultList;
    }

    private List<ExtensionElement> getMyExtensionElementList(
            Map<String, List<ExtensionElement>> extensionMap, String rootName, String childName) {
        if (extensionMap == null) {
            return Collections.emptyList();
        }
        List<ExtensionElement> elementList = extensionMap.get(rootName);
        if (CollUtil.isEmpty(elementList)) {
            return Collections.emptyList();
        }
        if (StrUtil.isBlank(childName)) {
            return elementList;
        }
        ExtensionElement ee = elementList.get(0);
        Map<String, List<ExtensionElement>> childExtensionMap = ee.getChildElements();
        if (MapUtil.isEmpty(childExtensionMap)) {
            return Collections.emptyList();
        }
        List<ExtensionElement> childrenElements = childExtensionMap.get(childName);
        if (CollUtil.isEmpty(childrenElements)) {
            return Collections.emptyList();
        }
        return childrenElements;
    }

    private List<String> getAssigneeList(String executionId, String taskId) {
        FlowMultiInstanceTrans flowMultiInstanceTrans =
                flowMultiInstanceTransService.getByExecutionId(executionId, taskId);
        String multiInstanceExecId;
        if (flowMultiInstanceTrans == null) {
            multiInstanceExecId = flowApiService.getTaskVariableStringWithSafe(
                    taskId, FlowConstant.MULTI_SIGN_TASK_EXECUTION_ID_VAR);
        } else {
            multiInstanceExecId = flowMultiInstanceTrans.getMultiInstanceExecId();
        }
        flowMultiInstanceTrans =
                flowMultiInstanceTransService.getWithAssigneeListByMultiInstanceExecId(multiInstanceExecId);
        return flowMultiInstanceTrans == null ? null
                : StrUtil.split(flowMultiInstanceTrans.getAssigneeList(), ",");
    }

    private boolean buildTransferUserList(TaskInfo taskInfo, List<FlowUserInfoVo> resultUserMapList) {
        BaseFlowIdentityExtHelper flowIdentityExtHelper = flowCustomExtFactory.getFlowIdentityExtHelper();
        List<FlowTaskComment> taskCommentList = flowTaskCommentService.getFlowTaskCommentListByExecutionId(
                taskInfo.getProcessInstanceId(), taskInfo.getId(), taskInfo.getExecutionId());
        if (CollUtil.isEmpty(taskCommentList)) {
            return false;
        }
        FlowTaskComment transferComment = null;
        for (int i = taskCommentList.size() - 1; i >= 0; i--) {
            FlowTaskComment comment = taskCommentList.get(i);
            if (StrUtil.equalsAny(comment.getApprovalType(),
                    FlowApprovalType.TRANSFER, FlowApprovalType.INTERVENE)) {
                transferComment = comment;
                break;
            }
        }
        if (transferComment == null || StrUtil.isBlank(transferComment.getDelegateAssignee())) {
            return false;
        }
        Set<String> loginNameSet = new HashSet<>(StrUtil.split(transferComment.getDelegateAssignee(), ","));
        resultUserMapList.addAll(flowIdentityExtHelper.getUserInfoListByUsernameSet(loginNameSet));
        return true;
    }
}
