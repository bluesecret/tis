package io.wangk.peekaboo.webadmin.upms.bo;

import lombok.Data;

import java.util.List;

/**
 * 菜单扩展数据对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
public class SysMenuExtraData {

    /**
     * 路由名称。
     */
    private String formRouterName;

    /**
     * 在线表单。
     */
    private Long onlineFormId;

    /**
     * 报表页面。
     */
    private Long reportPageId;

    /**
     * 流程。
     */
    private Long onlineFlowEntryId;

    /**
     * 目标url。
     */
    private String targetUrl;

    /**
     * 绑定类型。
     */
    private Integer bindType;

    /**
     * 前端使用的菜单编码。仅当选择satoken权限框架时使用。
     */
    private String menuCode;

    /**
     * 菜单关联的后台使用的权限字列表。仅当选择satoken权限框架时使用。
     */
    private List<String> permCodeList;
}
