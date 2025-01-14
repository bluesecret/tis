package io.wangk.peekaboo.common.flow.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

/**
 * 流程处理事务事件消费者流水实体。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
@AllArgsConstructor
@TableName(value = "zz_flow_trans_consumer")
public class FlowTransConsumer {

    /**
     * 流水Id。
     */
    @TableId(value = "trans_id")
    private Long transId;

    /**
     * 创建时间。
     */
    @TableField(value = "create_time")
    private Date createTime;
}
