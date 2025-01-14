package io.wangk.peekaboo.common.report.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 报表打印分组视图对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Schema(description = "报表打印分组视图对象")
@Data
public class ReportPrintGroupVo {

    /**
     * 主键Id。
     */
    @Schema(description = "主键Id")
    private Long groupId;

    /**
     * 名称。
     */
    @Schema(description = "名称")
    private String groupName;

    /**
     * 应用编码。为空时，表示非第三方应用接入。
     */
    @Schema(description = "应用编码。为空时，表示非第三方应用接入")
    private String appCode;

    /**
     * 父级Id。
     */
    @Schema(description = "父级Id")
    private Long parentId;

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
