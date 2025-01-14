package io.wangk.peekaboo.common.report.model;

import com.baomidou.mybatisplus.annotation.*;
import io.wangk.peekaboo.common.core.annotation.RelationConstDict;
import io.wangk.peekaboo.common.core.annotation.RelationDict;
import io.wangk.peekaboo.common.core.annotation.RelationOneToMany;
import io.wangk.peekaboo.common.core.annotation.RelationOneToOne;
import io.wangk.peekaboo.common.report.model.constant.DatasetType;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 报表数据集实体对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
@TableName(value = "zz_report_dataset")
public class ReportDataset {

    /**
     * 主键Id。
     */
    @TableId(value = "dataset_id")
    private Long datasetId;

    /**
     * 应用编码。为空时，表示非第三方应用接入。
     */
    @TableField(value = "app_code")
    private String appCode;

    /**
     * 数据集名称。
     */
    @TableField(value = "dataset_name")
    private String datasetName;

    /**
     * 分组Id。
     */
    @TableField(value = "group_id")
    private Long groupId;

    /**
     * 数据库链接Id。
     */
    @TableField(value = "dblink_id")
    private Long dblinkId;

    /**
     * 数据集类型。
     */
    @TableField(value = "dataset_type")
    private Integer datasetType;

    /**
     * 数据表名。仅当集合为数据表时可用。
     */
    @TableField(value = "table_name")
    private String tableName;

    /**
     * 数据集信息。
     */
    @TableField(value = "dataset_info")
    private String datasetInfo;

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

    @RelationOneToMany(
            masterIdField = "datasetId",
            slaveModelClass = ReportDatasetColumn.class,
            slaveIdField = "datasetId")
    @TableField(exist = false)
    private List<ReportDatasetColumn> columnList;

    @RelationOneToMany(
            masterIdField = "datasetId",
            slaveModelClass = ReportDatasetRelation.class,
            slaveIdField = "masterDatasetId")
    @TableField(exist = false)
    private List<ReportDatasetRelation> relationList;

    @RelationOneToOne(
            masterIdField = "dblinkId",
            slaveModelClass = ReportDblink.class,
            slaveIdField = "dblinkId")
    @TableField(exist = false)
    private ReportDblink reportDblink;

    @RelationDict(
            masterIdField = "dblinkId",
            slaveModelClass = ReportDblink.class,
            slaveIdField = "dblinkId",
            slaveNameField = "dblinkName",
            equalOneToOneRelationField = "reportDblink")
    @TableField(exist = false)
    private Map<String, Object> dblinkIdDictMap;

    @RelationConstDict(
            masterIdField = "datasetType",
            constantDictClass = DatasetType.class)
    @TableField(exist = false)
    private Map<String, Object> datasetTypeDictMap;

    @TableField(exist = false)
    private Map<String, ReportDatasetColumn> columnMap;
}
