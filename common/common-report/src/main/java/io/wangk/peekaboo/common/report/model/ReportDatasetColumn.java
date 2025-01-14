package io.wangk.peekaboo.common.report.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * 报表数据集字段实体对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
@TableName(value = "zz_report_dataset_column")
public class ReportDatasetColumn {

    /**
     * 主键Id。
     */
    @TableId(value = "column_id")
    private Long columnId;

    /**
     * 字段名。
     */
    @TableField(value = "column_name")
    private String columnName;

    /**
     * 数据集Id。
     */
    @TableField(value = "dataset_id")
    private Long datasetId;

    /**
     * 是否为主键。
     */
    @TableField(value = "primary_key")
    private Boolean primaryKey;

    /**
     * 表字段类型。
     */
    @TableField(value = "column_type")
    private String columnType;

    /**
     * Java属性类型。
     */
    @TableField(value = "field_type")
    private String fieldType;

    /**
     * 数值型字段的精度(目前仅Oracle使用)。
     */
    @TableField(value = "numeric_precision")
    private Integer numericPrecision;

    /**
     * 数值型字段的刻度(小数点后位数，目前仅Oracle使用)。
     */
    @TableField(value = "numeric_scale")
    private Integer numericScale;

    /**
     * 字段在数据表中的显示位置。
     */
    @TableField(value = "column_show_order")
    private Integer columnShowOrder;

    /**
     * 字段注释。
     */
    @TableField(value = "column_comment")
    private String columnComment;

    /**
     * Java属性名。
     */
    @TableField(value = "field_name")
    private String fieldName;

    /**
     * 字典Id。
     */
    @TableField(value = "dict_id")
    private Long dictId;

    /**
     * 是否为维度字段。
     */
    private Boolean dimension;

    /**
     * 是否为图片字段。
     */
    private Boolean image;

    /**
     * 是否为逻辑删除字段。
     */
    @TableField(value = "logic_delete")
    private Boolean logicDelete;

    /**
     * 是否部门过滤字段。
     */
    @TableField(value = "dept_filter")
    private Boolean deptFilter;

    /**
     * 是否用户过滤字段。
     */
    @TableField(value = "user_filter")
    private Boolean userFilter;

    /**
     * 是否为租户过滤字段。仅多租户系统可用。
     */
    @TableField(value = "tenant_filter")
    private Boolean tenantFilter;

    /**
     * 字段类别。
     */
    @TableField(value = "field_kind")
    private Integer fieldKind;

    /**
     * 函数体实现。
     */
    @TableField(value = "function_body")
    private String functionBody;
}
