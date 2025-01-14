package io.wangk.peekaboo.common.flow.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * 流程任务消息所属用户的操作表。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
@TableName(value = "zz_flow_msg_identity_operation")
public class FlowMessageIdentityOperation {

    /**
     * 主键Id。
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 任务消息Id。
     */
    @TableField(value = "message_id")
    private Long messageId;

    /**
     * 用户登录名。
     */
    @TableField(value = "login_name")
    private String loginName;

    /**
     * 操作类型。
     * 常量值参考FlowMessageOperationType对象。
     */
    @TableField(value = "operation_type")
    private Integer operationType;

    /**
     * 操作时间。
     */
    @TableField(value = "operation_time")
    private Date operationTime;
}
