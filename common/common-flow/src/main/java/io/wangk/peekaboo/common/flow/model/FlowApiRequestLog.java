package io.wangk.peekaboo.common.flow.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * 流程API请求日志实体对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
@TableName(value = "zz_flow_api_request_log")
public class FlowApiRequestLog {

    /**
     * 主键Id。
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 业务端的请求Id。
     */
    @TableField(value = "request_id")
    private String requestId;

    /**
     * 流程实例Id。
     */
    @TableField(value = "process_instance_id")
    private String processInstanceId;

    /**
     * 流程任务Id。
     */
    @TableField(value = "task_id")
    private String taskId;

    /**
     * 流程任务标识。
     */
    @TableField(value = "task_key")
    private String taskKey;

    /**
     * 业务主键Id。
     */
    @TableField(value = "business_key")
    private String businessKey;

    /**
     * 创建时间。
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 创建者Id。
     */
    @TableField(value = "create_user_id")
    private Long createUserId;
}
