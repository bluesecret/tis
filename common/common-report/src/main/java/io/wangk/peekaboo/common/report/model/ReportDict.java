package io.wangk.peekaboo.common.report.model;

import com.baomidou.mybatisplus.annotation.*;
import io.wangk.peekaboo.common.core.annotation.RelationConstDict;
import io.wangk.peekaboo.common.core.annotation.RelationDict;
import io.wangk.peekaboo.common.core.constant.DictType;
import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 * 报表字典实体对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
@TableName(value = "zz_report_dict")
public class ReportDict {

    /**
     * 主键Id。
     */
    @TableId(value = "dict_id")
    private Long dictId;

    /**
     * 应用编码。为空时，表示非第三方应用接入。
     */
    @TableField(value = "app_code")
    private String appCode;

    /**
     * 数据库链接Id。
     */
    @TableField(value = "dblink_id")
    private Long dblinkId;

    /**
     * 字典名称。
     */
    @TableField(value = "dict_name")
    private String dictName;

    /**
     * 字典类型。
     */
    @TableField(value = "dict_type")
    private Integer dictType;

    /**
     * 全局字典编码。
     */
    @TableField(value = "dict_code")
    private String dictCode;

    /**
     * 字典表名称。
     */
    @TableField(value = "table_name")
    private String tableName;

    /**
     * 是否树形标记。
     */
    @TableField(value = "tree_flag")
    private Boolean treeFlag;

    /**
     * 字典表键字段名称。
     */
    @TableField(value = "key_column_name")
    private String keyColumnName;

    /**
     * 字典值字段名称。
     */
    @TableField(value = "value_column_name")
    private String valueColumnName;

    /**
     * 字典表父键字段名称。
     */
    @TableField(value = "parent_key_column_name")
    private String parentKeyColumnName;

    /**
     * 逻辑删除字段。
     */
    @TableField(value = "deleted_column_name")
    private String deletedColumnName;

    /**
     * 租户过滤字段名称。
     */
    @TableField(value = "tenant_filter_column_name")
    private String tenantFilterColumnName;

    /**
     * 获取字典列表数据的url。
     */
    @TableField(value = "dict_list_url")
    private String dictListUrl;

    /**
     * 根据主键id批量获取字典数据的url。
     */
    @TableField(value = "dict_ids_url")
    private String dictIdsUrl;

    /**
     * 字典的JSON数据。
     */
    @TableField(value = "dict_data_json")
    private String dictDataJson;

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
     * 创建时间。
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 创建者。
     */
    @TableField(value = "create_user_id")
    private Long createUserId;

    @RelationConstDict(
            masterIdField = "dictType",
            constantDictClass = DictType.class)
    @TableField(exist = false)
    private Map<String, Object> dictTypeDictMap;

    @RelationDict(
            masterIdField = "dblinkId",
            slaveModelClass = ReportDblink.class,
            slaveIdField = "dblinkId",
            slaveNameField = "dblinkName")
    @TableField(exist = false)
    private Map<String, Object> dblinkIdDictMap;
}
