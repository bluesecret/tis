package io.wangk.peekaboo.common.report.model;

import com.baomidou.mybatisplus.annotation.*;
import io.wangk.peekaboo.common.core.annotation.RelationConstDict;
import io.wangk.peekaboo.common.dbutil.constant.DblinkType;
import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 * 报表数据库链接实体对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
@TableName(value = "zz_report_dblink")
public class ReportDblink {

    /**
     * 主键Id。
     */
    @TableId(value = "dblink_id")
    private Long dblinkId;

    /**
     * 应用编码。为空时，表示非第三方应用接入。
     */
    @TableField(value = "app_code")
    private String appCode;

    /**
     * 数据库链接名称。
     */
    @TableField(value = "dblink_name")
    private String dblinkName;

    /**
     * 数据库链接描述。
     */
    @TableField(value = "dblink_description")
    private String dblinkDescription;

    /**
     * 数据库链接类型。
     */
    @TableField(value = "dblink_type")
    private Integer dblinkType;

    /**
     * 配置信息。
     */
    private String configuration;

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
     * 更新者。
     */
    @TableField(value = "update_user_id")
    private Long updateUserId;

    /**
     * 更新时间。
     */
    @TableField(value = "update_time")
    private Date updateTime;

    @RelationConstDict(
            masterIdField = "dblinkType",
            constantDictClass = DblinkType.class)
    @TableField(exist = false)
    private Map<String, Object> dblinkTypeDictMap;
}
