package io.wangk.peekaboo.webadmin.app.dao;

import io.wangk.peekaboo.common.core.base.dao.BaseDaoMapper;
import io.wangk.peekaboo.webadmin.app.model.TisDeviceInfo;
import io.wangk.peekaboo.webadmin.upms.model.SysRole;
import io.wangk.peekaboo.webadmin.upms.model.SysUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * 角色数据访问操作接口。
 *
 * @author wangk
 * @date 2025-01-14
 */
public interface TisDeviceMapper extends BaseDaoMapper<TisDeviceInfo> {
}
