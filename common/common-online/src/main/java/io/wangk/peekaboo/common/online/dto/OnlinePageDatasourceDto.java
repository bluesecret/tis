package io.wangk.peekaboo.common.online.dto;

import io.wangk.peekaboo.common.core.validator.UpdateGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * 在线表单页面和数据源多对多关联Dto对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Schema(description = "在线表单页面和数据源多对多关联Dto对象")
@Data
public class OnlinePageDatasourceDto {

    /**
     * 主键Id。
     */
    @Schema(description = "主键Id")
    @NotNull(message = "数据验证失败，主键Id不能为空！", groups = {UpdateGroup.class})
    private Long id;

    /**
     * 页面主键Id。
     */
    @Schema(description = "页面主键Id")
    @NotNull(message = "数据验证失败，页面主键Id不能为空！")
    private Long pageId;

    /**
     * 数据源主键Id。
     */
    @Schema(description = "数据源主键Id")
    @NotNull(message = "数据验证失败，数据源主键Id不能为空！")
    private Long datasourceId;
}
