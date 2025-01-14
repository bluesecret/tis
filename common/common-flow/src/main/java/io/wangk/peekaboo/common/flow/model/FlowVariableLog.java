package io.wangk.peekaboo.common.flow.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * 流程变量日志实体对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
@TableName(value = "zz_flow_variable_log")
public class FlowVariableLog {

    /**
     * 主键Id。
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 流程定义键。
     */
    @TableField(value = "process_definition_key")
    private String processDefinitionKey;

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
     * 全部变量数据。
     */
    @TableField(value = "variable_data")
    private String variableData;

    /**
     * 创建时间。
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 过期时间。
     */
    @TableField(value = "expired_time")
    private Date expiredTime;
}
