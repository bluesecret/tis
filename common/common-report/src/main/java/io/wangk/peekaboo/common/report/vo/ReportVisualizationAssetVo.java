package io.wangk.peekaboo.common.report.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 可视化素材Vo对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Schema(description = "可视化素材Vo对象")
@Data
public class ReportVisualizationAssetVo {

    /**
     * 主键Id。
     */
    @Schema(description = "主键Id")
    private Long assetId;

    /**
     * 主键Id。
     */
    @Schema(description = "可视化工程Id")
    private Long visualId;

    /**
     * 素材名称。
     */
    @Schema(description = "素材名称")
    private String assetName;

    /**
     * 缩略图BASE64。
     */
    @Schema(description = "缩略图BASE64")
    private String thumbnailImg;

    /**
     * 图的BASE64。
     */
    @Schema(description = "封面图的BASE64")
    private String assetImg;

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
