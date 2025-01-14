package io.wangk.peekaboo.common.online.dao;

import io.wangk.peekaboo.common.core.base.dao.BaseDaoMapper;
import io.wangk.peekaboo.common.online.model.OnlineColumn;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 字段数据数据操作访问接口。
 *
 * @author wangk
 * @date 2025-01-14
 */
public interface OnlineColumnMapper extends BaseDaoMapper<OnlineColumn> {

    /**
     * 获取过滤后的对象列表。
     *
     * @param onlineColumnFilter 主表过滤对象。
     * @return 对象列表。
     */
    List<OnlineColumn> getOnlineColumnList(@Param("onlineColumnFilter") OnlineColumn onlineColumnFilter);
}
