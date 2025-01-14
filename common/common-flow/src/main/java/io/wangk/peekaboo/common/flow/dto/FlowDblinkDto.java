package io.wangk.peekaboo.common.flow.dto;

import io.wangk.peekaboo.common.core.validator.UpdateGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 工作流自动化流程数据表所在数据库链接Dto对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Schema(description = "工作流自动化流程数据表所在数据库链接Dto对象")
@Data
public class FlowDblinkDto {

    /**
     * 主键Id。
     */
    @Schema(description = "主键Id")
    @NotNull(message = "数据验证失败，主键Id不能为空！", groups = {UpdateGroup.class})
    private Long dblinkId;

    /**
     * 链接中文名称。
     */
    @Schema(description = "链接中文名称")
    @NotBlank(message = "数据验证失败，链接中文名称不能为空！")
    private String dblinkName;

    /**
     * 链接描述。
     */
    @Schema(description = "链接中文名称")
    private String dblinkDescription;

    /**
     * 配置信息。
     */
    @Schema(description = "配置信息")
    @NotBlank(message = "数据验证失败，配置信息不能为空！")
    private String configuration;

    /**
     * 数据库链接类型。
     */
    @Schema(description = "数据库链接类型")
    @NotNull(message = "数据验证失败，数据库链接类型不能为空！")
    private Integer dblinkType;
}
