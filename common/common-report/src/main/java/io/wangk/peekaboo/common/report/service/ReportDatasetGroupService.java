package io.wangk.peekaboo.common.report.service;

import io.wangk.peekaboo.common.core.base.service.IBaseService;
import io.wangk.peekaboo.common.report.model.ReportDatasetGroup;

import java.util.List;

/**
 * 数据集分组数据操作服务接口。
 *
 * @author wangk
 * @date 2025-01-14
 */
public interface ReportDatasetGroupService extends IBaseService<ReportDatasetGroup, Long> {

    /**
     * 保存新增对象。
     *
     * @param reportDatasetGroup 新增对象。
     * @return 返回新增对象。
     */
    ReportDatasetGroup saveNew(ReportDatasetGroup reportDatasetGroup);

    /**
     * 更新数据对象。
     *
     * @param reportDatasetGroup         更新的对象。
     * @param originalReportDatasetGroup 原有数据对象。
     * @return 成功返回true，否则false。
     */
    boolean update(ReportDatasetGroup reportDatasetGroup, ReportDatasetGroup originalReportDatasetGroup);

    /**
     * 删除指定数据。
     *
     * @param groupId 主键Id。
     * @return 成功返回true，否则false。
     */
    boolean remove(Long groupId);

    /**
     * 获取单表查询结果。由于没有关联数据查询，因此在仅仅获取单表数据的场景下，效率更高。
     * 如果需要同时获取关联数据，请移步(getReportDatasetGroupListWithRelation)方法。
     *
     * @param filter  过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    List<ReportDatasetGroup> getReportDatasetGroupList(ReportDatasetGroup filter, String orderBy);

    /**
     * 获取主表的查询结果，以及主表关联的字典数据和一对一从表数据，以及一对一从表的字典数据。
     * 该查询会涉及到一对一从表的关联过滤，或一对多从表的嵌套关联过滤，因此性能不如单表过滤。
     * 如果仅仅需要获取主表数据，请移步(getReportDatasetGroupList)，以便获取更好的查询性能。
     *
     * @param filter 主表过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    List<ReportDatasetGroup> getReportDatasetGroupListWithRelation(ReportDatasetGroup filter, String orderBy);

    /**
     * 判断指定对象是否包含下级对象。
     *
     * @param groupId 主键Id。
     * @return 存在返回true，否则false。
     */
    boolean hasChildren(Long groupId);
}
