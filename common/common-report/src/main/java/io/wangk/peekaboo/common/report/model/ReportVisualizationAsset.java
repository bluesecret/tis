package io.wangk.peekaboo.common.report.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * 可视化素材实体对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
@TableName(value = "zz_report_visualization_asset")
public class ReportVisualizationAsset {

    /**
     * 主键Id。
     */
    @TableId(value = "asset_id")
    private Long assetId;

    /**
     * 可视化项目Id，如果为NULL，为共有素材。
     */
    @TableField(value = "visual_id")
    private Long visualId;

    /**
     * 素材名称。
     */
    @TableField(value = "asset_name")
    private String assetName;

    /**
     * 缩略图BASE64。
     */
    @TableField(value = "thumbnail_img")
    private String thumbnailImg;

    /**
     * 图的BASE64。
     */
    @TableField(value = "asset_img")
    private String assetImg;

    /**
     * 创建时间。
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 创建者。
     */
    @TableField(value = "create_user_id")
    private Long createUserId;

    /**
     * 更新时间。
     */
    @TableField(value = "update_time")
    private Date updateTime;

    /**
     * 更新者。
     */
    @TableField(value = "update_user_id")
    private Long updateUserId;
}
