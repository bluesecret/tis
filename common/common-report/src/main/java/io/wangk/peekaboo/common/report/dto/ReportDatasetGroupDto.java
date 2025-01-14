package io.wangk.peekaboo.common.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.wangk.peekaboo.common.core.validator.UpdateGroup;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 报表数据集分组参数对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Schema(description = "报表数据集分组参数对象")
@Data
public class ReportDatasetGroupDto {

    /**
     * 主键Id。
     */
    @Schema(description = "主键Id")
    @NotNull(message = "数据验证失败，主键Id不能为空！", groups = {UpdateGroup.class})
    private Long groupId;

    /**
     * 分组名称。
     */
    @Schema(description = "分组名称")
    @NotBlank(message = "数据验证失败，分组名称不能为空！")
    private String groupName;

    /**
     * 父级Id。
     */
    @Schema(description = "父级Id")
    private Long parentId;
}
