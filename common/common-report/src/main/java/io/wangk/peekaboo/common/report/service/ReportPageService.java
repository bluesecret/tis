package io.wangk.peekaboo.common.report.service;

import io.wangk.peekaboo.common.core.base.service.IBaseService;
import io.wangk.peekaboo.common.report.model.ReportPage;

import java.util.List;

/**
 * 报表页面数据操作服务接口。
 *
 * @author wangk
 * @date 2025-01-14
 */
public interface ReportPageService extends IBaseService<ReportPage, Long> {

    /**
     * 保存新增对象。
     *
     * @param reportPage 新增对象。
     * @return 返回新增对象。
     */
    ReportPage saveNew(ReportPage reportPage);

    /**
     * 更新数据对象。
     *
     * @param reportPage         更新的对象。
     * @param originalReportPage 原有数据对象。
     * @return 成功返回true，否则false。
     */
    boolean update(ReportPage reportPage, ReportPage originalReportPage);

    /**
     * 删除指定数据。
     *
     * @param reportPage 页面对象。
     * @return 成功返回true，否则false。
     */
    boolean remove(ReportPage reportPage);

    /**
     * 获取单表查询结果。由于没有关联数据查询，因此在仅仅获取单表数据的场景下，效率更高。
     * 如果需要同时获取关联数据，请移步(getReportPageListWithRelation)方法。
     *
     * @param filter  过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    List<ReportPage> getReportPageList(ReportPage filter, String orderBy);

    /**
     * 获取主表的查询结果，以及主表关联的字典数据和一对一从表数据，以及一对一从表的字典数据。
     * 该查询会涉及到一对一从表的关联过滤，或一对多从表的嵌套关联过滤，因此性能不如单表过滤。
     * 如果仅仅需要获取主表数据，请移步(getReportPageList)，以便获取更好的查询性能。
     *
     * @param filter 主表过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    List<ReportPage> getReportPageListWithRelation(ReportPage filter, String orderBy);

    /**
     * 根据页面编码查询页面对象。
     * 会优先从缓存中读取，如果不存在会再从数据库中读取并将最新数据同步到缓存。
     *
     * @param pageCode 页面编码。
     * @return 页面对象。
     */
    ReportPage getReportPageFromCache(String pageCode);

    /**
     * 判断当前指定的页面编码是否存在。
     *
     * @param pageCode 页面编码。
     * @return true存在，否则false。
     */
    boolean existByPageCode(String pageCode);
}
