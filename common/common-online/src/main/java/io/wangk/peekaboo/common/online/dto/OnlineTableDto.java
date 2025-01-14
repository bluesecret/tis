package io.wangk.peekaboo.common.online.dto;

import io.wangk.peekaboo.common.core.validator.UpdateGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 在线表单的数据表Dto对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Schema(description = "在线表单的数据表Dto对象")
@Data
public class OnlineTableDto {

    /**
     * 主键Id。
     */
    @Schema(description = "主键Id")
    @NotNull(message = "数据验证失败，主键Id不能为空！", groups = {UpdateGroup.class})
    private Long tableId;

    /**
     * 表名称。
     */
    @Schema(description = "表名称")
    @NotBlank(message = "数据验证失败，表名称不能为空！")
    private String tableName;

    /**
     * 实体名称。
     */
    @Schema(description = "实体名称")
    @NotBlank(message = "数据验证失败，实体名称不能为空！")
    private String modelName;

    /**
     * 数据库链接Id。
     */
    @Schema(description = "数据库链接Id")
    @NotNull(message = "数据验证失败，数据库链接Id不能为空！")
    private Long dblinkId;
}
