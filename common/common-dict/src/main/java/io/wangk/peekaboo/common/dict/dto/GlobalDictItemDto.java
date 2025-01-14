package io.wangk.peekaboo.common.dict.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.wangk.peekaboo.common.core.validator.UpdateGroup;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 全局系统字典项目Dto。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Schema(description = "全局系统字典项目Dto")
@Data
public class GlobalDictItemDto {

    /**
     * 主键Id。
     */
    @Schema(description = "主键Id")
    @NotNull(message = "数据验证失败，主键Id不能为空！", groups = {UpdateGroup.class})
    private Long id;

    /**
     * 字典编码。
     */
    @Schema(description = "字典编码")
    @NotBlank(message = "数据验证失败，字典编码不能为空！")
    private String dictCode;

    /**
     * 字典数据项Id。
     */
    @Schema(description = "字典数据项Id")
    @NotNull(message = "数据验证失败，字典数据项Id不能为空！")
    private String itemId;

    /**
     * 字典数据项名称。
     */
    @Schema(description = "字典数据项名称")
    @NotBlank(message = "数据验证失败，字典数据项名称不能为空！")
    private String itemName;

    /**
     * 显示顺序(数值越小越靠前)。
     */
    @Schema(description = "显示顺序")
    @NotNull(message = "数据验证失败，显示顺序不能为空！")
    private Integer showOrder;
}
