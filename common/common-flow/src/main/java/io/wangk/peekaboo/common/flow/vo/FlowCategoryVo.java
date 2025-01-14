package io.wangk.peekaboo.common.flow.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 流程分类的Vo对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Schema(description = "流程分类的Vo对象")
@Data
public class FlowCategoryVo {

    /**
     * 主键Id。
     */
    @Schema(description = "主键Id")
    private Long categoryId;

    /**
     * 应用编码。为空时，表示非第三方应用接入。
     */
    @Schema(description = "应用编码")
    private String appCode;

    /**
     * 显示名称。
     */
    @Schema(description = "显示名称")
    private String name;

    /**
     * 分类编码。
     */
    @Schema(description = "分类编码")
    private String code;

    /**
     * 实现顺序。
     */
    @Schema(description = "实现顺序")
    private Integer showOrder;

    /**
     * 更新时间。
     */
    @Schema(description = "更新时间")
    private Date updateTime;

    /**
     * 更新者Id。
     */
    @Schema(description = "更新者Id")
    private Long updateUserId;

    /**
     * 创建时间。
     */
    @Schema(description = "创建时间")
    private Date createTime;

    /**
     * 创建者Id。
     */
    @Schema(description = "创建者Id")
    private Long createUserId;
}
