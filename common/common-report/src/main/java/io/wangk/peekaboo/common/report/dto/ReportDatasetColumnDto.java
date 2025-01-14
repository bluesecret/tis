package io.wangk.peekaboo.common.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.wangk.peekaboo.common.core.validator.ConstDictRef;
import io.wangk.peekaboo.common.core.validator.UpdateGroup;
import io.wangk.peekaboo.common.report.model.constant.ReportFieldKind;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 报表数据集字段参数对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Schema(description = "报表数据集字段参数对象")
@Data
public class ReportDatasetColumnDto {

    /**
     * 主键Id。
     */
    @Schema(description = "主键Id")
    @NotNull(message = "数据验证失败，主键Id不能为空！", groups = {UpdateGroup.class})
    private Long columnId;

    /**
     * 字段名。
     */
    @Schema(description = "字段名")
    @NotBlank(message = "数据验证失败，字段名不能为空！")
    private String columnName;

    /**
     * 数据集Id。
     */
    @Schema(description = "数据集Id")
    @NotNull(message = "数据验证失败，数据集Id不能为空！")
    private Long datasetId;

    /**
     * 是否为主键。
     */
    @Schema(description = "是否为主键")
    @NotNull(message = "数据验证失败，是否为主键标记不能为空！")
    private Boolean primaryKey;

    /**
     * 表字段类型。
     */
    @Schema(description = "表字段类型")
    @NotNull(message = "数据验证失败，表字段类型不能为空！")
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
    @NotNull(message = "数据验证失败，列位置不能为空！")
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
    @NotBlank(message = "数据验证失败，Java属性名不能为空！")
    private String fieldName;

    /**
     * Java属性类型。
     */
    @Schema(description = "Java属性类型")
    @NotBlank(message = "数据验证失败，Java属性类型不能为空！")
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
    @NotNull(message = "数据验证失败，是否为图片字段不能为空！")
    private Boolean image;

    /**
     * 是否为逻辑删除字段。
     */
    @Schema(description = "是否为逻辑删除字段")
    @NotNull(message = "数据验证失败，是否为逻辑删除字段不能为空！")
    private Boolean logicDelete;

    /**
     * 是否部门过滤字段。
     */
    @Schema(description = "是否部门过滤字段")
    @NotNull(message = "数据验证失败，是否部门过滤字段不能为空！")
    private Boolean deptFilter;

    /**
     * 是否租户过滤字段，仅多租户系统可用。
     */
    @Schema(description = "是否租户过滤字段")
    private Boolean tenantFilter;

    /**
     * 是否用户过滤字段。
     */
    @Schema(description = "是否用户过滤字段")
    @NotNull(message = "数据验证失败，是否用户过滤字段不能为空！")
    private Boolean userFilter;

    /**
     * 字段类别。
     */
    @Schema(description = "字段类别")
    @ConstDictRef(constDictClass = ReportFieldKind.class, message = "数据验证失败，字段类别为无效值！")
    @NotNull(message = "数据验证失败，字段类别不能为空！")
    private Integer fieldKind;

    /**
     * 函数体实现。
     */
    @Schema(description = "函数体实现")
    private String functionBody;
}
