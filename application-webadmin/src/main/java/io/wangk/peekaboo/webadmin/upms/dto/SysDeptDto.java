package io.wangk.peekaboo.webadmin.upms.dto;

import io.wangk.peekaboo.common.core.validator.UpdateGroup;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.*;

/**
 * 部门管理Dto对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Schema(description = "部门管理Dto对象")
@Data
public class SysDeptDto {

    /**
     * 部门Id。
     */
    @Schema(description = "部门Id。", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "数据验证失败，部门Id不能为空！", groups = {UpdateGroup.class})
    private Long deptId;

    /**
     * 部门名称。
     * NOTE: 可支持等于操作符的列表数据过滤。
     */
    @Schema(description = "部门名称。可支持等于操作符的列表数据过滤。", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "数据验证失败，部门名称不能为空！")
    private String deptName;

    /**
     * 显示顺序。
     */
    @Schema(description = "显示顺序。", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "数据验证失败，显示顺序不能为空！")
    private Integer showOrder;

    /**
     * 父部门Id。
     * NOTE: 可支持等于操作符的列表数据过滤。
     */
    @Schema(description = "父部门Id。可支持等于操作符的列表数据过滤。")
    private Long parentId;
}
