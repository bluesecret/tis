package io.wangk.peekaboo.common.report.dao;

import io.wangk.peekaboo.common.core.base.dao.BaseDaoMapper;
import io.wangk.peekaboo.common.report.model.ReportDataset;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * 报表数据集数据操作访问接口。
 *
 * @author wangk
 * @date 2025-01-14
 */
public interface ReportDatasetMapper extends BaseDaoMapper<ReportDataset> {

    /**
     * 获取过滤后的对象列表。
     *
     * @param reportDatasetFilter 主表过滤对象。
     * @param orderBy             排序字符串，order by从句的参数。
     * @return 对象列表。
     */
    List<ReportDataset> getReportDatasetList(
            @Param("reportDatasetFilter") ReportDataset reportDatasetFilter, @Param("orderBy") String orderBy);

    /**
     * 获取指定租户过滤后的对象列表。
     *
     * @param tenantId            租户Id。
     * @param reportDatasetFilter 主表过滤对象。
     * @param orderBy             排序字符串，order by从句的参数。
     * @return 对象列表。
     */
    List<ReportDataset> getReportDatasetListByTenantId(
            @Param("tenantId") Long tenantId,
            @Param("reportDatasetFilter") ReportDataset reportDatasetFilter,
            @Param("orderBy") String orderBy);

    /**
     * 根据主键Id集合，获取指定租户的报表数据集列表。
     *
     * @param tenantId   租户Id。
     * @param datasetIds 数据集Id集合。
     * @return 对象列表。
     */
    List<ReportDataset> getInListByTenantId(@Param("tenantId") Long tenantId, @Param("datasetIds") Set<Long> datasetIds);

    /**
     * 根据主键Id集合，获取指定租户的报表数据集。
     *
     * @param tenantId  租户Id。
     * @param datasetId 数据集Id。
     * @return 数据集对象。
     */
    ReportDataset getReportDatasetByTenantId(@Param("tenantId") Long tenantId, @Param("datasetId") Long datasetId);
}
