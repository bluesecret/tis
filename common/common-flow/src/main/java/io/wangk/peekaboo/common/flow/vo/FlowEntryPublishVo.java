package io.wangk.peekaboo.common.flow.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 流程发布信息的Vo对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Schema(description = "流程发布信息的Vo对象")
@Data
public class FlowEntryPublishVo {

    /**
     * 主键Id。
     */
    @Schema(description = "主键Id")
    private Long entryPublishId;

    /**
     * 发布版本。
     */
    @Schema(description = "发布版本")
    private Integer publishVersion;

    /**
     * 流程引擎中的流程定义Id。
     */
    @Schema(description = "流程引擎中的流程定义Id")
    private String processDefinitionId;

    /**
     * 激活状态。
     */
    @Schema(description = "激活状态")
    private Boolean activeStatus;

    /**
     * 是否为主版本。
     */
    @Schema(description = "是否为主版本")
    private Boolean mainVersion;

    /**
     * 创建者Id。
     */
    @Schema(description = "创建者Id")
    private Long createUserId;

    /**
     * 发布时间。
     */
    @Schema(description = "发布时间")
    private Date publishTime;
}
