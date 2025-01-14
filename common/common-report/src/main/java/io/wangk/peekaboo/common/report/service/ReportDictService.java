package io.wangk.peekaboo.common.report.service;

import io.wangk.peekaboo.common.core.base.service.IBaseService;
import io.wangk.peekaboo.common.report.dto.ReportDictFilterDto;
import io.wangk.peekaboo.common.report.model.ReportDict;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 报表字典数据操作服务接口。
 *
 * @author wangk
 * @date 2025-01-14
 */
public interface ReportDictService extends IBaseService<ReportDict, Long> {

    /**
     * 保存新增对象。
     *
     * @param reportDict 新增对象。
     * @return 返回新增对象。
     */
    ReportDict saveNew(ReportDict reportDict);

    /**
     * 更新数据对象。
     *
     * @param reportDict         更新的对象。
     * @param originalReportDict 原有数据对象。
     * @return 成功返回true，否则false。
     */
    boolean update(ReportDict reportDict, ReportDict originalReportDict);

    /**
     * 删除指定数据。
     *
     * @param reportDict 字典对象。
     * @return 成功返回true，否则false。
     */
    boolean remove(ReportDict reportDict);

    /**
     * 获取单表查询结果。由于没有关联数据查询，因此在仅仅获取单表数据的场景下，效率更高。
     * 如果需要同时获取关联数据，请移步(getReportDictListWithRelation)方法。
     *
     * @param filter  过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    List<ReportDict> getReportDictList(ReportDict filter, String orderBy);

    /**
     * 获取主表的查询结果，以及主表关联的字典数据和一对一从表数据，以及一对一从表的字典数据。
     * 该查询会涉及到一对一从表的关联过滤，或一对多从表的嵌套关联过滤，因此性能不如单表过滤。
     * 如果仅仅需要获取主表数据，请移步(getReportDictList)，以便获取更好的查询性能。
     *
     * @param filter  主表过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    List<ReportDict> getReportDictListWithRelation(ReportDict filter, String orderBy);

    /**
     * 根据指定字典Id集合返回字段对象数据列表。
     *
     * @param dictIdSet 字典Id集合。
     * @return 查询后的字典对象列表。
     */
    List<ReportDict> getReportDictList(Set<Long> dictIdSet);

    /**
     * 从缓存中获取字典对象。如果缓存中不存在，会从数据库中读取并同步到缓存。
     *
     * @param dictId 字典Id。
     * @return 统计模块使用的字典对象。
     */
    ReportDict getReportDictFromCache(Long dictId);

    /**
     * 获取字典的数据列表。
     *
     * @param reportDict    字典对象。
     * @param filterDtoList 过滤参数列表，目前仅支持数据表字典过滤。
     * @return 字典的数据列表。
     */
    List<Map<String, Object>> getDataList(ReportDict reportDict, List<ReportDictFilterDto> filterDtoList);

    /**
     * 构建字典数据查询的SELECT LIST。
     *
     * @param dict 字典对象。
     * @return SELECT LIST数据列表。
     */
    List<String> makeDictSelectFields(ReportDict dict);
}
