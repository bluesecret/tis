package io.wangk.peekaboo.common.flow.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * FlowTaskCommentVO对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Schema(description = "FlowTaskCommentVO对象")
@Data
public class FlowTaskCommentVo {

    /**
     * 主键Id。
     */
    @Schema(description = "主键Id")
    private Long id;

    /**
     * 流程实例Id。
     */
    @Schema(description = "流程实例Id")
    private String processInstanceId;

    /**
     * 任务Id。
     */
    @Schema(description = "任务Id")
    private String taskId;

    /**
     * 任务标识。
     */
    @Schema(description = "任务标识")
    private String taskKey;

    /**
     * 任务名称。
     */
    @Schema(description = "任务名称")
    private String taskName;

    /**
     * 任务的执行Id。
     */
    @Schema(description = "任务的执行Id")
    private String executionId;

    /**
     * 会签任务的执行Id。
     */
    @Schema(description = "会签任务的执行Id")
    private String multiInstanceExecId;

    /**
     * 审批类型。
     */
    @Schema(description = "审批类型")
    private String approvalType;

    /**
     * 批注内容。
     */
    @Schema(description = "批注内容")
    private String taskComment;

    /**
     * 委托指定人，比如加签、转办等。
     */
    @Schema(description = "委托指定人，比如加签、转办等")
    private String delegateAssignee;

    /**
     * 自定义数据。开发者可自行扩展，推荐使用JSON格式数据。
     */
    @Schema(description = "自定义数据")
    private String customBusinessData;

    /**
     * 审批人头像。
     */
    @Schema(description = "审批人头像")
    private String headImageUrl;

    /**
     * 创建者Id。
     */
    @Schema(description = "创建者Id")
    private Long createUserId;

    /**
     * 创建者登录名。
     */
    @Schema(description = "创建者登录名")
    private String createLoginName;

    /**
     * 创建者显示名。
     */
    @Schema(description = "创建者显示名")
    private String createUsername;

    /**
     * 创建时间。
     */
    @Schema(description = "创建时间")
    private Date createTime;
}
