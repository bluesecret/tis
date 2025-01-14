package io.wangk.peekaboo.common.report.service.impl;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import io.wangk.peekaboo.common.dbutil.object.SqlTable;
import io.wangk.peekaboo.common.dbutil.object.SqlTableColumn;
import io.wangk.peekaboo.common.report.config.ReportProperties;
import io.wangk.peekaboo.common.report.dao.ReportDblinkMapper;
import io.wangk.peekaboo.common.report.model.ReportDblink;
import io.wangk.peekaboo.common.report.service.ReportDblinkService;
import io.wangk.peekaboo.common.report.util.ReportDataSourceUtil;
import com.github.pagehelper.Page;
import io.wangk.peekaboo.common.core.annotation.MyDataSourceResolver;
import io.wangk.peekaboo.common.core.base.dao.BaseDaoMapper;
import io.wangk.peekaboo.common.core.base.service.BaseService;
import io.wangk.peekaboo.common.core.constant.ApplicationConstant;
import io.wangk.peekaboo.common.core.object.MyRelationParam;
import io.wangk.peekaboo.common.core.object.TokenData;
import io.wangk.peekaboo.common.core.util.DefaultDataSourceResolver;
import io.wangk.peekaboo.common.sequence.wrapper.IdGeneratorWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据库链接数据操作服务类。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Slf4j
@MyDataSourceResolver(
        resolver = DefaultDataSourceResolver.class,
        intArg = ApplicationConstant.COMMON_REPORT_DATASOURCE_TYPE)
@Service("reportDblinkService")
public class ReportDblinkServiceImpl extends BaseService<ReportDblink, Long> implements ReportDblinkService {

    @Autowired
    private ReportDblinkMapper reportDblinkMapper;
    @Autowired
    private IdGeneratorWrapper idGenerator;
    @Autowired
    private ReportDataSourceUtil dataSourceUtil;
    @Autowired
    private ReportProperties reportProperties;

    /**
     * 返回当前Service的主表Mapper对象。
     *
     * @return 主表Mapper对象。
     */
    @Override
    protected BaseDaoMapper<ReportDblink> mapper() {
        return reportDblinkMapper;
    }

    /**
     * 保存新增对象。
     *
     * @param reportDblink 新增对象。
     * @return 返回新增对象。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ReportDblink saveNew(ReportDblink reportDblink) {
        reportDblinkMapper.insert(this.buildDefaultValue(reportDblink));
        return reportDblink;
    }

    /**
     * 更新数据对象。
     *
     * @param reportDblink         更新的对象。
     * @param originalReportDblink 原有数据对象。
     * @return 成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean update(ReportDblink reportDblink, ReportDblink originalReportDblink) {
        if (!StrUtil.equals(reportDblink.getConfiguration(), originalReportDblink.getConfiguration())) {
            dataSourceUtil.removeDataSource(reportDblink.getDblinkId());
        }
        reportDblink.setAppCode(TokenData.takeFromRequest().getAppCode());
        reportDblink.setCreateUserId(originalReportDblink.getCreateUserId());
        reportDblink.setUpdateUserId(TokenData.takeFromRequest().getUserId());
        reportDblink.setCreateTime(originalReportDblink.getCreateTime());
        reportDblink.setUpdateTime(new Date());
        // 这里重点提示，在执行主表数据更新之前，如果有哪些字段不支持修改操作，请用原有数据对象字段替换当前数据字段。
        UpdateWrapper<ReportDblink> uw = this.createUpdateQueryForNullValue(reportDblink, reportDblink.getDblinkId());
        return reportDblinkMapper.update(reportDblink, uw) == 1;
    }

    /**
     * 删除指定数据。
     *
     * @param dblinkId 主键Id。
     * @return 成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean remove(Long dblinkId) {
        dataSourceUtil.removeDataSource(dblinkId);
        return reportDblinkMapper.deleteById(dblinkId) == 1;
    }

    /**
     * 获取单表查询结果。由于没有关联数据查询，因此在仅仅获取单表数据的场景下，效率更高。
     * 如果需要同时获取关联数据，请移步(getReportDblinkListWithRelation)方法。
     *
     * @param filter  过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    @Override
    public List<ReportDblink> getReportDblinkList(ReportDblink filter, String orderBy) {
        if (filter == null) {
            filter = new ReportDblink();
        }
        TokenData tokenData = TokenData.takeFromRequest();
        if (BooleanUtil.isTrue(reportProperties.getIsVisualization()) 
                && BooleanUtil.isFalse(tokenData.getIsAdmin())) {
            filter.setCreateUserId(tokenData.getUserId());
        }
        filter.setAppCode(tokenData.getAppCode());
        return reportDblinkMapper.getReportDblinkList(filter, orderBy);
    }

    /**
     * 获取主表的查询结果，以及主表关联的字典数据和一对一从表数据，以及一对一从表的字典数据。
     * 该查询会涉及到一对一从表的关联过滤，或一对多从表的嵌套关联过滤，因此性能不如单表过滤。
     * 如果仅仅需要获取主表数据，请移步(getReportDblinkList)，以便获取更好的查询性能。
     *
     * @param filter 主表过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    @Override
    public List<ReportDblink> getReportDblinkListWithRelation(ReportDblink filter, String orderBy) {
        List<ReportDblink> resultList = this.getReportDblinkList(filter, orderBy);
        // 在缺省生成的代码中，如果查询结果resultList不是Page对象，说明没有分页，那么就很可能是数据导出接口调用了当前方法。
        // 为了避免一次性的大量数据关联，规避因此而造成的系统运行性能冲击，这里手动进行了分批次读取，开发者可按需修改该值。
        int batchSize = resultList instanceof Page ? 0 : 1000;
        this.buildRelationForDataList(resultList, MyRelationParam.normal(), batchSize);
        return resultList;
    }

    @Override
    public List<String> getAllTables(ReportDblink reportDblink) {
        List<SqlTable> tableList = dataSourceUtil.getTableList(reportDblink.getDblinkId(), null);
        return tableList.stream().map(SqlTable::getTableName).collect(Collectors.toList());
    }

    @Override
    public List<SqlTableColumn> getTableColumnList(ReportDblink reportDblink, String tableName) {
        return dataSourceUtil.getTableColumnList(reportDblink.getDblinkId(), tableName);
    }

    private ReportDblink buildDefaultValue(ReportDblink reportDblink) {
        reportDblink.setDblinkId(idGenerator.nextLongId());
        TokenData tokenData = TokenData.takeFromRequest();
        reportDblink.setCreateUserId(tokenData.getUserId());
        reportDblink.setUpdateUserId(tokenData.getUserId());
        Date now = new Date();
        reportDblink.setCreateTime(now);
        reportDblink.setUpdateTime(now);
        reportDblink.setAppCode(tokenData.getAppCode());
        return reportDblink;
    }
}
