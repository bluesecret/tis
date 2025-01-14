package io.wangk.peekaboo.common.flow.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 流程任务的批注。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Schema(description = "流程任务的批注")
@Data
public class FlowTaskCommentDto {

    /**
     * 流程任务触发按钮类型，内置值可参考FlowTaskButton。
     */
    @Schema(description = "流程任务触发按钮类型")
    @NotNull(message = "数据验证失败，任务的审批类型不能为空！")
    private String approvalType;

    /**
     * 流程任务的批注内容。
     */
    @Schema(description = "流程任务的批注内容")
    @NotBlank(message = "数据验证失败，任务审批内容不能为空！")
    private String taskComment;

    /**
     * 委托指定人，比如加签、转办等。
     */
    @Schema(description = "委托指定人，比如加签、转办等")
    private String delegateAssignee;
}
