package io.wangk.peekaboo.webadmin.app.dao;

import io.wangk.peekaboo.common.core.base.dao.BaseDaoMapper;
import io.wangk.peekaboo.webadmin.app.model.TisPatResult;
import org.apache.ibatis.annotations.Param;

import java.util.*;

/**
 * 患者检测结果数据操作访问接口。
 *
 * @author wangk
 * @date 2025-01-14
 */
public interface TisPatResultMapper extends BaseDaoMapper<TisPatResult> {

    /**
     * 批量插入对象列表。
     *
     * @param tisPatResultList 新增对象列表。
     */
    void insertList(List<TisPatResult> tisPatResultList);

    /**
     * 获取过滤后的对象列表。
     *
     * @param tisPatResultFilter 主表过滤对象。
     * @param orderBy 排序字符串，order by从句的参数。
     * @return 对象列表。
     */
    List<TisPatResult> getTisPatResultList(
            @Param("tisPatResultFilter") TisPatResult tisPatResultFilter, @Param("orderBy") String orderBy);
}
