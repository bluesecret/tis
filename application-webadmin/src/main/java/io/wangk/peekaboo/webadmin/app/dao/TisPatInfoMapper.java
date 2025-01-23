package io.wangk.peekaboo.webadmin.app.dao;

import io.wangk.peekaboo.common.core.base.dao.BaseDaoMapper;
import io.wangk.peekaboo.webadmin.app.model.TisPatInfo;
import io.wangk.peekaboo.webadmin.app.model.TisPatResult;
import org.apache.ibatis.annotations.Param;

import java.util.*;

/**
 * 患者信息数据操作访问接口。
 *
 * @author wangk
 * @date 2025-01-14
 */
public interface TisPatInfoMapper extends BaseDaoMapper<TisPatInfo> {

    /**
     * 批量插入对象列表。
     *
     * @param tisPatInfoList 新增对象列表。
     */
    void insertList(List<TisPatInfo> tisPatInfoList);

    /**
     * 获取过滤后的对象列表。
     *
     * @param tisPatInfoFilter 主表过滤对象。
     * @param orderBy 排序字符串，order by从句的参数。
     * @return 对象列表。
     */
    List<TisPatInfo> getTisPatInfoList(
            @Param("tisPatInfoFilter") TisPatInfo tisPatInfoFilter, @Param("orderBy") String orderBy);

    /**
     * 获取过滤后的对象列表。同时支持基于一对一从表字段的过滤条件。
     *
     * @param tisPatInfoFilter 主表过滤对象。
     * @param tisPatResultFilter 一对多从表过滤对象。
     * @param orderBy 排序字符串，order by从句的参数。
     * @return 对象列表。
     */
    List<TisPatInfo> getTisPatInfoListEx(
            @Param("tisPatInfoFilter") TisPatInfo tisPatInfoFilter,
            @Param("tisPatResultFilter") TisPatResult tisPatResultFilter,
            @Param("orderBy") String orderBy);
}
