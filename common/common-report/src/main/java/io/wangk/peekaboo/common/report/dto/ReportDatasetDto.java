package io.wangk.peekaboo.common.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.wangk.peekaboo.common.core.validator.ConstDictRef;
import io.wangk.peekaboo.common.core.validator.UpdateGroup;
import io.wangk.peekaboo.common.report.model.constant.DatasetType;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 报表数据集参数对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Schema(description = "报表数据集参数对象")
@Data
public class ReportDatasetDto {

    /**
     * 主键Id。
     */
    @Schema(description = "主键Id")
    @NotNull(message = "数据验证失败，主键Id不能为空！", groups = {UpdateGroup.class})
    private Long datasetId;

    /**
     * 数据集名称。
     */
    @Schema(description = "数据集名称")
    @NotBlank(message = "数据验证失败，数据集名称不能为空！")
    private String datasetName;

    /**
     * 分组Id。
     */
    @Schema(description = "分组Id")
    @NotNull(message = "数据验证失败，分组Id不能为空！")
    private Long groupId;

    /**
     * 数据库链接Id。
     */
    @Schema(description = "数据库链接Id")
    private Long dblinkId;

    /**
     * 数据集类型。
     */
    @Schema(description = "数据集类型")
    @NotNull(message = "数据验证失败，数据集类型不能为空！")
    @ConstDictRef(constDictClass = DatasetType.class, message = "数据验证失败，数据集类型为无效值！")
    private Integer datasetType;

    /**
     * 数据表名。仅当集合为数据表时可用。
     */
    @Schema(description = "数据表名")
    private String tableName;

    /**
     * 表原始信息。
     */
    @Schema(description = "表原始信息")
    private String datasetInfo;
}
