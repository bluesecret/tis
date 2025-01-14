package io.wangk.peekaboo.common.report.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * 大屏可视化实体对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
@TableName(value = "zz_report_visualization")
public class ReportVisualization {

    /**
     * 主键Id。
     */
    @TableId(value = "visual_id")
    private Long visualId;

    /**
     * 名称。
     */
    @TableField(value = "visual_name")
    private String visualName;

    /**
     * 可视化配置。
     */
    @TableField(value = "config_json")
    private String configJson;

    /**
     * 封面图的BASE64。
     */
    @TableField(value = "cover_img")
    private String coverImg;

    /**
     * 代码页。
     */
    @TableField(value = "code_page")
    private String codePage;

    /**
     * 发布状态。
     */
    @TableField(value = "publish_status")
    private Integer publishStatus;

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

    /**
     * 逻辑删除标记字段(1: 正常 -1: 已删除)。
     */
    @TableLogic
    @TableField(value = "deleted_flag")
    private Integer deletedFlag;
}
