package io.wangk.peekaboo.common.report.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 报表打印视图对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Schema(description = "报表打印视图对象")
@Data
public class ReportPrintVo {

    /**
     * 主键Id。
     */
    @Schema(description = "主键Id")
    private Long printId;

    /**
     * 应用编码。为空时，表示非第三方应用接入。
     */
    @Schema(description = "应用编码。为空时，表示非第三方应用接入")
    private String appCode;

    /**
     * 名称。
     */
    @Schema(description = "名称")
    private String printName;

    /**
     * 变量名。
     */
    @Schema(description = "变量名")
    private String printVariable;

    /**
     * 打印类型。
     */
    @Schema(description = "打印类型")
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

    /**
     * 电子表格解析后的打印模板配置数据JSON。
     */
    @Schema(description = "电子表格解析后的打印模板配置数据JSON")
    private String templateDataJson;

    /**
     * 创建时间。
     */
    @Schema(description = "创建时间")
    private Date createTime;

    /**
     * 创建者。
     */
    @Schema(description = "创建者")
    private Long createUserId;

    /**
     * 更新时间。
     */
    @Schema(description = "更新时间")
    private Date updateTime;

    /**
     * 更新者。
     */
    @Schema(description = "更新者")
    private Long updateUserId;
}
