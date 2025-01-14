package io.wangk.peekaboo.common.report.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * 报表模块内部，所有分组对象的基类。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
public class BaseReportGroup {

    /**
     * 主键Id。
     */
    @TableId(value = "group_id")
    private Long groupId;

    /**
     * 应用编码。为空时，表示非第三方应用接入。
     */
    @TableField(value = "app_code")
    private String appCode;

    /**
     * 分组名称。
     */
    @TableField(value = "group_name")
    private String groupName;

    /**
     * 父级Id。
     */
    @TableField(value = "parent_id")
    private Long parentId;

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
