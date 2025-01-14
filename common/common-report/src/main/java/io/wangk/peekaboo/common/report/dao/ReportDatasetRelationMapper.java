package io.wangk.peekaboo.common.report.dao;

import io.wangk.peekaboo.common.core.base.dao.BaseDaoMapper;
import io.wangk.peekaboo.common.report.model.ReportDatasetRelation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 数据集关联数据操作访问接口。
 *
 * @author wangk
 * @date 2025-01-14
 */
public interface ReportDatasetRelationMapper extends BaseDaoMapper<ReportDatasetRelation> {

    /**
     * 获取过滤后的对象列表。
     *
     * @param reportDatasetRelationFilter 主表过滤对象。
     * @param orderBy 排序字符串，order by从句的参数。
     * @return 对象列表。
     */
    List<ReportDatasetRelation> getReportDatasetRelationList(
            @Param("reportDatasetRelationFilter") ReportDatasetRelation reportDatasetRelationFilter, @Param("orderBy") String orderBy);
}
