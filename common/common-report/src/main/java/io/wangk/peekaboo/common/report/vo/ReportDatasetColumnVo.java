package io.wangk.peekaboo.common.report.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 报表数据集字段视图对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Schema(description = "报表数据集字段视图对象")
@Data
public class ReportDatasetColumnVo {

    /**
     * 主键Id。
     */
    @Schema(description = "主键Id")
    private Long columnId;

    /**
     * 字段名。
     */
    @Schema(description = "字段名")
    private String columnName;

    /**
     * 数据集Id。
     */
    @Schema(description = "数据集Id")
    private Long datasetId;

    /**
     * 是否为主键。
     */
    @Schema(description = "是否为主键")
    private Boolean primaryKey;

    /**
     * 表字段类型。
     */
    @Schema(description = "表字段类型")
    private String columnType;

    /**
     * 数值型字段的精度(目前仅Oracle使用)。
     */
    @Schema(description = "数值型字段的精度")
    private Integer numericPrecision;

    /**
     * 数值型字段的刻度(小数点后位数，目前仅Oracle使用)。
     */
    @Schema(description = "数值型字段的刻度")
    private Integer numericScale;

    /**
     * 列位置。
     */
    @Schema(description = "列位置")
    private Integer columnShowOrder;

    /**
     * 字段注释。
     */
    @Schema(description = "字段注释")
    private String columnComment;

    /**
     * Java属性名。
     */
    @Schema(description = "Java属性名")
    private String fieldName;

    /**
     * Java属性类型。
     */
    @Schema(description = "Java属性类型")
    private String fieldType;

    /**
     * 字典Id。
     */
    @Schema(description = "字典Id")
    private Long dictId;

    /**
     * 是否为维度字段。
     */
    @Schema(description = "是否为维度字段")
    private Boolean dimension;

    /**
     * 是否为图片字段。
     */
    @Schema(description = "是否为图片字段")
    private Boolean image;

    /**
     * 是否为逻辑删除字段。
     */
    @Schema(description = "是否为逻辑删除字段")
    private Boolean logicDelete;

    /**
     * 是否部门过滤字段。
     */
    @Schema(description = "是否部门过滤字段")
    private Boolean deptFilter;

    /**
     * 是否用户过滤字段。
     */
    @Schema(description = "是否用户过滤字段")
    private Boolean userFilter;

    /**
     * 是否租户过滤字段，仅多租户系统可用。
     */
    @Schema(description = "是否租户过滤字段")
    private Boolean tenantFilter;

    /**
     * 字段类别。
     */
    @Schema(description = "字段类别")
    private Integer fieldKind;

    /**
     * 函数体实现。
     */
    @Schema(description = "函数体实现")
    private String functionBody;

    /**
     * 上级父字段Id。仅仅API数据源使用。
     */
    @Schema(description = "上级父字段Id。仅仅API数据源使用")
    private Long parentId;
}
