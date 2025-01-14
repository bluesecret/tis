package io.wangk.peekaboo.common.report.dao;

import io.wangk.peekaboo.common.core.base.dao.BaseDaoMapper;
import io.wangk.peekaboo.common.report.model.ReportDatasetColumn;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 数据集字段数据操作访问接口。
 *
 * @author wangk
 * @date 2025-01-14
 */
public interface ReportDatasetColumnMapper extends BaseDaoMapper<ReportDatasetColumn> {

    /**
     * 批量插入对象列表。
     *
     * @param reportDatasetColumnList 新增对象列表。
     */
    void insertList(List<ReportDatasetColumn> reportDatasetColumnList);

    /**
     * 获取过滤后的对象列表。
     *
     * @param reportDatasetColumnFilter 主表过滤对象。
     * @return 对象列表。
     */
    List<ReportDatasetColumn> getReportDatasetColumnList(
            @Param("reportDatasetColumnFilter") ReportDatasetColumn reportDatasetColumnFilter);
}
