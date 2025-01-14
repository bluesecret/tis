package io.wangk.peekaboo.common.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.wangk.peekaboo.common.core.validator.UpdateGroup;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 报表打印分组参数对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Schema(description = "报表打印分组参数对象")
@Data
public class ReportPrintGroupDto {

    /**
     * 主键Id。
     */
    @Schema(description = "主键Id")
    @NotNull(message = "数据验证失败，主键Id不能为空！", groups = {UpdateGroup.class})
    private Long groupId;

    /**
     * 名称。
     */
    @Schema(description = "名称")
    @NotBlank(message = "数据验证失败，名称不能为空！")
    private String groupName;

    /**
     * 父级id。
     */
    @Schema(description = "父级id")
    private Long parentId;
}
