package io.wangk.peekaboo.webadmin.app.dto;

import io.wangk.peekaboo.common.core.validator.UpdateGroup;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.*;

/**
 * 检查结果Dto对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Schema(description = "检查结果Dto对象")
@Data
public class TisPatResultDto {

    /**
     * 主键Id。
     */
    @Schema(description = "主键Id。", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "数据验证失败，主键Id不能为空！", groups = {UpdateGroup.class})
    private Long id;

    /**
     * 患者ID。
     */
    @Schema(description = "患者ID。", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "数据验证失败，患者ID不能为空！", groups = {UpdateGroup.class})
    private Long patId;

    /**
     * 检测项目。
     * NOTE: 可支持等于操作符的列表数据过滤。
     */
    @Schema(description = "检测项目。可支持等于操作符的列表数据过滤。")
    private String projectName;

    /**
     * 检测结果。
     * NOTE: 可支持等于操作符的列表数据过滤。
     */
    @Schema(description = "检测结果。可支持等于操作符的列表数据过滤。")
    private String result;

    /**
     * 备用字段1。
     */
    @Schema(description = "备用字段1。")
    private String remark1;

    /**
     * 备用字段2。
     */
    @Schema(description = "备用字段2。")
    private String remark2;

    /**
     * 备用字段3。
     */
    @Schema(description = "备用字段3。")
    private String remark3;
}
