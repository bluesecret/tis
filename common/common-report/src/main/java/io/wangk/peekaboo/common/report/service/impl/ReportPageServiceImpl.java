package io.wangk.peekaboo.common.report.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import io.wangk.peekaboo.common.redis.util.CommonRedisUtil;
import io.wangk.peekaboo.common.report.dao.ReportPageMapper;
import io.wangk.peekaboo.common.report.model.ReportPage;
import io.wangk.peekaboo.common.report.service.ReportPageService;
import io.wangk.peekaboo.common.report.util.ReportRedisKeyUtil;
import io.wangk.peekaboo.common.core.annotation.MyDataSourceResolver;
import io.wangk.peekaboo.common.core.base.dao.BaseDaoMapper;
import io.wangk.peekaboo.common.core.base.service.BaseService;
import io.wangk.peekaboo.common.core.constant.ApplicationConstant;
import io.wangk.peekaboo.common.core.object.MyRelationParam;
import io.wangk.peekaboo.common.core.object.TokenData;
import io.wangk.peekaboo.common.core.util.DefaultDataSourceResolver;
import io.wangk.peekaboo.common.sequence.wrapper.IdGeneratorWrapper;
import com.github.pagehelper.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 报表页面数据操作服务类。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Slf4j
@MyDataSourceResolver(
        resolver = DefaultDataSourceResolver.class,
        intArg = ApplicationConstant.COMMON_REPORT_DATASOURCE_TYPE)
@Service("reportPageService")
public class ReportPageServiceImpl extends BaseService<ReportPage, Long> implements ReportPageService {

    @Autowired
    private ReportPageMapper reportPageMapper;
    @Autowired
    private IdGeneratorWrapper idGenerator;
    @Autowired
    private CommonRedisUtil commonRedisUtil;

    /**
     * 返回当前Service的主表Mapper对象。
     *
     * @return 主表Mapper对象。
     */
    @Override
    protected BaseDaoMapper<ReportPage> mapper() {
        return reportPageMapper;
    }

    /**
     * 保存新增对象。
     *
     * @param reportPage 新增对象。
     * @return 返回新增对象。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ReportPage saveNew(ReportPage reportPage) {
        reportPageMapper.insert(this.buildDefaultValue(reportPage));
        return reportPage;
    }

    /**
     * 更新数据对象。
     *
     * @param reportPage         更新的对象。
     * @param originalReportPage 原有数据对象。
     * @return 成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean update(ReportPage reportPage, ReportPage originalReportPage) {
        commonRedisUtil.evictFormCache(this.makeReportPageKey(originalReportPage.getPageCode()));
        TokenData tokenData = TokenData.takeFromRequest();
        reportPage.setTenantId(tokenData.getTenantId());
        reportPage.setAppCode(tokenData.getAppCode());
        reportPage.setCreateUserId(originalReportPage.getCreateUserId());
        reportPage.setUpdateUserId(tokenData.getUserId());
        reportPage.setCreateTime(originalReportPage.getCreateTime());
        reportPage.setUpdateTime(new Date());
        // 这里重点提示，在执行主表数据更新之前，如果有哪些字段不支持修改操作，请用原有数据对象字段替换当前数据字段。
        UpdateWrapper<ReportPage> uw = this.createUpdateQueryForNullValue(reportPage, reportPage.getPageId());
        boolean r = reportPageMapper.update(reportPage, uw) == 1;
        commonRedisUtil.evictFormCache(this.makeReportPageKey(originalReportPage.getPageCode()));
        return r;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean remove(ReportPage reportPage) {
        commonRedisUtil.evictFormCache(this.makeReportPageKey(reportPage.getPageCode()));
        boolean r = reportPageMapper.deleteById(reportPage.getPageId()) == 1;
        commonRedisUtil.evictFormCache(this.makeReportPageKey(reportPage.getPageCode()));
        return r;
    }

    /**
     * 获取单表查询结果。由于没有关联数据查询，因此在仅仅获取单表数据的场景下，效率更高。
     * 如果需要同时获取关联数据，请移步(getReportPageListWithRelation)方法。
     *
     * @param filter  过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    @Override
    public List<ReportPage> getReportPageList(ReportPage filter, String orderBy) {
        if (filter == null) {
            filter = new ReportPage();
        }
        TokenData tokenData = TokenData.takeFromRequest();
        filter.setAppCode(tokenData.getAppCode());
        filter.setTenantId(tokenData.getTenantId());
        return reportPageMapper.getReportPageList(filter, orderBy);
    }

    /**
     * 获取主表的查询结果，以及主表关联的字典数据和一对一从表数据，以及一对一从表的字典数据。
     * 该查询会涉及到一对一从表的关联过滤，或一对多从表的嵌套关联过滤，因此性能不如单表过滤。
     * 如果仅仅需要获取主表数据，请移步(getReportPageList)，以便获取更好的查询性能。
     *
     * @param filter 主表过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    @Override
    public List<ReportPage> getReportPageListWithRelation(ReportPage filter, String orderBy) {
        List<ReportPage> resultList = this.getReportPageList(filter, orderBy);
        // 在缺省生成的代码中，如果查询结果resultList不是Page对象，说明没有分页，那么就很可能是数据导出接口调用了当前方法。
        // 为了避免一次性的大量数据关联，规避因此而造成的系统运行性能冲击，这里手动进行了分批次读取，开发者可按需修改该值。
        int batchSize = resultList instanceof Page ? 0 : 1000;
        this.buildRelationForDataList(resultList, MyRelationParam.normal(), batchSize);
        return resultList;
    }

    @Override
    public ReportPage getReportPageFromCache(String pageCode) {
        LambdaQueryWrapper<ReportPage> qw = new LambdaQueryWrapper<>();
        qw.eq(ReportPage::getPageCode, pageCode);
        String appCode = TokenData.takeFromRequest().getAppCode();
        if (StrUtil.isBlank(appCode)) {
            qw.isNull(ReportPage::getAppCode);
        } else {
            qw.eq(ReportPage::getAppCode, appCode);
        }
        String key = this.makeReportPageKey(pageCode);
        return commonRedisUtil.getFromCacheWithQueryWrapper(key, qw, reportPageMapper::selectOne, ReportPage.class);
    }

    @Override
    public boolean existByPageCode(String pageCode) {
        ReportPage filter = new ReportPage();
        filter.setPageCode(pageCode);
        return CollUtil.isNotEmpty(this.getReportPageList(filter, null));
    }

    private ReportPage buildDefaultValue(ReportPage reportPage) {
        reportPage.setPageId(idGenerator.nextLongId());
        TokenData tokenData = TokenData.takeFromRequest();
        reportPage.setTenantId(tokenData.getTenantId());
        reportPage.setCreateUserId(tokenData.getUserId());
        reportPage.setUpdateUserId(tokenData.getUserId());
        Date now = new Date();
        reportPage.setCreateTime(now);
        reportPage.setUpdateTime(now);
        reportPage.setAppCode(tokenData.getAppCode());
        return reportPage;
    }

    private String makeReportPageKey(String pageCode) {
        String key;
        String appCode = TokenData.takeFromRequest().getAppCode();
        if (StrUtil.isBlank(appCode)) {
            key = ReportRedisKeyUtil.makeReportPageKey(pageCode);
        } else {
            key = ReportRedisKeyUtil.makeReportPageKey(appCode + "--" + pageCode);
        }
        return key;
    }
}
