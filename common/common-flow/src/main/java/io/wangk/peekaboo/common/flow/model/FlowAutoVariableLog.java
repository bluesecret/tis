package io.wangk.peekaboo.common.flow.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * 自动化流程变量实体。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
@TableName(value = "zz_flow_auto_variable_log")
public class FlowAutoVariableLog {

    /**
     * 主键Id。
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 流程实例Id。
     */
    @TableField(value = "process_instance_id")
    private String processInstanceId;

    /**
     * 执行实例Id。
     */
    @TableField(value = "execution_id")
    private String executionId;

    /**
     * 任务Id。
     */
    @TableField(value = "task_id")
    private String taskId;

    /**
     * 任务标识。
     */
    @TableField(value = "task_key")
    private String taskKey;

    /**
     * 当前请求的traceId。
     */
    @TableField(value = "trace_id")
    private String traceId;

    /**
     * 变量数据。
     */
    @TableField(value = "variable_data")
    private String variableData;

    /**
     * 创建时间。
     */
    @TableField(value = "create_time")
    private Date createTime;
}
