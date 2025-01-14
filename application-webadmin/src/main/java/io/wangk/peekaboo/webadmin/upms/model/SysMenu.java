package io.wangk.peekaboo.webadmin.upms.model;

import com.baomidou.mybatisplus.annotation.*;
import io.wangk.peekaboo.common.core.base.model.BaseModel;
import io.wangk.peekaboo.webadmin.upms.bo.SysMenuExtraData;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 菜单实体对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_menu")
public class SysMenu extends BaseModel {

    /**
     * 菜单Id。
     */
    @TableId(value = "menu_id")
    private Long menuId;

    /**
     * 父菜单Id，目录菜单的父菜单为null。
     */
    @TableField(value = "parent_id")
    private Long parentId;

    /**
     * 菜单显示名称。
     */
    @TableField(value = "menu_name")
    private String menuName;

    /**
     * 菜单类型(0: 目录 1: 菜单 2: 按钮 3: UI片段)。
     */
    @TableField(value = "menu_type")
    private Integer menuType;

    /**
     * 前端表单路由名称，仅用于menu_type为1的菜单类型。
     */
    @TableField(value = "form_router_name")
    private String formRouterName;

    /**
     * 在线表单主键Id，仅用于在线表单绑定的菜单。
     */
    @TableField(value = "online_form_id")
    private Long onlineFormId;

    /**
     * 在线表单菜单的权限控制类型，具体值可参考SysOnlineMenuPermType常量对象。
     */
    @TableField(value = "online_menu_perm_type")
    private Integer onlineMenuPermType;

    /**
     * 统计页面主键Id，仅用于统计页面绑定的菜单。
     */
    @TableField(value = "report_page_id")
    private Long reportPageId;

    /**
     * 仅用于在线表单的流程Id。
     */
    @TableField(value = "online_flow_entry_id")
    private Long onlineFlowEntryId;

    /**
     * 菜单显示顺序 (值越小，排序越靠前)。
     */
    @TableField(value = "show_order")
    private Integer showOrder;

    /**
     * 菜单图标。
     */
    private String icon;

    /**
     * 附加信息。
     */
    @TableField(value = "extra_data")
    private String extraData;

    /**
     * extraData字段解析后的对象数据。
     */
    @TableField(exist = false)
    private SysMenuExtraData extraObject;
}
