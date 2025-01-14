package io.wangk.peekaboo.common.report.model;

import com.baomidou.mybatisplus.annotation.*;
import io.wangk.peekaboo.common.core.annotation.RelationConstDict;
import io.wangk.peekaboo.common.core.annotation.RelationDict;
import io.wangk.peekaboo.common.report.model.constant.ReportRelationType;
import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 * 报表数据集关联实体对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
@TableName(value = "zz_report_dataset_relation")
public class ReportDatasetRelation {

    /**
     * 主键Id。
     */
    @TableId(value = "relation_id")
    private Long relationId;

    /**
     * 应用编码。为空时，表示非第三方应用接入。
     */
    @TableField(value = "app_code")
    private String appCode;

    /**
     * 变量名。
     */
    @TableField(value = "variable_name")
    private String variableName;

    /**
     * 主表数据集Id。
     */
    @TableField(value = "master_dataset_id")
    private Long masterDatasetId;

    /**
     * 主表关联字段Id。
     */
    @TableField(value = "master_column_id")
    private Long masterColumnId;

    /**
     * 从表数据集Id。
     */
    @TableField(value = "slave_dataset_id")
    private Long slaveDatasetId;

    /**
     * 从表关联字段Id。
     */
    @TableField(value = "slave_column_id")
    private Long slaveColumnId;

    /**
     * 关联类型 (0:一对一 1:一对多)。
     */
    @TableField(value = "relation_type")
    private Integer relationType;

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

    @RelationDict(
            masterIdField = "masterDatasetId",
            slaveModelClass = ReportDataset.class,
            slaveIdField = "datasetId",
            slaveNameField = "datasetName")
    @TableField(exist = false)
    private Map<String, Object> masterDatasetIdDictMap;

    @RelationDict(
            masterIdField = "masterColumnId",
            slaveModelClass = ReportDatasetColumn.class,
            slaveIdField = "columnId",
            slaveNameField = "columnName")
    @TableField(exist = false)
    private Map<String, Object> masterColumnIdDictMap;

    @RelationDict(
            masterIdField = "slaveDatasetId",
            slaveModelClass = ReportDataset.class,
            slaveIdField = "datasetId",
            slaveNameField = "datasetName")
    @TableField(exist = false)
    private Map<String, Object> slaveDatasetIdDictMap;

    @RelationDict(
            masterIdField = "slaveColumnId",
            slaveModelClass = ReportDatasetColumn.class,
            slaveIdField = "columnId",
            slaveNameField = "columnName")
    @TableField(exist = false)
    private Map<String, Object> slaveColumnIdDictMap;

    @RelationConstDict(
            masterIdField = "relationType",
            constantDictClass = ReportRelationType.class)
    @TableField(exist = false)
    private Map<String, Object> relationTypeDictMap;
}
