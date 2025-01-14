package io.wangk.peekaboo.common.online.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 在线表单的数据表VO对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Schema(description = "在线表单的数据表VO对象")
@Data
public class OnlineTableVo {

    /**
     * 主键Id。
     */
    @Schema(description = "主键Id")
    private Long tableId;

    /**
     * 应用编码。为空时，表示非第三方应用接入。
     */
    @Schema(description = "应用。为空时，表示非第三方应用接入")
    private String appCode;

    /**
     * 表名称。
     */
    @Schema(description = "表名称")
    private String tableName;

    /**
     * 实体名称。
     */
    @Schema(description = "实体名称")
    private String modelName;

    /**
     * 数据库链接Id。
     */
    @Schema(description = "数据库链接Id")
    private Long dblinkId;

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
