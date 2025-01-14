package io.wangk.peekaboo.common.report.dao;

import io.wangk.peekaboo.common.core.base.dao.BaseDaoMapper;
import io.wangk.peekaboo.common.report.model.ReportDblink;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 数据库链接数据操作访问接口。
 *
 * @author wangk
 * @date 2025-01-14
 */
public interface ReportDblinkMapper extends BaseDaoMapper<ReportDblink> {

    /**
     * 获取过滤后的对象列表。
     *
     * @param reportDblinkFilter 主表过滤对象。
     * @param orderBy 排序字符串，order by从句的参数。
     * @return 对象列表。
     */
    List<ReportDblink> getReportDblinkList(
            @Param("reportDblinkFilter") ReportDblink reportDblinkFilter, @Param("orderBy") String orderBy);
}
