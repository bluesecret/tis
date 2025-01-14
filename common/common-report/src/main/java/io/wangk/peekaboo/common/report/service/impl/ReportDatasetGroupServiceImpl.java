package io.wangk.peekaboo.common.report.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import io.wangk.peekaboo.common.report.dao.ReportDatasetGroupMapper;
import io.wangk.peekaboo.common.report.model.ReportDatasetGroup;
import io.wangk.peekaboo.common.report.service.ReportDatasetGroupService;
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

/**
 * 数据集分组数据操作服务类。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Slf4j
@MyDataSourceResolver(
        resolver = DefaultDataSourceResolver.class,
        intArg = ApplicationConstant.COMMON_REPORT_DATASOURCE_TYPE)
@Service("reportDatasetGroupService")
public class ReportDatasetGroupServiceImpl extends BaseService<ReportDatasetGroup, Long> implements ReportDatasetGroupService {

    @Autowired
    private ReportDatasetGroupMapper reportDatasetGroupMapper;
    @Autowired
    private IdGeneratorWrapper idGenerator;

    /**
     * 返回当前Service的主表Mapper对象。
     *
     * @return 主表Mapper对象。
     */
    @Override
    protected BaseDaoMapper<ReportDatasetGroup> mapper() {
        return reportDatasetGroupMapper;
    }

    /**
     * 保存新增对象。
     *
     * @param reportDatasetGroup 新增对象。
     * @return 返回新增对象。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ReportDatasetGroup saveNew(ReportDatasetGroup reportDatasetGroup) {
        reportDatasetGroupMapper.insert(this.buildDefaultValue(reportDatasetGroup));
        return reportDatasetGroup;
    }

    /**
     * 更新数据对象。
     *
     * @param reportDatasetGroup         更新的对象。
     * @param originalReportDatasetGroup 原有数据对象。
     * @return 成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean update(ReportDatasetGroup reportDatasetGroup, ReportDatasetGroup originalReportDatasetGroup) {
        reportDatasetGroup.setAppCode(TokenData.takeFromRequest().getAppCode());
        reportDatasetGroup.setCreateUserId(originalReportDatasetGroup.getCreateUserId());
        reportDatasetGroup.setUpdateUserId(TokenData.takeFromRequest().getUserId());
        reportDatasetGroup.setCreateTime(originalReportDatasetGroup.getCreateTime());
        reportDatasetGroup.setUpdateTime(new Date());
        // 这里重点提示，在执行主表数据更新之前，如果有哪些字段不支持修改操作，请用原有数据对象字段替换当前数据字段。
        UpdateWrapper<ReportDatasetGroup> uw = this.createUpdateQueryForNullValue(reportDatasetGroup, reportDatasetGroup.getGroupId());
        return reportDatasetGroupMapper.update(reportDatasetGroup, uw) == 1;
    }

    /**
     * 删除指定数据。
     *
     * @param groupId 主键Id。
     * @return 成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean remove(Long groupId) {
        return reportDatasetGroupMapper.deleteById(groupId) == 1;
    }

    /**
     * 获取单表查询结果。由于没有关联数据查询，因此在仅仅获取单表数据的场景下，效率更高。
     * 如果需要同时获取关联数据，请移步(getReportDatasetGroupListWithRelation)方法。
     *
     * @param filter  过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    @Override
    public List<ReportDatasetGroup> getReportDatasetGroupList(ReportDatasetGroup filter, String orderBy) {
        if (filter == null) {
            filter = new ReportDatasetGroup();
        }
        filter.setAppCode(TokenData.takeFromRequest().getAppCode());
        return reportDatasetGroupMapper.getReportDatasetGroupList(filter, orderBy);
    }

    /**
     * 获取主表的查询结果，以及主表关联的字典数据和一对一从表数据，以及一对一从表的字典数据。
     * 该查询会涉及到一对一从表的关联过滤，或一对多从表的嵌套关联过滤，因此性能不如单表过滤。
     * 如果仅仅需要获取主表数据，请移步(getReportDatasetGroupList)，以便获取更好的查询性能。
     *
     * @param filter 主表过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    @Override
    public List<ReportDatasetGroup> getReportDatasetGroupListWithRelation(ReportDatasetGroup filter, String orderBy) {
        List<ReportDatasetGroup> resultList = this.getReportDatasetGroupList(filter, orderBy);
        // 在缺省生成的代码中，如果查询结果resultList不是Page对象，说明没有分页，那么就很可能是数据导出接口调用了当前方法。
        // 为了避免一次性的大量数据关联，规避因此而造成的系统运行性能冲击，这里手动进行了分批次读取，开发者可按需修改该值。
        int batchSize = resultList instanceof Page ? 0 : 1000;
        this.buildRelationForDataList(resultList, MyRelationParam.normal(), batchSize);
        return resultList;
    }

    /**
     * 判断指定对象是否包含下级对象。
     *
     * @param groupId 主键Id。
     * @return 存在返回true，否则false。
     */
    @Override
    public boolean hasChildren(Long groupId) {
        ReportDatasetGroup filter = new ReportDatasetGroup();
        filter.setParentId(groupId);
        return getCountByFilter(filter) > 0;
    }

    private ReportDatasetGroup buildDefaultValue(ReportDatasetGroup reportDatasetGroup) {
        reportDatasetGroup.setGroupId(idGenerator.nextLongId());
        TokenData tokenData = TokenData.takeFromRequest();
        reportDatasetGroup.setCreateUserId(tokenData.getUserId());
        reportDatasetGroup.setUpdateUserId(tokenData.getUserId());
        Date now = new Date();
        reportDatasetGroup.setCreateTime(now);
        reportDatasetGroup.setUpdateTime(now);
        reportDatasetGroup.setAppCode(tokenData.getAppCode());
        return reportDatasetGroup;
    }
}
