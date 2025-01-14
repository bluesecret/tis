package io.wangk.peekaboo.common.report.dao;

import io.wangk.peekaboo.common.core.base.dao.BaseDaoMapper;
import io.wangk.peekaboo.common.report.model.ReportPrintGroup;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 打印分组数据操作访问接口。
 *
 * @author wangk
 * @date 2025-01-14
 */
public interface ReportPrintGroupMapper extends BaseDaoMapper<ReportPrintGroup> {

    /**
     * 获取过滤后的对象列表。
     *
     * @param reportPrintGroupFilter 主表过滤对象。
     * @param orderBy 排序字符串，order by从句的参数。
     * @return 对象列表。
     */
    List<ReportPrintGroup> getReportPrintGroupList(
            @Param("reportPrintGroupFilter") ReportPrintGroup reportPrintGroupFilter, @Param("orderBy") String orderBy);
}
