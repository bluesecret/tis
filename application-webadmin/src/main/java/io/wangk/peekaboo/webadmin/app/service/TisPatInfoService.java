package io.wangk.peekaboo.webadmin.app.service;

import com.alibaba.fastjson.JSONObject;
import io.wangk.peekaboo.webadmin.app.model.*;
import io.wangk.peekaboo.common.core.base.service.IBaseService;

import java.util.*;

/**
 * 患者数据操作服务接口。
 *
 * @author wangk
 * @date 2025-01-14
 */
public interface TisPatInfoService extends IBaseService<TisPatInfo, Long> {

    /**
     * 保存新增对象。
     *
     * @param tisPatInfo 新增对象。
     * @return 返回新增对象。
     */
    TisPatInfo saveNew(TisPatInfo tisPatInfo);

    /**
     * 利用数据库的insertList语法，批量插入对象列表。
     *
     * @param tisPatInfoList 新增对象列表。
     */
    void saveNewBatch(List<TisPatInfo> tisPatInfoList);

    /**
     * 保存新增主表对象及关联对象。
     *
     * @param tisPatInfo 新增主表对象。
     * @param relationData 全部关联从表数据。
     * @return 返回新增主表对象。
     */
    TisPatInfo saveNewWithRelation(TisPatInfo tisPatInfo, JSONObject relationData);

    /**
     * 更新数据对象。
     *
     * @param tisPatInfo         更新的对象。
     * @param originalTisPatInfo 原有数据对象。
     * @return 成功返回true，否则false。
     */
    boolean update(TisPatInfo tisPatInfo, TisPatInfo originalTisPatInfo);

    /**
     * 更新主表对象及关联对象。
     *
     * @param tisPatInfo 主表对象新数据。
     * @param originalTisPatInfo 主表对象源数据。
     * @param relationData 全部关联从表数据。
     * @return 修改成功返回true，否则false。
     */
    boolean updateWithRelation(TisPatInfo tisPatInfo, TisPatInfo originalTisPatInfo, JSONObject relationData);

    /**
     * 删除指定数据。
     *
     * @param id 主键Id。
     * @return 成功返回true，否则false。
     */
    boolean remove(Long id);

    /**
     * 获取单表查询结果。由于没有关联数据查询，因此在仅仅获取单表数据的场景下，效率更高。
     * 如果需要同时获取关联数据，请移步(getTisPatInfoListWithRelation)方法。
     *
     * @param filter  过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    List<TisPatInfo> getTisPatInfoList(TisPatInfo filter, String orderBy);

    /**
     * 获取主表的查询结果，以及主表关联的字典数据和一对一从表数据，以及一对一从表的字典数据。
     * 该查询会涉及到一对一从表的关联过滤，或一对多从表的嵌套关联过滤，因此性能不如单表过滤。
     * 如果仅仅需要获取主表数据，请移步(getTisPatInfoList)，以便获取更好的查询性能。
     *
     * @param filter 主表过滤对象。
     * @param tisPatResultFilter 一对多从表过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    List<TisPatInfo> getTisPatInfoListWithRelation(TisPatInfo filter, TisPatResult tisPatResultFilter, String orderBy);

    List<TisPatInfo> getTisPatInfoListByDeviceIdsWithRelation(TisPatInfo filter, TisPatResult tisPatResultFilter, Set<String> deiviceList, String orderBy);
}
