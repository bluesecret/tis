package io.wangk.peekaboo.common.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.wangk.peekaboo.common.core.validator.ConstDictRef;
import io.wangk.peekaboo.common.core.validator.UpdateGroup;
import io.wangk.peekaboo.common.report.model.constant.ReportRelationType;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 报表数据集关联参数对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Schema(description = "报表数据集关联参数对象")
@Data
public class ReportDatasetRelationDto {

    /**
     * 主键Id。
     */
    @Schema(description = "主键Id")
    @NotNull(message = "数据验证失败，主键Id不能为空！", groups = {UpdateGroup.class})
    private Long relationId;

    /**
     * 变量名。
     */
    @Schema(description = "变量名")
    @NotBlank(message = "数据验证失败，变量名不能为空！")
    private String variableName;

    /**
     * 主表数据集Id。
     */
    @Schema(description = "主表数据集Id")
    @NotNull(message = "数据验证失败，主表数据集Id不能为空！")
    private Long masterDatasetId;

    /**
     * 主表关联字段Id。
     */
    @Schema(description = "主表关联字段Id")
    @NotNull(message = "数据验证失败，主表关联字段Id不能为空！")
    private Long masterColumnId;

    /**
     * 从表数据集Id。
     */
    @Schema(description = "从表数据集Id")
    @NotNull(message = "数据验证失败，从表数据集Id不能为空！")
    private Long slaveDatasetId;

    /**
     * 从表关联字段Id。
     */
    @Schema(description = "从表关联字段Id")
    @NotNull(message = "数据验证失败，从表关联字段Id不能为空！")
    private Long slaveColumnId;

    /**
     * 关联类型 (0:一对一 1:一对多)。
     */
    @Schema(description = "关联类型")
    @NotNull(message = "数据验证失败，数据集关联类型不能为空！")
    @ConstDictRef(constDictClass = ReportRelationType.class, message = "数据验证失败，数据集关联类型为无效值！")
    private Integer relationType;
}
