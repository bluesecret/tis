package io.wangk.peekaboo.webadmin.upms.dto;

import io.wangk.peekaboo.common.core.validator.UpdateGroup;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.*;

/**
 * 岗位Dto对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Schema(description = "岗位Dto")
@Data
public class SysPostDto {

    /**
     * 岗位Id。
     */
    @Schema(description = "岗位Id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "数据验证失败，岗位Id不能为空！", groups = {UpdateGroup.class})
    private Long postId;

    /**
     * 岗位名称。
     */
    @Schema(description = "岗位名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "数据验证失败，岗位名称不能为空！")
    private String postName;

    /**
     * 岗位层级，数值越小级别越高。
     */
    @Schema(description = "岗位层级", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "数据验证失败，岗位层级不能为空！")
    private Integer postLevel;

    /**
     * 是否领导岗位。
     */
    @Schema(description = "是否领导岗位", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "数据验证失败，领导岗位不能为空！", groups = {UpdateGroup.class})
    private Boolean leaderPost;
}
