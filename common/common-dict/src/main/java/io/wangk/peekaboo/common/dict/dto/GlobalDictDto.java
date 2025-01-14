package io.wangk.peekaboo.common.dict.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.wangk.peekaboo.common.core.validator.UpdateGroup;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 全局系统字典Dto。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Schema(description = "全局系统字典Dto")
@Data
public class GlobalDictDto {

    /**
     * 主键Id。
     */
    @Schema(description = "主键Id")
    @NotNull(message = "数据验证失败，主键Id不能为空！", groups = {UpdateGroup.class})
    private Long dictId;

    /**
     * 字典编码。
     */
    @Schema(description = "字典编码")
    @NotBlank(message = "数据验证失败，字典编码不能为空！")
    private String dictCode;

    /**
     * 字典中文名称。
     */
    @Schema(description = "字典中文名称")
    @NotBlank(message = "数据验证失败，字典中文名称不能为空！")
    private String dictName;
}
