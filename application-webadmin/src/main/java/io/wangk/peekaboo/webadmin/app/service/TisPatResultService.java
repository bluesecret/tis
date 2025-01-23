package io.wangk.peekaboo.webadmin.app.service;

import io.wangk.peekaboo.webadmin.app.model.*;
import io.wangk.peekaboo.common.core.object.CallResult;
import io.wangk.peekaboo.common.core.base.service.IBaseService;

import java.util.*;

/**
 * 检查结果数据操作服务接口。
 *
 * @author wangk
 * @date 2025-01-14
 */
public interface TisPatResultService extends IBaseService<TisPatResult, Long> {

    /**
     * 保存新增对象。
     *
     * @param tisPatResult 新增对象。
     * @return 返回新增对象。
     */
    TisPatResult saveNew(TisPatResult tisPatResult);

    /**
     * 利用数据库的insertList语法，批量插入对象列表。
     *
     * @param tisPatResultList 新增对象列表。
     */
    void saveNewBatch(List<TisPatResult> tisPatResultList);

    /**
     * 利用数据库的insertList语法，批量插入对象列表。通常适用于更大的插入数据量，如批量导入。
     *
     * @param tisPatResultList 新增对象列表。
     * @param batchSize  每批插入的数量。如果该值小于等于0，则使用缺省值10000。
     */
    void saveNewBatch(List<TisPatResult> tisPatResultList, int batchSize);

    /**
     * 更新数据对象。
     *
     * @param tisPatResult         更新的对象。
     * @param originalTisPatResult 原有数据对象。
     * @return 成功返回true，否则false。
     */
    boolean update(TisPatResult tisPatResult, TisPatResult originalTisPatResult);

    /**
     * 删除指定数据。
     *
     * @param id 主键Id。
     * @return 成功返回true，否则false。
     */
    boolean remove(Long id);

    /**
     * 当前服务的支持表为从表，根据主表的关联Id，删除一对多的从表数据。
     *
     * @param patId 从表关联字段。
     * @return 删除数量。
     */
    int removeByPatId(Long patId);

    /**
     * 批量更新一对多从表的数据。
     *
     * @param patId 从表关联字段。
     * @param dataList 本次批量更新的一对多从表数据。
     */
    void updateBatchByPatId(Long patId, List<TisPatResult> dataList);

    /**
     * 获取单表查询结果。由于没有关联数据查询，因此在仅仅获取单表数据的场景下，效率更高。
     * 如果需要同时获取关联数据，请移步(getTisPatResultListWithRelation)方法。
     *
     * @param filter  过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    List<TisPatResult> getTisPatResultList(TisPatResult filter, String orderBy);

    /**
     * 获取主表的查询结果，以及主表关联的字典数据和一对一从表数据，以及一对一从表的字典数据。
     * 该查询会涉及到一对一从表的关联过滤，或一对多从表的嵌套关联过滤，因此性能不如单表过滤。
     * 如果仅仅需要获取主表数据，请移步(getTisPatResultList)，以便获取更好的查询性能。
     *
     * @param filter 主表过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    List<TisPatResult> getTisPatResultListWithRelation(TisPatResult filter, String orderBy);

    /**
     * 对批量导入数据列表进行数据合法性验证。
     * 验证逻辑主要覆盖主表的常量字典字段、字典表字典字段、数据源字段和一对一关联数据是否存在。
     *
     * @param dataList 主表的数据列表。
     * @param ignoreFieldSet 需要忽略校验的字典字段集合。通常对于字典反向翻译过来的字段适用，
     *                       避免了二次验证，以提升效率。
     * @return 验证结果。如果失败，包含具体的错误信息和导致错误的数据对象。
     */
    CallResult verifyImportList(List<TisPatResult> dataList, Set<String> ignoreFieldSet);
}
