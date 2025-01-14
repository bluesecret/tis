package io.wangk.peekaboo.webadmin.upms.dto;

import io.wangk.peekaboo.common.core.validator.UpdateGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.*;

/**
 * 角色Dto。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Schema(description = "角色Dto")
@Data
public class SysRoleDto {

    /**
     * 角色Id。
     */
    @Schema(description = "角色Id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "角色Id不能为空！", groups = {UpdateGroup.class})
    private Long roleId;

    /**
     * 角色名称。
     */
    @Schema(description = "角色名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "角色名称不能为空！")
    private String roleName;
}
