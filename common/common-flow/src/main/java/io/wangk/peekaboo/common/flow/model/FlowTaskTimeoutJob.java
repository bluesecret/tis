package io.wangk.peekaboo.common.flow.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * 超时任务作业实体对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
@TableName(value = "zz_flow_task_timeout_job")
public class FlowTaskTimeoutJob {

    /**
     * 主键Id。
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 流程定义Id。
     */
    @TableField(value = "process_definition_id")
    private String processDefinitionId;

    /**
     * 流程实例Id。
     */
    @TableField(value = "process_instance_id")
    private String processInstanceId;

    /**
     * 流程任务标识。
     */
    @TableField(value = "task_key")
    private String taskKey;

    /**
     * 流程任务Id。
     */
    @TableField(value = "task_id")
    private String taskId;

    /**
     * 超时的小时数。
     */
    @TableField(value = "timeout_hours")
    private Integer timeoutHours;

    /**
     * 超时处理方式。
     */
    @TableField(value = "handle_way")
    private String handleWay;

    /**
     * 超时处理缺省用户名。
     */
    @TableField(value = "default_assignee")
    private String defaultAssignee;

    /**
     * 处理错误信息。
     */
    @TableField(value = "error_message")
    private String errorMessage;

    /**
     * 执行状态。参考常量类FlowTaskTimeoutJobStatus的值。
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 执行时间。
     */
    @TableField(value = "exec_time")
    private Date execTime;

    /**
     * 创建时间。
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 更新时间。
     */
    @TableField(value = "update_time")
    private Date updateTime;
}
