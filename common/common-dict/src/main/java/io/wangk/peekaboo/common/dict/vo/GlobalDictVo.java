package io.wangk.peekaboo.common.dict.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 全局系统字典Vo。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Schema(description = "全局系统字典Vo")
@Data
public class GlobalDictVo {

    /**
     * 主键Id。
     */
    @Schema(description = "主键Id")
    private Long dictId;

    /**
     * 字典编码。
     */
    @Schema(description = "字典编码")
    private String dictCode;

    /**
     * 字典中文名称。
     */
    @Schema(description = "字典中文名称")
    private String dictName;

    /**
     * 创建用户Id。
     */
    @Schema(description = "创建用户Id")
    private Long createUserId;

    /**
     * 创建时间。
     */
    @Schema(description = "创建时间")
    private Date createTime;

    /**
     * 创建用户名。
     */
    @Schema(description = "创建用户名")
    private Long updateUserId;

    /**
     * 更新时间。
     */
    @Schema(description = "更新时间")
    private Date updateTime;
}
