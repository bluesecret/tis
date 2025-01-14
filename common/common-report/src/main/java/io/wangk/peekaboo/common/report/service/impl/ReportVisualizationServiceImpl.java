package io.wangk.peekaboo.common.report.service.impl;

import cn.hutool.core.util.BooleanUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import io.wangk.peekaboo.common.core.base.dao.BaseDaoMapper;
import io.wangk.peekaboo.common.core.constant.GlobalDeletedFlag;
import io.wangk.peekaboo.common.core.object.TokenData;
import io.wangk.peekaboo.common.core.object.MyRelationParam;
import io.wangk.peekaboo.common.core.base.service.BaseService;
import io.wangk.peekaboo.common.report.dao.ReportVisualizationMapper;
import io.wangk.peekaboo.common.report.model.ReportVisualization;
import io.wangk.peekaboo.common.report.service.ReportVisualizationService;
import io.wangk.peekaboo.common.sequence.wrapper.IdGeneratorWrapper;
import com.github.pagehelper.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 报表可视化数据操作服务类。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Slf4j
@Service("reportVisualizationService")
public class ReportVisualizationServiceImpl extends BaseService<ReportVisualization, Long> implements ReportVisualizationService {

    @Autowired
    private ReportVisualizationMapper reportVisualizationMapper;
    @Autowired
    private IdGeneratorWrapper idGenerator;

    @Override
    protected BaseDaoMapper<ReportVisualization> mapper() {
        return reportVisualizationMapper;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ReportVisualization saveNew(ReportVisualization reportVisualization) {
        reportVisualizationMapper.insert(this.buildDefaultValue(reportVisualization));
        return reportVisualization;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean update(ReportVisualization reportVisualization, ReportVisualization originalReportVisualization) {
        reportVisualization.setCreateUserId(originalReportVisualization.getCreateUserId());
        reportVisualization.setUpdateUserId(TokenData.takeFromRequest().getUserId());
        reportVisualization.setCreateTime(originalReportVisualization.getCreateTime());
        reportVisualization.setUpdateTime(new Date());
        // 这里重点提示，在执行主表数据更新之前，如果有哪些字段不支持修改操作，请用原有数据对象字段替换当前数据字段。
        UpdateWrapper<ReportVisualization> uw = this.createUpdateQueryForNullValue(reportVisualization, reportVisualization.getVisualId());
        return reportVisualizationMapper.update(reportVisualization, uw) == 1;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean remove(Long visualId) {
        return reportVisualizationMapper.deleteById(visualId) == 1;
    }

    @Override
    public List<ReportVisualization> getReportVisualizationList(ReportVisualization filter, String orderBy) {
        if (filter == null) {
            filter = new ReportVisualization();
        }
        TokenData tokenData = TokenData.takeFromRequest();
        if (BooleanUtil.isFalse(tokenData.getIsAdmin())) {
            filter.setCreateUserId(tokenData.getUserId());
        }
        return reportVisualizationMapper.getReportVisualizationList(filter, orderBy);
    }

    @Override
    public List<ReportVisualization> getReportVisualizationListWithRelation(ReportVisualization filter, String orderBy) {
        List<ReportVisualization> resultList = this.getReportVisualizationList(filter, orderBy);
        // 在缺省生成的代码中，如果查询结果resultList不是Page对象，说明没有分页，那么就很可能是数据导出接口调用了当前方法。
        // 为了避免一次性的大量数据关联，规避因此而造成的系统运行性能冲击，这里手动进行了分批次读取，开发者可按需修改该值。
        int batchSize = resultList instanceof Page ? 0 : 1000;
        this.buildRelationForDataList(resultList, MyRelationParam.normal(), batchSize);
        return resultList;
    }

    private ReportVisualization buildDefaultValue(ReportVisualization reportVisualization) {
        if (reportVisualization.getVisualId() == null) {
            reportVisualization.setVisualId(idGenerator.nextLongId());
        }
        TokenData tokenData = TokenData.takeFromRequest();
        reportVisualization.setCreateUserId(tokenData.getUserId());
        reportVisualization.setUpdateUserId(tokenData.getUserId());
        Date now = new Date();
        reportVisualization.setCreateTime(now);
        reportVisualization.setUpdateTime(now);
        reportVisualization.setDeletedFlag(GlobalDeletedFlag.NORMAL);
        return reportVisualization;
    }
}
