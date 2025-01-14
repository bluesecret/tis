package io.wangk.peekaboo.webadmin.upms.dao;

import io.wangk.peekaboo.common.core.base.dao.BaseDaoMapper;
import io.wangk.peekaboo.webadmin.upms.model.SysDataPerm;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 数据权限数据访问操作接口。
 * NOTE: 该对象一定不能被 @EnableDataPerm 注解标注，否则会导致无限递归。
 *
 * @author wangk
 * @date 2025-01-14
 */
public interface SysDataPermMapper extends BaseDaoMapper<SysDataPerm> {

    /**
     * 获取数据权限列表。
     *
     * @param sysDataPermFilter 过滤对象。
     * @param orderBy           排序字符串。
     * @return 过滤后的数据权限列表。
     */
    List<SysDataPerm> getSysDataPermList(
            @Param("sysDataPermFilter") SysDataPerm sysDataPermFilter, @Param("orderBy") String orderBy);

    /**
     * 获取指定用户的数据权限列表。
     *
     * @param userId 用户Id。
     * @return 数据权限列表。
     */
    List<SysDataPerm> getSysDataPermListByUserId(@Param("userId") Long userId);

    /**
     * 查询与指定菜单关联的数据权限列表。
     *
     * @param menuId 菜单Id。
     * @return 与菜单Id关联的数据权限列表。
     */
    List<SysDataPerm> getSysDataPermListByMenuId(@Param("menuId") Long menuId);
}
