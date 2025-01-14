package io.wangk.peekaboo.common.report.service;

import io.wangk.peekaboo.common.core.base.service.IBaseService;
import io.wangk.peekaboo.common.report.model.ReportDatasetRelation;

import java.util.List;

/**
 * 数据集关联数据操作服务接口。
 *
 * @author wangk
 * @date 2025-01-14
 */
public interface ReportDatasetRelationService extends IBaseService<ReportDatasetRelation, Long> {

    /**
     * 保存新增对象。
     *
     * @param reportDatasetRelation 新增对象。
     * @return 返回新增对象。
     */
    ReportDatasetRelation saveNew(ReportDatasetRelation reportDatasetRelation);

    /**
     * 更新数据对象。
     *
     * @param reportDatasetRelation         更新的对象。
     * @param originalReportDatasetRelation 原有数据对象。
     * @return 成功返回true，否则false。
     */
    boolean update(ReportDatasetRelation reportDatasetRelation, ReportDatasetRelation originalReportDatasetRelation);

    /**
     * 删除指定数据。
     *
     * @param relationId 主键Id。
     * @return 成功返回true，否则false。
     */
    boolean remove(Long relationId);

    /**
     * 获取单表查询结果。由于没有关联数据查询，因此在仅仅获取单表数据的场景下，效率更高。
     * 如果需要同时获取关联数据，请移步(getReportDatasetRelationListWithRelation)方法。
     *
     * @param filter  过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    List<ReportDatasetRelation> getReportDatasetRelationList(ReportDatasetRelation filter, String orderBy);

    /**
     * 获取主表的查询结果，以及主表关联的字典数据和一对一从表数据，以及一对一从表的字典数据。
     * 该查询会涉及到一对一从表的关联过滤，或一对多从表的嵌套关联过滤，因此性能不如单表过滤。
     * 如果仅仅需要获取主表数据，请移步(getReportDatasetRelationList)，以便获取更好的查询性能。
     *
     * @param filter 主表过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    List<ReportDatasetRelation> getReportDatasetRelationListWithRelation(ReportDatasetRelation filter, String orderBy);
}
