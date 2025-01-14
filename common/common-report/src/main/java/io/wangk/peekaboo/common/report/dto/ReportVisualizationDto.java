package io.wangk.peekaboo.common.report.dto;

import io.wangk.peekaboo.common.core.validator.ConstDictRef;
import io.wangk.peekaboo.common.core.validator.UpdateGroup;

import io.wangk.peekaboo.common.report.model.constant.ReportVisualPublishStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.*;

/**
 * 大屏可视化Dto对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Schema(description = "大屏可视化Dto对象")
@Data
public class ReportVisualizationDto {

    /**
     * 主键Id。
     */
    @Schema(description = "主键Id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "数据验证失败，主键Id不能为空！", groups = {UpdateGroup.class})
    private Long visualId;

    /**
     * 名称。
     */
    @Schema(description = "名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "数据验证失败，名称不能为空！")
    private String visualName;

    /**
     * 可视化配置。
     */
    @Schema(description = "可视化配置", requiredMode = Schema.RequiredMode.REQUIRED)
    private String configJson;

    /**
     * 封面图的BASE64。
     */
    @Schema(description = "封面图的BASE64")
    private String coverImg;

    /**
     * 代码页。
     */
    @Schema(description = "代码页")
    private String codePage;

    /**
     * 发布状态。
     */
    @Schema(description = "发布状态 (0: 未发布，1: 发布)", requiredMode = Schema.RequiredMode.REQUIRED)
    @ConstDictRef(constDictClass = ReportVisualPublishStatus.class, message = "数据验证失败，发布状态为无效值！")
    @NotNull(message = "数据验证失败，发布状态不能为空！")
    private Integer publishStatus;
}
