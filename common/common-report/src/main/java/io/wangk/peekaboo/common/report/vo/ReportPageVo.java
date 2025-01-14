package io.wangk.peekaboo.common.report.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 报表页面视图对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Schema(description = "报表页面视图对象")
@Data
public class ReportPageVo {

    /**
     * 主键Id。
     */
    @Schema(description = "主键Id")
    private Long pageId;

    /**
     * 应用编码。为空时，表示非第三方应用接入。
     */
    @Schema(description = "应用编码。为空时，表示非第三方应用接入")
    private String appCode;

    /**
     * 具有唯一性的页面编码。
     */
    @Schema(description = "具有唯一性的页面编码")
    private String pageCode;

    /**
     * 名称。
     */
    @Schema(description = "名称")
    private String pageName;

    /**
     * 页面分组Id。
     */
    @Schema(description = "页面分组Id")
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
