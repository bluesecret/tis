package io.wangk.peekaboo.common.report.service;

import io.wangk.peekaboo.common.core.base.service.IBaseService;
import io.wangk.peekaboo.common.report.model.ReportDataset;
import io.wangk.peekaboo.common.report.model.ReportDatasetColumn;

import java.io.Serializable;
import java.util.List;

/**
 * 数据集字段数据操作服务接口。
 *
 * @author wangk
 * @date 2025-01-14
 */
public interface ReportDatasetColumnService extends IBaseService<ReportDatasetColumn, Long> {

    /**
     * 利用数据库的insertList语法，批量插入对象列表。
     *
     * @param reportDatasetColumnList 新增对象列表。
     */
    void saveNewBatch(ReportDataset reportDataset, List<ReportDatasetColumn> reportDatasetColumnList);

    /**
     * 更新数据对象。
     *
     * @param reportDatasetColumn         更新的对象。
     * @param originalReportDatasetColumn 原有数据对象。
     * @return 成功返回true，否则false。
     */
    boolean update(ReportDatasetColumn reportDatasetColumn, ReportDatasetColumn originalReportDatasetColumn);

    /**
     * 删除指定数据集Id的所有字段。
     *
     * @param datasetId 数据集Id。
     */
    void removeByDatasetId(Long datasetId);

    /**
     * 获取单表查询结果。由于没有关联数据查询，因此在仅仅获取单表数据的场景下，效率更高。
     *
     * @param filter 过滤对象。
     * @return 查询结果集。
     */
    List<ReportDatasetColumn> getReportDatasetColumnList(ReportDatasetColumn filter);

    /**
     * 获取指定数据集的字段列表。
     *
     * @param datasetId 数据集Id。
     * @return 数据集字段列表。
     */
    List<ReportDatasetColumn> getReportDatasetColumnListByDatasetId(Long datasetId);

    /**
     * 根据字段的类型，将参数值转换为和字段匹配的类型。
     *
     * @param column   字段对象。
     * @param strValue 字符型的字段值。
     * @return 根据字段对象类型转换后的字段值。
     */
    Serializable convertToColumnValue(ReportDatasetColumn column, String strValue);

    /**
     * 根据字段的类型，将字符型参数值列表转换为和字段匹配的类型值列表。
     *
     * @param column       字段对象。
     * @param strValueList 字符型的字段值列表。
     * @return 根据字段对象类型转换后的字段值列表。
     */
    List<Serializable> convertToColumnValueList(ReportDatasetColumn column, List<String> strValueList);
}
