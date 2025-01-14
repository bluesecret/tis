package io.wangk.peekaboo.common.report.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import io.wangk.peekaboo.common.report.dao.ReportDatasetRelationMapper;
import io.wangk.peekaboo.common.report.model.ReportDatasetRelation;
import io.wangk.peekaboo.common.report.service.ReportDatasetColumnService;
import io.wangk.peekaboo.common.report.service.ReportDatasetRelationService;
import io.wangk.peekaboo.common.report.service.ReportDatasetService;
import com.github.pagehelper.Page;
import io.wangk.peekaboo.common.core.annotation.MyDataSourceResolver;
import io.wangk.peekaboo.common.core.base.dao.BaseDaoMapper;
import io.wangk.peekaboo.common.core.base.service.BaseService;
import io.wangk.peekaboo.common.core.constant.ApplicationConstant;
import io.wangk.peekaboo.common.core.object.CallResult;
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
 * 数据集关联数据操作服务类。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Slf4j
@MyDataSourceResolver(
        resolver = DefaultDataSourceResolver.class,
        intArg = ApplicationConstant.COMMON_REPORT_DATASOURCE_TYPE)
@Service("reportDatasetRelationService")
public class ReportDatasetRelationServiceImpl extends BaseService<ReportDatasetRelation, Long> implements ReportDatasetRelationService {

    @Autowired
    private ReportDatasetRelationMapper reportDatasetRelationMapper;
    @Autowired
    private ReportDatasetService reportDatasetService;
    @Autowired
    private ReportDatasetColumnService reportDatasetColumnService;
    @Autowired
    private IdGeneratorWrapper idGenerator;

    /**
     * 返回当前Service的主表Mapper对象。
     *
     * @return 主表Mapper对象。
     */
    @Override
    protected BaseDaoMapper<ReportDatasetRelation> mapper() {
        return reportDatasetRelationMapper;
    }

    /**
     * 保存新增对象。
     *
     * @param reportDatasetRelation 新增对象。
     * @return 返回新增对象。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ReportDatasetRelation saveNew(ReportDatasetRelation reportDatasetRelation) {
        reportDatasetRelationMapper.insert(this.buildDefaultValue(reportDatasetRelation));
        return reportDatasetRelation;
    }

    /**
     * 更新数据对象。
     *
     * @param reportDatasetRelation         更新的对象。
     * @param originalReportDatasetRelation 原有数据对象。
     * @return 成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean update(ReportDatasetRelation reportDatasetRelation, ReportDatasetRelation originalReportDatasetRelation) {
        reportDatasetRelation.setAppCode(TokenData.takeFromRequest().getAppCode());
        reportDatasetRelation.setCreateUserId(originalReportDatasetRelation.getCreateUserId());
        reportDatasetRelation.setUpdateUserId(TokenData.takeFromRequest().getUserId());
        reportDatasetRelation.setCreateTime(originalReportDatasetRelation.getCreateTime());
        reportDatasetRelation.setUpdateTime(new Date());
        // 这里重点提示，在执行主表数据更新之前，如果有哪些字段不支持修改操作，请用原有数据对象字段替换当前数据字段。
        UpdateWrapper<ReportDatasetRelation> uw =
                this.createUpdateQueryForNullValue(reportDatasetRelation, reportDatasetRelation.getRelationId());
        return reportDatasetRelationMapper.update(reportDatasetRelation, uw) == 1;
    }

    /**
     * 删除指定数据。
     *
     * @param relationId 主键Id。
     * @return 成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean remove(Long relationId) {
        return reportDatasetRelationMapper.deleteById(relationId) == 1;
    }

    /**
     * 获取单表查询结果。由于没有关联数据查询，因此在仅仅获取单表数据的场景下，效率更高。
     * 如果需要同时获取关联数据，请移步(getReportDatasetRelationListWithRelation)方法。
     *
     * @param filter  过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    @Override
    public List<ReportDatasetRelation> getReportDatasetRelationList(ReportDatasetRelation filter, String orderBy) {
        if (filter == null) {
            filter = new ReportDatasetRelation();
        }
        filter.setAppCode(TokenData.takeFromRequest().getAppCode());
        return reportDatasetRelationMapper.getReportDatasetRelationList(filter, orderBy);
    }

    /**
     * 获取主表的查询结果，以及主表关联的字典数据和一对一从表数据，以及一对一从表的字典数据。
     * 该查询会涉及到一对一从表的关联过滤，或一对多从表的嵌套关联过滤，因此性能不如单表过滤。
     * 如果仅仅需要获取主表数据，请移步(getReportDatasetRelationList)，以便获取更好的查询性能。
     *
     * @param filter 主表过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    @Override
    public List<ReportDatasetRelation> getReportDatasetRelationListWithRelation(ReportDatasetRelation filter, String orderBy) {
        List<ReportDatasetRelation> resultList = this.getReportDatasetRelationList(filter, orderBy);
        // 在缺省生成的代码中，如果查询结果resultList不是Page对象，说明没有分页，那么就很可能是数据导出接口调用了当前方法。
        // 为了避免一次性的大量数据关联，规避因此而造成的系统运行性能冲击，这里手动进行了分批次读取，开发者可按需修改该值。
        int batchSize = resultList instanceof Page ? 0 : 1000;
        this.buildRelationForDataList(resultList, MyRelationParam.normal(), batchSize);
        return resultList;
    }

    /**
     * 根据最新对象和原有对象的数据对比，判断关联的字典数据和多对一主表数据是否都是合法数据。
     *
     * @param reportDatasetRelation 最新数据对象。
     * @param originalReportDatasetRelation 原有数据对象。
     * @return 数据全部正确返回true，否则false。
     */
    @Override
    public CallResult verifyRelatedData(ReportDatasetRelation reportDatasetRelation, ReportDatasetRelation originalReportDatasetRelation) {
        String errorMessageFormat = "数据验证失败，关联的%s并不存在，请刷新后重试！";
        //这里是基于字典的验证。
        if (this.needToVerify(reportDatasetRelation, originalReportDatasetRelation, ReportDatasetRelation::getMasterDatasetId)
                && !reportDatasetService.existId(reportDatasetRelation.getMasterDatasetId())) {
            return CallResult.error(String.format(errorMessageFormat, "主表数据集Id"));
        }
        //这里是基于字典的验证。
        if (this.needToVerify(reportDatasetRelation, originalReportDatasetRelation, ReportDatasetRelation::getMasterColumnId)
                && !reportDatasetColumnService.existId(reportDatasetRelation.getMasterColumnId())) {
            return CallResult.error(String.format(errorMessageFormat, "主表关联字段Id"));
        }
        //这里是基于字典的验证。
        if (this.needToVerify(reportDatasetRelation, originalReportDatasetRelation, ReportDatasetRelation::getSlaveDatasetId)
                && !reportDatasetService.existId(reportDatasetRelation.getSlaveDatasetId())) {
            return CallResult.error(String.format(errorMessageFormat, "从表数据集Id"));
        }
        //这里是基于字典的验证。
        if (this.needToVerify(reportDatasetRelation, originalReportDatasetRelation, ReportDatasetRelation::getSlaveColumnId)
                && !reportDatasetColumnService.existId(reportDatasetRelation.getSlaveColumnId())) {
            return CallResult.error(String.format(errorMessageFormat, "从表关联字段Id"));
        }
        return CallResult.ok();
    }

    private ReportDatasetRelation buildDefaultValue(ReportDatasetRelation reportDatasetRelation) {
        reportDatasetRelation.setRelationId(idGenerator.nextLongId());
        TokenData tokenData = TokenData.takeFromRequest();
        reportDatasetRelation.setCreateUserId(tokenData.getUserId());
        reportDatasetRelation.setUpdateUserId(tokenData.getUserId());
        Date now = new Date();
        reportDatasetRelation.setCreateTime(now);
        reportDatasetRelation.setUpdateTime(now);
        reportDatasetRelation.setAppCode(tokenData.getAppCode());
        return reportDatasetRelation;
    }
}
