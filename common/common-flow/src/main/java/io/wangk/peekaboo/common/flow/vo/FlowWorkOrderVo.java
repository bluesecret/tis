package io.wangk.peekaboo.common.flow.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import com.alibaba.fastjson.JSONArray;
import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 * 工作流工单VO对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Schema(description = "工作流工单Vo对象")
@Data
public class FlowWorkOrderVo {

    /**
     * 主键Id。
     */
    @Schema(description = "主键Id")
    private Long workOrderId;

    /**
     * 应用编码。为空时，表示非第三方应用接入。
     */
    @Schema(description = "应用编码")
    private String appCode;

    /**
     * 工单编码字段。
     */
    @Schema(description = "工单编码字段")
    private String workOrderCode;

    /**
     * 流程定义标识。
     */
    @Schema(description = "流程定义标识")
    private String processDefinitionKey;

    /**
     * 流程名称。
     */
    @Schema(description = "流程名称")
    private String processDefinitionName;

    /**
     * 流程引擎的定义Id。
     */
    @Schema(description = "流程引擎的定义Id")
    private String processDefinitionId;

    /**
     * 流程实例Id。
     */
    @Schema(description = "流程实例Id")
    private String processInstanceId;

    /**
     * 在线表单的主表Id。
     */
    @Schema(description = "在线表单的主表Id")
    private Long onlineTableId;

    /**
     * 业务主键值。
     */
    @Schema(description = "业务主键值")
    private String businessKey;

    /**
     * 最近的审批状态。
     */
    @Schema(description = "最近的审批状态")
    private Integer latestApprovalStatus;

    /**
     * 流程状态。参考FlowTaskStatus常量值对象。
     */
    @Schema(description = "流程状态")
    private Integer flowStatus;

    /**
     * 提交用户登录名称。
     */
    @Schema(description = "提交用户登录名称")
    private String submitUsername;

    /**
     * 提交用户所在部门Id。
     */
    @Schema(description = "提交用户所在部门Id")
    private Long deptId;

    /**
     * 更新时间。
     */
    @Schema(description = "更新时间")
    private Date updateTime;

    /**
     * 更新者Id。
     */
    @Schema(description = "更新者Id")
    private Long updateUserId;

    /**
     * 创建时间。
     */
    @Schema(description = "创建时间")
    private Date createTime;

    /**
     * 创建者Id。
     */
    @Schema(description = "创建者Id")
    private Long createUserId;

    /**
     * latestApprovalStatus 关联的字典数据。
     */
    @Schema(description = "latestApprovalStatus 常量字典关联数据")
    private Map<String, Object> latestApprovalStatusDictMap;

    /**
     * flowStatus 常量字典关联数据。
     */
    @Schema(description = "flowStatus 常量字典关联数据")
    private Map<String, Object> flowStatusDictMap;

    /**
     * 用户的显示名。
     */
    @Schema(description = "用户的显示名")
    private String userShowName;

    /**
     * FlowEntryPublish对象中的同名字段。
     */
    @Schema(description = "FlowEntryPublish对象中的同名字段")
    private String initTaskInfo;

    /**
     * 当前实例的运行时任务列表。
     * 正常情况下只有一个，在并行网关下可能存在多个。
     */
    @Schema(description = "实例的运行时任务列表")
    private JSONArray runtimeTaskInfoList;

    /**
     * 业务主表数据。
     */
    @Schema(description = "业务主表数据")
    private Map<String, Object> masterData;

    /**
     * 草稿数据。
     */
    @Schema(description = "草稿数据")
    private String draftData;
}
