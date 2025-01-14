package io.wangk.peekaboo.common.flow.service;

import io.wangk.peekaboo.common.core.base.service.IBaseService;
import io.wangk.peekaboo.common.dbutil.object.SqlTable;
import io.wangk.peekaboo.common.dbutil.object.SqlTableColumn;
import io.wangk.peekaboo.common.flow.model.FlowDblink;

import java.util.List;

/**
 * 数据库链接数据操作服务接口。
 *
 * @author wangk
 * @date 2025-01-14
 */
public interface FlowDblinkService extends IBaseService<FlowDblink, Long> {

    /**
     * 保存新增对象。
     *
     * @param flowDblink 新增对象。
     * @return 返回新增对象。
     */
    FlowDblink saveNew(FlowDblink flowDblink);

    /**
     * 更新数据对象。
     *
     * @param flowDblink         更新的对象。
     * @param originalFlowDblink 原有数据对象。
     * @return 成功返回true，否则false。
     */
    boolean update(FlowDblink flowDblink, FlowDblink originalFlowDblink);

    /**
     * 删除指定数据。
     *
     * @param dblinkId 主键Id。
     * @return 成功返回true，否则false。
     */
    boolean remove(Long dblinkId);

    /**
     * 获取单表查询结果。由于没有关联数据查询，因此在仅仅获取单表数据的场景下，效率更高。
     * 如果需要同时获取关联数据，请移步(getOnlineDblinkListWithRelation)方法。
     *
     * @param filter  过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    List<FlowDblink> getFlowDblinkList(FlowDblink filter, String orderBy);

    /**
     * 获取主表的查询结果，以及主表关联的字典数据和一对一从表数据，以及一对一从表的字典数据。
     * 该查询会涉及到一对一从表的关联过滤，或一对多从表的嵌套关联过滤，因此性能不如单表过滤。
     * 如果仅仅需要获取主表数据，请移步(getOnlineDblinkList)，以便获取更好的查询性能。
     *
     * @param filter  主表过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    List<FlowDblink> getFlowDblinkListWithRelation(FlowDblink filter, String orderBy);

    /**
     * 获取指定DBLink下面的全部数据表。
     *
     * @param dblink 数据库链接对象。
     * @return 全部数据表列表。
     */
    List<SqlTable> getDblinkTableList(FlowDblink dblink);

    /**
     * 获取指定DBLink下，指定表名的数据表对象，及其关联字段列表。
     *
     * @param dblink    数据库链接对象。
     * @param tableName 数据库中的数据表名。
     * @return 数据表对象。
     */
    SqlTable getDblinkTable(FlowDblink dblink, String tableName);

    /**
     * 获取指定DBLink下，指定表名的字段列表。
     *
     * @param dblink    数据库链接对象。
     * @param tableName 数据库中的数据表名。
     * @return 表的字段列表。
     */
    List<SqlTableColumn> getDblinkTableColumnList(FlowDblink dblink, String tableName);
}
