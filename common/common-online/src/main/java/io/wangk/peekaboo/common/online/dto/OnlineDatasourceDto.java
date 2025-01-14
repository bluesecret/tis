package io.wangk.peekaboo.common.online.dto;

import io.wangk.peekaboo.common.core.validator.AddGroup;
import io.wangk.peekaboo.common.core.validator.UpdateGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 在线表单的数据源Dto对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Schema(description = "在线表单的数据源Dto对象")
@Data
public class OnlineDatasourceDto {

    /**
     * 主键Id。
     */
    @Schema(description = "主键Id")
    @NotNull(message = "数据验证失败，主键Id不能为空！", groups = {UpdateGroup.class})
    private Long datasourceId;

    /**
     * 数据源名称。
     */
    @Schema(description = "数据源名称")
    @NotBlank(message = "数据验证失败，数据源名称不能为空！")
    private String datasourceName;

    /**
     * 数据源变量名，会成为数据访问url的一部分。
     */
    @Schema(description = "数据源变量名，会成为数据访问url的一部分")
    @NotBlank(message = "数据验证失败，数据源变量名不能为空！")
    private String variableName;

    /**
     * 主表所在的数据库链接Id。
     */
    @Schema(description = "主表所在的数据库链接Id")
    @NotNull(message = "数据验证失败，数据库链接Id不能为空！")
    private Long dblinkId;

    /**
     * 主表Id。
     */
    @Schema(description = "主表Id")
    @NotNull(message = "数据验证失败，主表Id不能为空！", groups = {UpdateGroup.class})
    private Long masterTableId;

    /**
     * 主表表名。
     */
    @Schema(description = "主表表名")
    @NotBlank(message = "数据验证失败，主表名不能为空！", groups = {AddGroup.class})
    private String masterTableName;
}
