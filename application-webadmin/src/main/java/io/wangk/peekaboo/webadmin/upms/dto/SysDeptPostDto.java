package io.wangk.peekaboo.webadmin.upms.dto;

import io.wangk.peekaboo.common.core.validator.UpdateGroup;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.*;

/**
 * 部门岗位Dto对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Schema(description = "部门岗位Dto")
@Data
public class SysDeptPostDto {

    /**
     * 部门岗位Id。
     */
    @Schema(description = "部门岗位Id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "数据验证失败，部门岗位Id不能为空！", groups = {UpdateGroup.class})
    private Long deptPostId;

    /**
     * 部门Id。
     */
    @Schema(description = "部门Id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "数据验证失败，部门Id不能为空！", groups = {UpdateGroup.class})
    private Long deptId;

    /**
     * 岗位Id。
     */
    @Schema(description = "岗位Id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "数据验证失败，岗位Id不能为空！", groups = {UpdateGroup.class})
    private Long postId;

    /**
     * 部门岗位显示名称。
     */
    @Schema(description = "部门岗位显示名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "数据验证失败，部门岗位显示名称不能为空！")
    private String postShowName;
}
