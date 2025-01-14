package io.wangk.peekaboo.common.dict.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 全局系统字典项目Vo。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Schema(description = "全局系统字典项目Vo")
@Data
public class GlobalDictItemVo {

    /**
     * 主键Id。
     */
    @Schema(description = "主键Id")
    private Long id;

    /**
     * 字典编码。
     */
    @Schema(description = "字典编码")
    private String dictCode;

    /**
     * 字典数据项Id。
     */
    @Schema(description = "字典数据项Id")
    private String itemId;

    /**
     * 字典数据项名称。
     */
    @Schema(description = "字典数据项名称")
    private String itemName;

    /**
     * 显示顺序(数值越小越靠前)。
     */
    @Schema(description = "显示顺序")
    private Integer showOrder;

    /**
     * 字典状态。具体值引用DictItemStatus常量类。
     */
    @Schema(description = "字典状态")
    private Integer status;

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
