package io.wangk.peekaboo.common.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.wangk.peekaboo.common.core.validator.ConstDictRef;
import io.wangk.peekaboo.common.core.validator.UpdateGroup;
import io.wangk.peekaboo.common.dbutil.constant.DblinkType;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 报表数据库链接参数参数对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Schema(description = "报表数据库链接参数参数对象")
@Data
public class ReportDblinkDto {

    /**
     * 主键Id。
     */
    @Schema(description = "主键Id")
    @NotNull(message = "数据验证失败，主键Id不能为空！", groups = {UpdateGroup.class})
    private Long dblinkId;

    /**
     * 数据库链接名称。
     */
    @Schema(description = "数据库链接名称")
    @NotBlank(message = "数据验证失败，数据库链接名称不能为空！")
    private String dblinkName;

    /**
     * 数据源描述。
     */
    @Schema(description = "数据源描述")
    private String dblinkDescription;

    /**
     * 数据库链接类型。
     */
    @Schema(description = "数据库链接类型")
    @NotNull(message = "数据验证失败，数据库链接类型不能为空！")
    @ConstDictRef(constDictClass = DblinkType.class, message = "数据验证失败，数据源类型为无效值！")
    private Integer dblinkType;

    /**
     * 配置信息。
     */
    @Schema(description = "配置信息")
    @NotBlank(message = "数据验证失败，配置信息不能为空！")
    private String configuration;
}
