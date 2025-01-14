package io.wangk.peekaboo.common.report.service;

import io.wangk.peekaboo.common.core.base.service.IBaseService;
import io.wangk.peekaboo.common.core.object.CallResult;
import io.wangk.peekaboo.common.report.model.ReportPrint;
import io.wangk.peekaboo.common.report.object.ReportPrintParam;

import java.util.List;

/**
 * 报表打印数据操作服务接口。
 *
 * @author wangk
 * @date 2025-01-14
 */
public interface ReportPrintService extends IBaseService<ReportPrint, Long> {

    /**
     * 保存新增对象。
     *
     * @param reportPrint 新增对象。
     * @return 返回新增对象。
     */
    ReportPrint saveNew(ReportPrint reportPrint);

    /**
     * 更新数据对象。
     *
     * @param reportPrint         更新的对象。
     * @param originalReportPrint 原有数据对象。
     * @return 成功返回true，否则false。
     */
    boolean update(ReportPrint reportPrint, ReportPrint originalReportPrint);

    /**
     * 删除指定数据。
     *
     * @param printId 主键Id。
     * @return 成功返回true，否则false。
     */
    boolean remove(Long printId);

    /**
     * 获取单表查询结果。由于没有关联数据查询，因此在仅仅获取单表数据的场景下，效率更高。
     * 如果需要同时获取关联数据，请移步(getReportPrintListWithRelation)方法。
     *
     * @param filter  过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    List<ReportPrint> getReportPrintList(ReportPrint filter, String orderBy);

    /**
     * 获取主表的查询结果，以及主表关联的字典数据和一对一从表数据，以及一对一从表的字典数据。
     * 该查询会涉及到一对一从表的关联过滤，或一对多从表的嵌套关联过滤，因此性能不如单表过滤。
     * 如果仅仅需要获取主表数据，请移步(getReportPrintList)，以便获取更好的查询性能。
     *
     * @param filter  主表过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    List<ReportPrint> getReportPrintListWithRelation(ReportPrint filter, String orderBy);

    /**
     * 通过打印参数，获取实际数据，并结合打印模板，渲染出最终的打印结果。
     *
     * @param printVariable  打印模板标识。
     * @param printParamList 打印参数列表，多个参数可支持批量打印。
     * @param renderType     打印渲染类型，参考PrintRenderType。
     * @return 调用结果。
     */
    CallResult render(String printVariable, List<ReportPrintParam> printParamList, int renderType);

    /**
     * 通过打印参数，获取实际数据，并结合打印模板，渲染出最终的打印结果。
     *
     * @param reportPrint    打印模板对象。
     * @param printParamList 打印参数列表，多个参数可支持批量打印。
     * @param renderType     打印渲染类型，参考PrintRenderType。
     * @return 调用结果。
     */
    CallResult render(ReportPrint reportPrint, List<ReportPrintParam> printParamList, int renderType);

    /**
     * 判断指定打印模板变量是否存在。
     *
     * @param printVariable 打印模板变量。
     * @return true存在，否则false。
     */
    boolean existByPrintVariable(String printVariable);
}
