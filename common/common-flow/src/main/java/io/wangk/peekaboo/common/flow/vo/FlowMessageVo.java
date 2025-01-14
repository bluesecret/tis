package io.wangk.peekaboo.common.flow.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 工作流通知消息Vo对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Schema(description = "工作流通知消息Vo对象")
@Data
public class FlowMessageVo {

    /**
     * 主键Id。
     */
    @Schema(description = "主键Id")
    private Long messageId;

    /**
     * 消息类型。
     */
    @Schema(description = "消息类型")
    private Integer messageType;

    /**
     * 消息内容。
     */
    @Schema(description = "消息内容")
    private String messageContent;

    /**
     * 催办次数。
     */
    @Schema(description = "催办次数")
    private Integer remindCount;

    /**
     * 工单Id。
     */
    @Schema(description = "工单Id")
    private Long workOrderId;

    /**
     * 流程定义Id。
     */
    @Schema(description = "流程定义Id")
    private String processDefinitionId;

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
     * 流程实例Id。
     */
    @Schema(description = "流程实例Id")
    private String processInstanceId;

    /**
     * 流程实例发起者。
     */
    @Schema(description = "流程实例发起者")
    private String processInstanceInitiator;

    /**
     * 流程任务Id。
     */
    @Schema(description = "流程任务Id")
    private String taskId;

    /**
     * 流程任务定义标识。
     */
    @Schema(description = "流程任务定义标识")
    private String taskDefinitionKey;

    /**
     * 流程任务名称。
     */
    @Schema(description = "流程任务名称")
    private String taskName;

    /**
     * 创建时间。
     */
    @Schema(description = "创建时间")
    private Date taskStartTime;

    /**
     * 业务数据快照。
     */
    @Schema(description = "业务数据快照")
    private String businessDataShot;

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
     * 创建者显示名。
     */
    @Schema(description = "创建者显示名")
    private String createUsername;
}
