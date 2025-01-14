package io.wangk.peekaboo.common.online.model;

import com.baomidou.mybatisplus.annotation.*;
import io.wangk.peekaboo.common.core.annotation.RelationConstDict;
import io.wangk.peekaboo.common.online.model.constant.PageStatus;
import io.wangk.peekaboo.common.online.model.constant.PageType;
import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 * 在线表单所在页面实体对象。这里我们可以把页面理解为表单的容器。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
@TableName(value = "zz_online_page")
public class OnlinePage {

    /**
     * 主键Id。
     */
    @TableId(value = "page_id")
    private Long pageId;

    /**
     * 租户Id。
     */
    @TableField(value = "tenant_id")
    private Long tenantId;

    /**
     * 应用编码。为空时，表示非第三方应用接入。
     */
    @TableField(value = "app_code")
    private String appCode;

    /**
     * 页面编码。
     */
    @TableField(value = "page_code")
    private String pageCode;

    /**
     * 页面名称。
     */
    @TableField(value = "page_name")
    private String pageName;

    /**
     * 页面类型。
     */
    @TableField(value = "page_type")
    private Integer pageType;

    /**
     * 扩展数据。
     */
    @TableField(value = "extra_json")
    private String extraJson;

    /**
     * 页面编辑状态。
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 是否发布。
     */
    @TableField(value = "published")
    private Boolean published;

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

    @RelationConstDict(
            masterIdField = "pageType",
            constantDictClass = PageType.class)
    @TableField(exist = false)
    private Map<String, Object> pageTypeDictMap;

    @RelationConstDict(
            masterIdField = "status",
            constantDictClass = PageStatus.class)
    @TableField(exist = false)
    private Map<String, Object> statusDictMap;
}
