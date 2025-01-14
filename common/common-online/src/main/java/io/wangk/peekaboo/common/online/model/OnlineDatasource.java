package io.wangk.peekaboo.common.online.model;

import com.baomidou.mybatisplus.annotation.*;
import io.wangk.peekaboo.common.core.annotation.RelationDict;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 在线表单的数据源实体对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
@TableName(value = "zz_online_datasource")
public class OnlineDatasource {

    /**
     * 主键Id。
     */
    @TableId(value = "datasource_id")
    private Long datasourceId;

    /**
     * 应用编码。为空时，表示非第三方应用接入。
     */
    @TableField(value = "app_code")
    private String appCode;

    /**
     * 数据源名称。
     */
    @TableField(value = "datasource_name")
    private String datasourceName;

    /**
     * 数据源变量名，会成为数据访问url的一部分。
     */
    @TableField(value = "variable_name")
    private String variableName;

    /**
     * 数据库链接Id。
     */
    @TableField(value = "dblink_id")
    private Long dblinkId;

    /**
     * 主表Id。
     */
    @TableField(value = "master_table_id")
    private Long masterTableId;

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
     * datasourceId 的多对多关联的数据对象。
     */
    @TableField(exist = false)
    private OnlinePageDatasource onlinePageDatasource;

    /**
     * datasourceId 的多对多关联的数据对象。
     */
    @TableField(exist = false)
    private List<OnlineFormDatasource> onlineFormDatasourceList;

    @RelationDict(
            masterIdField = "masterTableId",
            slaveModelClass = OnlineTable.class,
            slaveIdField = "tableId",
            slaveNameField = "tableName")
    @TableField(exist = false)
    private Map<String, Object> masterTableIdDictMap;

    @TableField(exist = false)
    private OnlineTable masterTable;
}
