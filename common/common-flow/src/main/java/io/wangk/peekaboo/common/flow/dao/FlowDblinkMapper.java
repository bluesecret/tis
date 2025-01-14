package io.wangk.peekaboo.common.flow.dao;

import io.wangk.peekaboo.common.core.base.dao.BaseDaoMapper;
import io.wangk.peekaboo.common.flow.model.FlowDblink;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 数据库链接数据操作访问接口。
 *
 * @author wangk
 * @date 2025-01-14
 */
public interface FlowDblinkMapper extends BaseDaoMapper<FlowDblink> {

    /**
     * 获取过滤后的对象列表。
     *
     * @param flowDblinkFilter 主表过滤对象。
     * @param orderBy          排序字符串，order by从句的参数。
     * @return 对象列表。
     */
    List<FlowDblink> getFlowDblinkList(
            @Param("flowDblinkFilter") FlowDblink flowDblinkFilter, @Param("orderBy") String orderBy);
}
