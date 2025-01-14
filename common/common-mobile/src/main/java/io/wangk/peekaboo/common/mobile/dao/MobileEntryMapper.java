package io.wangk.peekaboo.common.mobile.dao;

import io.wangk.peekaboo.common.core.base.dao.BaseDaoMapper;
import io.wangk.peekaboo.common.mobile.model.MobileEntry;
import org.apache.ibatis.annotations.Param;

import java.util.*;

/**
 * 移动端首页入口管理数据操作访问接口。
 *
 * @author wangk
 * @date 2025-01-14
 */
public interface MobileEntryMapper extends BaseDaoMapper<MobileEntry> {

    /**
     * 获取过滤后的对象列表。
     *
     * @param mobileEntryFilter 主表过滤对象。
     * @param orderBy 排序字符串，order by从句的参数。
     * @return 对象列表。
     */
    List<MobileEntry> getMobileEntryList(
            @Param("mobileEntryFilter") MobileEntry mobileEntryFilter, @Param("orderBy") String orderBy);

    /**
     * 获取属于指定角色Id集合的移动端入口列表。
     *
     * @param roleIds  角色Id集合。
     * @param tenantId 租户Id。
     * @return 移动端入口对象列表。
     */
    List<MobileEntry> getMobileEntryListByRoleIds(@Param("roleIds") Set<Long> roleIds, @Param("tenantId") Long tenantId);

    /**
     * 获取公用的移动端入口数据。
     *
     * @param tenantId 租户Id，如果为NULL，则过滤tenantId为IS NULL的数据。
     * @return 移动端入口对象列表。
     */
    List<MobileEntry> getCommonMobileEntryList(@Param("tenantId") Long tenantId);
}
