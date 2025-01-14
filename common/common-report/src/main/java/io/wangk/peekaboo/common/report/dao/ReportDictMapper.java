package io.wangk.peekaboo.common.report.dao;

import io.wangk.peekaboo.common.core.base.dao.BaseDaoMapper;
import io.wangk.peekaboo.common.report.model.ReportDict;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 报表字典数据操作访问接口。
 *
 * @author wangk
 * @date 2025-01-14
 */
public interface ReportDictMapper extends BaseDaoMapper<ReportDict> {

    /**
     * 获取过滤后的对象列表。
     *
     * @param reportDictFilter 主表过滤对象。
     * @param orderBy 排序字符串，order by从句的参数。
     * @return 对象列表。
     */
    List<ReportDict> getReportDictList(
            @Param("reportDictFilter") ReportDict reportDictFilter, @Param("orderBy") String orderBy);
}
