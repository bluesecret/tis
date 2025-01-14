package io.wangk.peekaboo.webadmin.upms.dao;

import io.wangk.peekaboo.common.core.base.dao.BaseDaoMapper;
import io.wangk.peekaboo.webadmin.upms.model.SysMenu;
import org.apache.ibatis.annotations.Param;

import java.util.*;

/**
 * 菜单数据访问操作接口。
 *
 * @author wangk
 * @date 2025-01-14
 */
public interface SysMenuMapper extends BaseDaoMapper<SysMenu> {

    /**
     * 获取登录用户的菜单列表。
     *
     * @param userId 登录用户。
     * @return 菜单列表。
     */
    List<SysMenu> getMenuListByUserId(@Param("userId") Long userId);

    /**
     * 获取指定角色Id集合的菜单列表。
     *
     * @param roleIds 角色Id集合。
     * @return 菜单列表。
     */
    List<SysMenu> getMenuListByRoleIds(@Param("roleIds") Set<Long> roleIds);

    /**
     * 查询包含指定菜单编码的菜单数量，目前仅用于satoken的权限框架。
     *
     * @param menuCode 菜单编码。
     * @return 查询数量
     */
    int countMenuCode(@Param("menuCode") String menuCode);
}
