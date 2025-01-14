package io.wangk.peekaboo.common.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.wangk.peekaboo.common.core.validator.UpdateGroup;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 报表页面参数对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Schema(description = "报表页面参数对象")
@Data
public class ReportPageDto {

    /**
     * 主键Id。
     */
    @Schema(description = "主键Id")
    @NotNull(message = "数据验证失败，主键Id不能为空！", groups = {UpdateGroup.class})
    private Long pageId;

    /**
     * 具有唯一性的页面编码。
     */
    @Schema(description = "具有唯一性的页面编码")
    @NotBlank(message = "数据验证失败，页面编码不能为空！")
    private String pageCode;

    /**
     * 名称。
     */
    @Schema(description = "名称")
    @NotBlank(message = "数据验证失败，名称不能为空！")
    private String pageName;

    /**
     * 页面分组Id。
     */
    @Schema(description = "页面分组Id")
    @NotNull(message = "数据验证失败，页面分组Id不能为空！")
    private Long groupId;

    /**
     * 页面配置的JSON。
     */
    @Schema(description = "页面配置的JSON")
    private String pageJson;

    /**
     * 表单组件JSON。
     */
    @Schema(description = "表单组件JSON")
    private String widgetJson;

    /**
     * 表单参数JSON。
     */
    @Schema(description = "表单参数JSON")
    private String paramJson;
}
