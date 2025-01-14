package io.wangk.peekaboo.common.report.dto;

import io.wangk.peekaboo.common.core.validator.UpdateGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 可视化素材Dto对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Schema(description = "可视化素材Dto对象")
@Data
public class ReportVisualizationAssetDto {

    /**
     * 主键Id。
     */
    @Schema(description = "主键Id")
    @NotNull(message = "数据验证失败，主键Id不能为空！", groups = {UpdateGroup.class})
    private Long assetId;

    /**
     * 可视化工程Id。
     */
    @Schema(description = "可视化工程Id")
    private Long visualId;

    /**
     * 素材名称。
     */
    @Schema(description = "素材名称")
    @NotBlank(message = "数据验证失败，名称不能为空！")
    private String assetName;

    /**
     * 缩略图BASE64。
     */
    @Schema(description = "缩略图BASE64")
    @NotBlank(message = "数据验证失败，名称不能为空！")
    private String thumbnailImg;

    /**
     * 图的BASE64。
     */
    @Schema(description = "封面图的BASE64")
    @NotBlank(message = "数据验证失败，名称不能为空！")
    private String assetImg;
}
