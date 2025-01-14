package io.wangk.peekaboo.webadmin.upms.service;

import io.wangk.peekaboo.webadmin.upms.model.SysUserAuth;
import io.wangk.peekaboo.common.core.base.service.IBaseService;

/**
 * 用户第三方授权数据数据操作服务接口。
 *
 * @author wangk
 * @date 2025-01-14
 */
public interface SysUserAuthService extends IBaseService<SysUserAuth, Long> {

    /**
     * 保存新增对象。
     *
     * @param sysUserAuth 新增对象。
     * @return 返回新增对象。
     */
    SysUserAuth saveNew(SysUserAuth sysUserAuth);

    /**
     * 更新数据对象。
     *
     * @param sysUserAuth 更新的对象。
     * @return 成功返回true，否则false。
     */
    boolean update(SysUserAuth sysUserAuth);

    /**
     * 删除指定数据。
     *
     * @param id 主键Id。
     * @return 成功返回true，否则false。
     */
    boolean remove(Long id);

    /**
     * 根据userId移除数据。
     *
     * @param userId 用户Id。
     */
    void removeByUserId(Long userId);

    /**
     * 根据授权渠道和用户Id获取对象。
     *
     * @param source 第三方授权源。
     * @param userId 当前系统用户表中的userId。
     * @return 用户第三方鉴权对象。
     */
    SysUserAuth getByUserId(String source, Long userId);
}
