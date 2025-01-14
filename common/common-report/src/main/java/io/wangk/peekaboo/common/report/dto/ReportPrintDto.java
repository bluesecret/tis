package io.wangk.peekaboo.common.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.wangk.peekaboo.common.core.validator.ConstDictRef;
import io.wangk.peekaboo.common.core.validator.UpdateGroup;
import io.wangk.peekaboo.common.report.model.constant.ReportPrintType;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 报表打印参数对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Schema(description = "报表打印参数对象")
@Data
public class ReportPrintDto {

    /**
     * 主键Id。
     */
    @Schema(description = "主键Id")
    @NotNull(message = "数据验证失败，主键Id不能为空！", groups = {UpdateGroup.class})
    private Long printId;

    /**
     * 名称。
     */
    @Schema(description = "名称")
    @NotBlank(message = "数据验证失败，名称不能为空！")
    private String printName;


    /**
     * 变量名。
     */
    @Schema(description = "变量名")
    @NotBlank(message = "数据验证失败，变量名不能为空！")
    private String printVariable;

    /**
     * 打印类型。
     */
    @Schema(description = "打印类型")
    @ConstDictRef(constDictClass = ReportPrintType.class, message = "数据验证失败，打印类型为无效值！")
    private Integer printType;

    /**
     * word模板。
     */
    @Schema(description = "word模板")
    private String wordTemplate;

    /**
     * 页面分组Id。
     */
    @Schema(description = "页面分组Id")
    @NotNull(message = "数据验证失败，页面分组Id不能为空！")
    private Long groupId;

    /**
     * 打印配置JSON。
     */
    @Schema(description = "打印配置JSON")
    private String printJson;

    /**
     * 参数数据JSON。
     */
    @Schema(description = "参数数据JSON")
    private String paramJson;

    /**
     * 打印片段参数JSON。
     */
    @Schema(description = "打印片段参数JSON")
    private String fragmentJson;

    /**
     * luckysheet电子表单原始配置JSON。
     */
    @Schema(description = "luckysheet电子表单原始配置JSON")
    private String sheetDataJson;
}
