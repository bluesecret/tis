package io.wangk.peekaboo.common.report.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import io.wangk.peekaboo.common.core.annotation.MyDataSourceResolver;
import io.wangk.peekaboo.common.core.base.dao.BaseDaoMapper;
import io.wangk.peekaboo.common.core.base.service.BaseService;
import io.wangk.peekaboo.common.core.constant.ApplicationConstant;
import io.wangk.peekaboo.common.core.object.MyRelationParam;
import io.wangk.peekaboo.common.core.object.TokenData;
import io.wangk.peekaboo.common.core.util.DefaultDataSourceResolver;
import io.wangk.peekaboo.common.report.dao.ReportPrintGroupMapper;
import io.wangk.peekaboo.common.report.model.ReportPrintGroup;
import io.wangk.peekaboo.common.report.service.ReportPrintGroupService;
import io.wangk.peekaboo.common.sequence.wrapper.IdGeneratorWrapper;
import com.github.pagehelper.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 打印分组数据操作服务类。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Slf4j
@MyDataSourceResolver(
        resolver = DefaultDataSourceResolver.class,
        intArg = ApplicationConstant.COMMON_REPORT_DATASOURCE_TYPE)
@Service("reportPrintGroupService")
public class ReportPrintGroupServiceImpl extends BaseService<ReportPrintGroup, Long> implements ReportPrintGroupService {

    @Autowired
    private ReportPrintGroupMapper reportPrintGroupMapper;
    @Autowired
    private IdGeneratorWrapper idGenerator;

    @Override
    protected BaseDaoMapper<ReportPrintGroup> mapper() {
        return reportPrintGroupMapper;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ReportPrintGroup saveNew(ReportPrintGroup reportPrintGroup) {
        reportPrintGroupMapper.insert(this.buildDefaultValue(reportPrintGroup));
        return reportPrintGroup;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean update(ReportPrintGroup reportPrintGroup, ReportPrintGroup originalReportPrintGroup) {
        TokenData tokenData = TokenData.takeFromRequest();
        reportPrintGroup.setTenantId(tokenData.getTenantId());
        reportPrintGroup.setAppCode(tokenData.getAppCode());
        reportPrintGroup.setCreateUserId(originalReportPrintGroup.getCreateUserId());
        reportPrintGroup.setUpdateUserId(tokenData.getUserId());
        reportPrintGroup.setCreateTime(originalReportPrintGroup.getCreateTime());
        reportPrintGroup.setUpdateTime(new Date());
        // 这里重点提示，在执行主表数据更新之前，如果有哪些字段不支持修改操作，请用原有数据对象字段替换当前数据字段。
        UpdateWrapper<ReportPrintGroup> uw =
                this.createUpdateQueryForNullValue(reportPrintGroup, reportPrintGroup.getGroupId());
        return reportPrintGroupMapper.update(reportPrintGroup, uw) == 1;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean remove(Long groupId) {
        return reportPrintGroupMapper.deleteById(groupId) == 1;
    }

    @Override
    public List<ReportPrintGroup> getReportPrintGroupList(ReportPrintGroup filter, String orderBy) {
        if (filter == null) {
            filter = new ReportPrintGroup();
        }
        TokenData tokenData = TokenData.takeFromRequest();
        filter.setAppCode(tokenData.getAppCode());
        filter.setTenantId(tokenData.getTenantId());
        return reportPrintGroupMapper.getReportPrintGroupList(filter, orderBy);
    }

    @Override
    public List<ReportPrintGroup> getReportPrintGroupListWithRelation(ReportPrintGroup filter, String orderBy) {
        List<ReportPrintGroup> resultList = this.getReportPrintGroupList(filter, orderBy);
        // 在缺省生成的代码中，如果查询结果resultList不是Page对象，说明没有分页，那么就很可能是数据导出接口调用了当前方法。
        // 为了避免一次性的大量数据关联，规避因此而造成的系统运行性能冲击，这里手动进行了分批次读取，开发者可按需修改该值。
        int batchSize = resultList instanceof Page ? 0 : 1000;
        this.buildRelationForDataList(resultList, MyRelationParam.normal(), batchSize);
        return resultList;
    }

    @Override
    public boolean hasChildren(Long groupId) {
        ReportPrintGroup filter = new ReportPrintGroup();
        filter.setParentId(groupId);
        return getCountByFilter(filter) > 0;
    }

    private ReportPrintGroup buildDefaultValue(ReportPrintGroup reportPrintGroup) {
        reportPrintGroup.setGroupId(idGenerator.nextLongId());
        TokenData tokenData = TokenData.takeFromRequest();
        reportPrintGroup.setTenantId(tokenData.getTenantId());
        reportPrintGroup.setCreateUserId(tokenData.getUserId());
        reportPrintGroup.setUpdateUserId(tokenData.getUserId());
        Date now = new Date();
        reportPrintGroup.setCreateTime(now);
        reportPrintGroup.setUpdateTime(now);
        reportPrintGroup.setAppCode(tokenData.getAppCode());
        return reportPrintGroup;
    }
}
