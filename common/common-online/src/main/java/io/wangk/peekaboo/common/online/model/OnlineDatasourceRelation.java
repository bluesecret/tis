package io.wangk.peekaboo.common.online.model;

import com.baomidou.mybatisplus.annotation.*;
import io.wangk.peekaboo.common.core.annotation.RelationConstDict;
import io.wangk.peekaboo.common.core.annotation.RelationDict;
import io.wangk.peekaboo.common.core.annotation.RelationOneToOne;
import io.wangk.peekaboo.common.online.model.constant.RelationType;
import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 * 在线表单的数据源关联实体对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
@TableName(value = "zz_online_datasource_relation")
public class OnlineDatasourceRelation {

    /**
     * 主键Id。
     */
    @TableId(value = "relation_id")
    private Long relationId;

    /**
     * 应用Id。为空时，表示非第三方应用接入。
     */
    @TableField(value = "app_code")
    private String appCode;

    /**
     * 关联名称。
     */
    @TableField(value = "relation_name")
    private String relationName;

    /**
     * 变量名。
     */
    @TableField(value = "variable_name")
    private String variableName;

    /**
     * 主数据源Id。
     */
    @TableField(value = "datasource_id")
    private Long datasourceId;

    /**
     * 关联类型。
     */
    @TableField(value = "relation_type")
    private Integer relationType;

    /**
     * 主表关联字段Id。
     */
    @TableField(value = "master_column_id")
    private Long masterColumnId;

    /**
     * 从表Id。
     */
    @TableField(value = "slave_table_id")
    private Long slaveTableId;

    /**
     * 从表关联字段Id。
     */
    @TableField(value = "slave_column_id")
    private Long slaveColumnId;

    /**
     * 删除主表的时候是否级联删除一对一和一对多的从表数据，多对多只是删除关联，不受到这个标记的影响。。
     */
    @TableField(value = "cascade_delete")
    private Boolean cascadeDelete;

    /**
     * 是否左连接。
     */
    @TableField(value = "left_join")
    private Boolean leftJoin;

    /**
     * 关联过滤条件。
     */
    @TableField(value = "relation_filter_sql")
    private String relationFilterSql;

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

    @RelationOneToOne(
            masterIdField = "masterColumnId",
            slaveModelClass = OnlineColumn.class,
            slaveIdField = "columnId")
    @TableField(exist = false)
    private OnlineColumn masterColumn;

    @RelationOneToOne(
            masterIdField = "slaveTableId",
            slaveModelClass = OnlineTable.class,
            slaveIdField = "tableId")
    @TableField(exist = false)
    private OnlineTable slaveTable;

    @RelationOneToOne(
            masterIdField = "slaveColumnId",
            slaveModelClass = OnlineColumn.class,
            slaveIdField = "columnId")
    @TableField(exist = false)
    private OnlineColumn slaveColumn;

    @RelationDict(
            masterIdField = "masterColumnId",
            equalOneToOneRelationField = "onlineColumn",
            slaveModelClass = OnlineColumn.class,
            slaveIdField = "columnId",
            slaveNameField = "columnName")
    @TableField(exist = false)
    private Map<String, Object> masterColumnIdDictMap;

    @RelationDict(
            masterIdField = "slaveTableId",
            equalOneToOneRelationField = "onlineTable",
            slaveModelClass = OnlineTable.class,
            slaveIdField = "tableId",
            slaveNameField = "modelName")
    @TableField(exist = false)
    private Map<String, Object> slaveTableIdDictMap;

    @RelationDict(
            masterIdField = "slaveColumnId",
            equalOneToOneRelationField = "onlineColumn",
            slaveModelClass = OnlineColumn.class,
            slaveIdField = "columnId",
            slaveNameField = "columnName")
    @TableField(exist = false)
    private Map<String, Object> slaveColumnIdDictMap;

    @RelationConstDict(
            masterIdField = "relationType",
            constantDictClass = RelationType.class)
    @TableField(exist = false)
    private Map<String, Object> relationTypeDictMap;
}
