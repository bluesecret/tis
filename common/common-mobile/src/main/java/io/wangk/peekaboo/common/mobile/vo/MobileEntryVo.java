package io.wangk.peekaboo.common.mobile.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 移动端入口视图对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Schema(description = "移动端入口VO视图对象")
@Data
public class MobileEntryVo {

    /**
     * 主键Id。
     */
    @Schema(description = "主键Id")
    private Long entryId;

    /**
     * 父Id。
     */
    @Schema(description = "父Id")
    private Long parentId;

    /**
     * 显示名称。
     */
    @Schema(description = "显示名称")
    private String entryName;

    /**
     * 移动端入口类型。
     */
    @Schema(description = "移动端入口类型")
    private Integer entryType;

    /**
     * 是否对所有角色可见。
     */
    @Schema(description = "是否对所有角色可见", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean commonEntry;

    /**
     * 附件信息。
     */
    @Schema(description = "附件信息")
    private String extraData;

    /**
     * 显示图片。
     */
    @Schema(description = "图片数据")
    private String imageData;

    /**
     * 显示顺序。
     */
    @Schema(description = "菜单显示顺序 (值越小，排序越靠前)")
    private Integer showOrder;

    /**
     * 是否为租户自定义移动端入口。
     */
    @Schema(description = "是否为租户自定义移动端入口")
    private Boolean tenantCustom;

    /**
     * 创建者Id。
     */
    @Schema(description = "创建者Id")
    private Long createUserId;

    /**
     * 创建时间。
     */
    @Schema(description = "创建时间")
    private Date createTime;

    /**
     * 更新者Id。
     */
    @Schema(description = "更新者Id")
    private Long updateUserId;

    /**
     * 最后更新时间。
     */
    @Schema(description = "最后更新时间")
    private Date updateTime;

    /**
     * 多对多移动端入口和角色数据集合。
     */
    @Schema(description = "多对多移动端入口角色数据集合")
    private List<Map<String, Object>> mobileEntryRoleList;
}
