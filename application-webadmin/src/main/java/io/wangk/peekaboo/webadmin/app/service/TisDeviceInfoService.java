package io.wangk.peekaboo.webadmin.app.service;

import io.wangk.peekaboo.common.core.base.service.IBaseService;
import io.wangk.peekaboo.common.dict.model.GlobalDict;
import io.wangk.peekaboo.webadmin.app.model.TisDeviceInfo;
import io.wangk.peekaboo.webadmin.app.model.TisUserDevice;
import io.wangk.peekaboo.webadmin.upms.model.SysUser;
import io.wangk.peekaboo.webadmin.upms.model.SysUserRole;

import java.util.List;

/**
 * 患者数据操作服务接口。
 *
 * @author wangk
 * @date 2025-01-14
 */
public interface TisDeviceInfoService extends IBaseService<TisDeviceInfo, Long> {

    /**
     * 保存新增对象。
     *
     * @param tisDeviceInfo 新增对象。
     * @return 返回新增对象。
     */
    TisDeviceInfo saveNew(TisDeviceInfo tisDeviceInfo);

    /**
     * 判断设备编码是否存在。
     *
     * @param serNo 设备编码。
     * @return true表示存在，否则false。
     */
    boolean existSerNo(String serNo);

    /**
     * 更新设备对象。
     *
     * @param tisDeviceInfo         更新的设备对象。
     * @param originalTisDeviceInfo 原有的设备对象。
     * @return 更新成功返回true，否则false。
     */
    boolean update(TisDeviceInfo tisDeviceInfo, TisDeviceInfo originalTisDeviceInfo);

    /**
     * 删除指定数据。
     *
     * @param id 主键Id。
     * @return 成功返回true，否则false。
     */
    boolean remove(Long id);

    List<TisUserDevice> getTisUserDeviceListByUserId(Long userId);

    /**
     * 批量新增用户设备关联。
     *
     * @param userDeviceList 用户设备关系数据列表。
     */
    void addUserDeviceList(SysUser sysUser, List<TisUserDevice> userDeviceList);
}
