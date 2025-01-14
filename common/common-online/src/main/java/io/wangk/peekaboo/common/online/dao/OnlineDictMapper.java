package io.wangk.peekaboo.common.online.dao;

import io.wangk.peekaboo.common.core.base.dao.BaseDaoMapper;
import io.wangk.peekaboo.common.online.model.OnlineDict;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 在线表单字典数据操作访问接口。
 *
 * @author wangk
 * @date 2025-01-14
 */
public interface OnlineDictMapper extends BaseDaoMapper<OnlineDict> {

    /**
     * 获取过滤后的对象列表。
     *
     * @param onlineDictFilter 主表过滤对象。
     * @param orderBy          排序字符串，order by从句的参数。
     * @return 对象列表。
     */
    List<OnlineDict> getOnlineDictList(
            @Param("onlineDictFilter") OnlineDict onlineDictFilter, @Param("orderBy") String orderBy);
}
