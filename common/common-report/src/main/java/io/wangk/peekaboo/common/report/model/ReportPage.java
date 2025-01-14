package io.wangk.peekaboo.common.report.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * 报表页面实体对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
@TableName(value = "zz_report_page")
public class ReportPage {

    /**
     * 主键Id。
     */
    @TableId(value = "page_id")
    private Long pageId;

    /**
     * 应用编码。为空时，表示非第三方应用接入。
     */
    @TableField(value = "app_code")
    private String appCode;

    /**
     * 租户Id。非多租户系统该值为NULL。
     * 为了保持组件的兼容性，该字段始终为字符型。
     */
    @TableField(value = "tenant_id")
    private Long tenantId;

    /**
     * 具有唯一性的页面编码。
     */
    @TableField(value = "page_code")
    private String pageCode;

    /**
     * 名称。
     */
    @TableField(value = "page_name")
    private String pageName;

    /**
     * 页面分组Id。
     */
    @TableField(value = "group_id")
    private Long groupId;

    /**
     * 页面配置的JSON。
     */
    @TableField(value = "page_json")
    private String pageJson;

    /**
     * 表单组件JSON。
     */
    @TableField(value = "widget_json")
    private String widgetJson;

    /**
     * 表单参数JSON。
     */
    @TableField(value = "param_json")
    private String paramJson;

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
