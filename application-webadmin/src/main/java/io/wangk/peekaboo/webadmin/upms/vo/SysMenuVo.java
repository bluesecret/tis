package io.wangk.peekaboo.webadmin.upms.vo;

import io.wangk.peekaboo.common.core.base.vo.BaseVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 菜单VO。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Schema(description = "菜单VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class SysMenuVo extends BaseVo {

    /**
     * 菜单Id。
     */
    @Schema(description = "菜单Id")
    private Long menuId;

    /**
     * 父菜单Id，目录菜单的父菜单为null
     */
    @Schema(description = "父菜单Id")
    private Long parentId;

    /**
     * 菜单显示名称。
     */
    @Schema(description = "菜单显示名称")
    private String menuName;

    /**
     * 菜单类型 (0: 目录 1: 菜单 2: 按钮 3: UI片段)。
     */
    @Schema(description = "菜单类型")
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
     * 在线表单菜单的权限控制类型，具体值可参考SysOnlineMenuPermType常量对象。
     */
    @Schema(description = "在线表单菜单的权限控制类型")
    private Integer onlineMenuPermType;

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
    @Schema(description = "菜单显示顺序")
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
