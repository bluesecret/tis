package io.wangk.peekaboo.common.flow.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.wangk.peekaboo.common.core.validator.UpdateGroup;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 流程分类的Dto对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Schema(description = "流程分类的Dto对象")
@Data
public class FlowCategoryDto {

    /**
     * 主键Id。
     */
    @Schema(description = "主键Id")
    @NotNull(message = "数据验证失败，主键Id不能为空！", groups = {UpdateGroup.class})
    private Long categoryId;

    /**
     * 显示名称。
     */
    @Schema(description = "显示名称")
    @NotBlank(message = "数据验证失败，显示名称不能为空！")
    private String name;

    /**
     * 分类编码。
     */
    @Schema(description = "分类编码")
    @NotBlank(message = "数据验证失败，分类编码不能为空！")
    private String code;

    /**
     * 实现顺序。
     */
    @Schema(description = "实现顺序")
    @NotNull(message = "数据验证失败，实现顺序不能为空！")
    private Integer showOrder;
}
