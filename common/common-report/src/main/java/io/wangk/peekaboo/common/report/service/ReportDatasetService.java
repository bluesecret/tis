package io.wangk.peekaboo.common.report.service;

import io.wangk.peekaboo.common.core.base.service.IBaseService;
import io.wangk.peekaboo.common.core.object.CallResult;
import io.wangk.peekaboo.common.dbutil.object.DatasetParam;
import io.wangk.peekaboo.common.report.model.ReportDataset;
import io.wangk.peekaboo.common.report.model.ReportDatasetColumn;
import io.wangk.peekaboo.common.report.model.ReportDatasetRelation;
import io.wangk.peekaboo.common.report.object.ReportDatasetInfo;
import io.wangk.peekaboo.common.report.object.ReportResultSet;
import net.sf.jsqlparser.JSQLParserException;

import java.util.List;
import java.util.Map;

/**
 * 报表数据集数据操作服务接口。
 *
 * @author wangk
 * @date 2025-01-14
 */
public interface ReportDatasetService extends IBaseService<ReportDataset, Long> {

    /**
     * 保存新增对象。
     *
     * @param reportDataset 新增对象。
     * @return 返回新增对象。
     */
    ReportDataset saveNew(ReportDataset reportDataset);

    /**
     * 更新数据对象。
     *
     * @param reportDataset         更新的对象。
     * @param originalReportDataset 原有数据对象。
     * @return 成功返回true，否则false。
     */
    boolean update(ReportDataset reportDataset, ReportDataset originalReportDataset);

    /**
     * 删除指定数据。
     *
     * @param datasetId 主键Id。
     * @return 成功返回true，否则false。
     */
    boolean remove(Long datasetId);

    /**
     * 获取单表查询结果。由于没有关联数据查询，因此在仅仅获取单表数据的场景下，效率更高。
     * 如果需要同时获取关联数据，请移步(getReportDatasetListWithRelation)方法。
     *
     * @param filter  过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    List<ReportDataset> getReportDatasetList(ReportDataset filter, String orderBy);

    /**
     * 获取主表的查询结果，以及主表关联的字典数据和一对一从表数据，以及一对一从表的字典数据。
     * 该查询会涉及到一对一从表的关联过滤，或一对多从表的嵌套关联过滤，因此性能不如单表过滤。
     * 如果仅仅需要获取主表数据，请移步(getReportDatasetList)，以便获取更好的查询性能。
     *
     * @param filter  主表过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    List<ReportDataset> getReportDatasetListWithRelation(ReportDataset filter, String orderBy);

    /**
     * 从缓存中获取数据集对象，如果缓存中并不存在，会从数据库中读取，并同步到缓存。
     * 就目前来看，该方法主要是给ReportOperationController.listDataWithGroup接口使用，以提升运行时数据计算接口的效率。
     * 另外需要强调的是，该功能仅仅获取columnList的关联数据，其他关联数据都不会返回。
     *
     * @param datasetId 数据集Id。
     * @return 数据集对象。
     */
    ReportDataset getReportDatasetFromCache(Long datasetId);

    /**
     * 获取数据集的数据列表。同时也会返回每个字段的meta数据。
     *
     * @param reportDataset 数据集对象。
     * @param datasetParam  数据集查询参数。
     * @return 查询结果集，以及结果集中的字段meta信息。
     */
    ReportResultSet<Map<String, Object>> getDataList(ReportDataset reportDataset, DatasetParam datasetParam);

    /**
     * 获取数据集的数据列表。如果包含字典字段，则返回关联的字典数据。
     *
     * @param reportDataset 数据集对象。
     * @param datasetParam  数据集查询参数。
     * @return 查询结果集，以及结果集中的字段meta信息。
     */
    ReportResultSet<Map<String, Object>> getDataListWithRelation(ReportDataset reportDataset, DatasetParam datasetParam);

    /**
     * 当前查询只能返回一条记录，如果返回多条记录就会抛出异常。同时返回指定字段名的值。
     *
     * @param reportDataset 数据集。
     * @param datasetParam  数据集参数。
     * @param columnName    指定的字段名。
     * @return 查询结果数据
     */
    Object getDataByColumnName(ReportDataset reportDataset, DatasetParam datasetParam, String columnName);

    /**
     * 同步指定数据集的字段列表。
     *
     * @param reportDataset 数据集对象。
     * @return 同步结果。
     */
    CallResult sync(ReportDataset reportDataset);

    /**
     * 为结果集绑定字典关联数据。
     *
     * @param resultList     数据结果集。
     * @param columnMetaList 字段Meta列表。
     */
    void buildDataListWithDict(List<Map<String, Object>> resultList, List<ReportDatasetColumn> columnMetaList);

    /**
     * 为结果集绑定数据集关联数据。
     *
     * @param resultList 数据结果集。
     * @param relation   数据集关联。
     */
    void buildDataListWithRelation(List<Map<String, Object>> resultList, ReportDatasetRelation relation);

    /**
     * 目前仅应用于SQL结果集。
     * 替换SQL结果集中的参数值。替换逻辑是，datasetParam包含的参数直接使用，没有包含在datasetParam中的SQL结果集
     * 参数，可以使用ReportDatasetInfo.paramList中的默认值替换，如果都没有就跳过。
     *
     * @param datasetInfo  结果集中的信息。
     * @param datasetParam 结果集的过滤参数。
     * @return 替换后的SQL语句。
     */
    String replaceParametersWithFilterParam(ReportDatasetInfo datasetInfo, DatasetParam datasetParam);

    /**
     * 目前仅应用于SQL结果集。
     * 替换SQL结果集中的参数值。替换逻辑是把所有带有参数的从句都用 1=1 恒成立条件过滤。如：
     * select * from zz_sys_user where user_status = ${status} 会被替换为
     * select * from zz_sys_user where 1 = 1。
     *
     * @param sql 被替换的SQL语句。
     * @return 替换后的SQL语句。
     * @throws JSQLParserException 解析异常。
     */
    String replaceParametersWithAlwayTrue(String sql) throws JSQLParserException;
}
