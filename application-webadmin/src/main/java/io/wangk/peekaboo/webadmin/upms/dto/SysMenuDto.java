package io.wangk.peekaboo.webadmin.upms.dto;

import io.wangk.peekaboo.common.core.validator.ConstDictRef;
import io.wangk.peekaboo.common.core.validator.UpdateGroup;
import io.wangk.peekaboo.webadmin.upms.model.constant.SysMenuType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 菜单Dto。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Schema(description = "菜单Dto")
@Data
public class SysMenuDto {

    /**
     * 菜单Id。
     */
    @Schema(description = "菜单Id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "菜单Id不能为空！", groups = {UpdateGroup.class})
    private Long menuId;

    /**
     * 父菜单Id，目录菜单的父菜单为null
     */
    @Schema(description = "父菜单Id")
    private Long parentId;

    /**
     * 菜单显示名称。
     */
    @Schema(description = "菜单显示名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "菜单显示名称不能为空！")
    private String menuName;

    /**
     * 菜单类型 (0: 目录 1: 菜单 2: 按钮 3: UI片段)。
     */
    @Schema(description = "菜单类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "菜单类型不能为空！")
    @ConstDictRef(constDictClass = SysMenuType.class, message = "数据验证失败，菜单类型为无效值！")
    private Integer menuType;

    /**
     * 前端表单路由名称，仅用于menu_type为1的菜单类型。
     */
    @Schema(description = "前端表单路由名称")
    private String formRouterName;

    /**
     * 在线表单主键Id，仅用于在线表单绑定的菜单。
     */
    @Schema(description = "在线表单主键Id")
    private Long onlineFormId;

    /**
     * 统计页面主键Id，仅用于统计页面绑定的菜单。
     */
    @Schema(description = "统计页面主键Id")
    private Long reportPageId;

    /**
     * 仅用于在线表单的流程Id。
     */
    @Schema(description = "仅用于在线表单的流程Id")
    private Long onlineFlowEntryId;

    /**
     * 菜单显示顺序 (值越小，排序越靠前)。
     */
    @Schema(description = "菜单显示顺序", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "菜单显示顺序不能为空！")
    private Integer showOrder;

    /**
     * 菜单图标。
     */
    @Schema(description = "菜单显示图标")
    private String icon;

    /**
     * 附加信息。
     */
    @Schema(description = "附加信息")
    private String extraData;
}
