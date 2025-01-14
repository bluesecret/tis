package io.wangk.peekaboo.common.flow.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 流程任务Vo对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Schema(description = "流程任务Vo对象")
@Data
public class FlowTaskVo {

    /**
     * 流程任务Id。
     */
    @Schema(description = "流程任务Id")
    private String taskId;

    /**
     * 流程任务名称。
     */
    @Schema(description = "流程任务名称")
    private String taskName;

    /**
     * 流程任务标识。
     */
    @Schema(description = "流程任务标识")
    private String taskKey;

    /**
     * 任务的表单信息。
     */
    @Schema(description = "任务的表单信息")
    private String taskFormKey;

    /**
     * 待办任务开始时间。
     */
    @Schema(description = "待办任务开始时间")
    private Date taskStartTime;

    /**
     * 流程Id。
     */
    @Schema(description = "流程Id")
    private Long entryId;

    /**
     * 流程定义Id。
     */
    @Schema(description = "流程定义Id")
    private String processDefinitionId;

    /**
     * 流程定义名称。
     */
    @Schema(description = "流程定义名称")
    private String processDefinitionName;

    /**
     * 流程定义标识。
     */
    @Schema(description = "流程定义标识")
    private String processDefinitionKey;

    /**
     * 流程定义版本。
     */
    @Schema(description = "流程定义版本")
    private Integer processDefinitionVersion;

    /**
     * 流程实例Id。
     */
    @Schema(description = "流程实例Id")
    private String processInstanceId;

    /**
     * 流程实例发起人。
     */
    @Schema(description = "流程实例发起人")
    private String processInstanceInitiator;

    /**
     * 流程实例发起人显示名。
     */
    @Schema(description = "流程实例发起人显示名")
    private String showName;

    /**
     * 用户头像信息。
     */
    @Schema(description = "用户头像信息")
    private String headImageUrl;

    /**
     * 流程实例创建时间。
     */
    @Schema(description = "流程实例创建时间")
    private Date processInstanceStartTime;

    /**
     * 流程实例主表业务数据主键。
     */
    @Schema(description = "流程实例主表业务数据主键")
    private String businessKey;

    /**
     * 工单编码。
     */
    @Schema(description = "工单编码")
    private String workOrderCode;

    /**
     * 是否为草稿状态。
     */
    @Schema(description = "是否为草稿状态")
    private Boolean isDraft;
}
