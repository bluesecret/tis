package io.wangk.peekaboo.common.report.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 大屏可视化VO对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Schema(description = "大屏可视化VO对象")
@Data
public class ReportVisualizationVo {

    /**
     * 主键Id。
     */
    @Schema(description = "主键Id")
    private Long visualId;

    /**
     * 名称。
     */
    @Schema(description = "名称")
    private String visualName;

    /**
     * 可视化配置。
     */
    @Schema(description = "可视化配置")
    private String configJson;

    /**
     * 封面图的BASE64。
     */
    @Schema(description = "封面图的BASE64")
    private String coverImg;

    /**
     * 代码页。
     */
    @Schema(description = "代码页")
    private String codePage;

    /**
     * 发布状态。
     */
    @Schema(description = "发布状态 (0: 未发布，1: 已发布)")
    private Integer publishStatus;

    /**
     * 创建时间。
     */
    @Schema(description = "创建时间")
    private Date createTime;

    /**
     * 创建者。
     */
    @Schema(description = "创建者")
    private Long createUserId;

    /**
     * 更新时间。
     */
    @Schema(description = "更新时间")
    private Date updateTime;

    /**
     * 更新者。
     */
    @Schema(description = "更新者")
    private Long updateUserId;
}
