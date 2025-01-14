package io.wangk.peekaboo.common.mobile.dto;

import io.wangk.peekaboo.common.core.validator.ConstDictRef;
import io.wangk.peekaboo.common.core.validator.UpdateGroup;
import io.wangk.peekaboo.common.mobile.model.constant.MobileEntryType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.*;

/**
 * 移动端入口Dto对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Schema(description = "移动端入口Dto对象")
@Data
public class MobileEntryDto {

    /**
     * 主键Id。
     */
    @Schema(description = "主键Id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "数据验证失败，主键Id不能为空！", groups = {UpdateGroup.class})
    private Long entryId;

    /**
     * 父Id。
     */
    @Schema(description = "父Id")
    private Long parentId;

    /**
     * 显示名称。
     */
    @Schema(description = "显示名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "数据验证失败，显示名称不能为空！")
    private String entryName;

    /**
     * 移动端入口类型。
     */
    @Schema(description = "移动端入口类型")
    @ConstDictRef(constDictClass = MobileEntryType.class, message = "数据验证失败，移动端入口类型值无效！")
    @NotNull(message = "数据验证失败，移动端入口类型不能为空！")
    private Integer entryType;

    /**
     * 是否对所有角色可见。
     */
    @Schema(description = "是否对所有角色可见", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "数据验证失败，是否对所有角色可见标记不能为空！")
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
    @Schema(description = "菜单显示顺序 (值越小，排序越靠前)", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "数据验证失败，显示顺序不能为空！")
    private Integer showOrder;
}
