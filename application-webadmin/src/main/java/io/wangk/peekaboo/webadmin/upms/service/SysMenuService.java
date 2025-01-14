package io.wangk.peekaboo.webadmin.upms.service;

import io.wangk.peekaboo.common.core.base.service.IBaseService;
import io.wangk.peekaboo.webadmin.upms.model.SysMenu;

import java.util.*;

/**
 * 菜单数据服务接口。
 *
 * @author wangk
 * @date 2025-01-14
 */
public interface SysMenuService extends IBaseService<SysMenu, Long> {

    /**
     * 保存新增的菜单对象。
     *
     * @param sysMenu       新增的菜单对象。
     * @return 新增后的菜单对象。
     */
    SysMenu saveNew(SysMenu sysMenu);

    /**
     * 更新菜单对象。
     *
     * @param sysMenu         更新的菜单对象。
     * @param originalSysMenu 原有的菜单对象。
     * @return 更新成功返回true，否则false。
     */
    boolean update(SysMenu sysMenu, SysMenu originalSysMenu);

    /**
     * 删除指定的菜单。
     *
     * @param menu 菜单对象。
     * @return 删除成功返回true，否则false。
     */
    boolean remove(SysMenu menu);

    /**
     * 获取指定用户Id的菜单列表，已去重。
     *
     * @param userId 用户主键Id。
     * @return 用户关联的菜单列表。
     */
    Collection<SysMenu> getMenuListByUserId(Long userId);
    
    /**
     * 根据角色Id集合获取菜单对象列表。
     *
     * @param roleIds 逗号分隔的角色Id集合。
     * @return 菜单对象列表。
     */
    Collection<SysMenu> getMenuListByRoleIds(String roleIds);

    /**
     * 判断当前菜单是否存在子菜单。
     *
     * @param menuId 菜单主键Id。
     * @return 存在返回true，否则false。
     */
    boolean hasChildren(Long menuId);

    /**
     * 获取指定类型的所有在线表单的菜单。
     *
     * @param menuType 菜单类型，NULL则返回全部类型。
     * @return 在线表单关联的菜单列表。
     */
    List<SysMenu> getAllOnlineMenuList(Integer menuType);
}
