package io.wangk.peekaboo.common.flow.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * 流程任务消息的候选身份实体对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
@TableName(value = "zz_flow_msg_candidate_identity")
public class FlowMessageCandidateIdentity {

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
     * 候选身份类型。
     */
    @TableField(value = "candidate_type")
    private String candidateType;

    /**
     * 候选身份Id。
     */
    @TableField(value = "candidate_id")
    private String candidateId;
}
