package io.wangk.peekaboo.common.report.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import io.wangk.peekaboo.common.core.annotation.MyDataSourceResolver;
import io.wangk.peekaboo.common.core.constant.ApplicationConstant;
import io.wangk.peekaboo.common.core.constant.DictType;
import io.wangk.peekaboo.common.core.constant.FieldFilterType;
import io.wangk.peekaboo.common.core.constant.GlobalDeletedFlag;
import io.wangk.peekaboo.common.core.exception.MyRuntimeException;
import io.wangk.peekaboo.common.core.object.*;
import io.wangk.peekaboo.common.core.util.DefaultDataSourceResolver;
import io.wangk.peekaboo.common.dbutil.object.DatasetFilter;
import io.wangk.peekaboo.common.dbutil.object.DatasetParam;
import io.wangk.peekaboo.common.dbutil.object.SqlResultSet;
import io.wangk.peekaboo.common.dict.model.GlobalDictItem;
import io.wangk.peekaboo.common.dict.model.TenantGlobalDict;
import io.wangk.peekaboo.common.dict.model.TenantGlobalDictItem;
import io.wangk.peekaboo.common.dict.service.GlobalDictService;
import io.wangk.peekaboo.common.dict.service.TenantGlobalDictService;
import io.wangk.peekaboo.common.redis.util.CommonRedisUtil;
import io.wangk.peekaboo.common.report.config.ReportProperties;
import io.wangk.peekaboo.common.report.dao.ReportDictMapper;
import io.wangk.peekaboo.common.report.dto.ReportDictFilterDto;
import io.wangk.peekaboo.common.report.model.ReportDict;
import io.wangk.peekaboo.common.report.service.ReportDblinkService;
import io.wangk.peekaboo.common.report.service.ReportDictService;
import io.wangk.peekaboo.common.report.util.ReportDataSourceUtil;
import io.wangk.peekaboo.common.report.util.ReportRedisKeyUtil;
import com.github.pagehelper.Page;
import io.wangk.peekaboo.common.core.base.dao.BaseDaoMapper;
import io.wangk.peekaboo.common.core.base.service.BaseService;
import io.wangk.peekaboo.common.sequence.wrapper.IdGeneratorWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 报表字典数据操作服务类。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Slf4j
@MyDataSourceResolver(
        resolver = DefaultDataSourceResolver.class,
        intArg = ApplicationConstant.COMMON_REPORT_DATASOURCE_TYPE)
@Service("reportDictService")
public class ReportDictServiceImpl extends BaseService<ReportDict, Long> implements ReportDictService {

    @Autowired
    private ReportDictMapper reportDictMapper;
    @Autowired
    private ReportDblinkService reportDblinkService;
    @Autowired
    private ReportDataSourceUtil dataSourceUtil;
    @Autowired
    private GlobalDictService globalDictService;
    @Autowired
    private TenantGlobalDictService tenantGlobalDictService;
    @Autowired
    private IdGeneratorWrapper idGenerator;
    @Autowired
    private CommonRedisUtil commonRedisUtil;
    @Autowired
    private ReportProperties reportProperties;

    /**
     * 返回当前Service的主表Mapper对象。
     *
     * @return 主表Mapper对象。
     */
    @Override
    protected BaseDaoMapper<ReportDict> mapper() {
        return reportDictMapper;
    }

    /**
     * 保存新增对象。
     *
     * @param reportDict 新增对象。
     * @return 返回新增对象。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ReportDict saveNew(ReportDict reportDict) {
        reportDictMapper.insert(this.buildDefaultValue(reportDict));
        return reportDict;
    }

    /**
     * 更新数据对象。
     *
     * @param reportDict         更新的对象。
     * @param originalReportDict 原有数据对象。
     * @return 成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean update(ReportDict reportDict, ReportDict originalReportDict) {
        commonRedisUtil.evictFormCache(ReportRedisKeyUtil.makeReportDictKey(reportDict.getDictId()));
        reportDict.setAppCode(TokenData.takeFromRequest().getAppCode());
        reportDict.setCreateUserId(originalReportDict.getCreateUserId());
        reportDict.setUpdateUserId(TokenData.takeFromRequest().getUserId());
        reportDict.setCreateTime(originalReportDict.getCreateTime());
        reportDict.setUpdateTime(new Date());
        // 这里重点提示，在执行主表数据更新之前，如果有哪些字段不支持修改操作，请用原有数据对象字段替换当前数据字段。
        UpdateWrapper<ReportDict> uw = this.createUpdateQueryForNullValue(reportDict, reportDict.getDictId());
        return reportDictMapper.update(reportDict, uw) == 1;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean remove(ReportDict reportDict) {
        commonRedisUtil.evictFormCache(ReportRedisKeyUtil.makeReportDictKey(reportDict.getDictId()));
        return reportDictMapper.deleteById(reportDict.getDictId()) == 1;
    }

    /**
     * 获取单表查询结果。由于没有关联数据查询，因此在仅仅获取单表数据的场景下，效率更高。
     * 如果需要同时获取关联数据，请移步(getReportDictListWithRelation)方法。
     *
     * @param filter  过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    @Override
    public List<ReportDict> getReportDictList(ReportDict filter, String orderBy) {
        if (filter == null) {
            filter = new ReportDict();
        }
        TokenData tokenData = TokenData.takeFromRequest();
        if (BooleanUtil.isTrue(reportProperties.getIsVisualization())
                && BooleanUtil.isFalse(tokenData.getIsAdmin())) {
            filter.setCreateUserId(tokenData.getUserId());
        }
        filter.setAppCode(tokenData.getAppCode());
        return reportDictMapper.getReportDictList(filter, orderBy);
    }

    /**
     * 获取主表的查询结果，以及主表关联的字典数据和一对一从表数据，以及一对一从表的字典数据。
     * 该查询会涉及到一对一从表的关联过滤，或一对多从表的嵌套关联过滤，因此性能不如单表过滤。
     * 如果仅仅需要获取主表数据，请移步(getReportDictList)，以便获取更好的查询性能。
     *
     * @param filter 主表过滤对象。
     * @param orderBy 排序参数。
     * @return 查询结果集。
     */
    @Override
    public List<ReportDict> getReportDictListWithRelation(ReportDict filter, String orderBy) {
        List<ReportDict> resultList = this.getReportDictList(filter, orderBy);
        // 在缺省生成的代码中，如果查询结果resultList不是Page对象，说明没有分页，那么就很可能是数据导出接口调用了当前方法。
        // 为了避免一次性的大量数据关联，规避因此而造成的系统运行性能冲击，这里手动进行了分批次读取，开发者可按需修改该值。
        int batchSize = resultList instanceof Page ? 0 : 1000;
        this.buildRelationForDataList(resultList, MyRelationParam.normal(), batchSize);
        return resultList;
    }

    @Override
    public List<ReportDict> getReportDictList(Set<Long> dictIdSet) {
        return reportDictMapper.selectBatchIds(dictIdSet);
    }

    @Override
    public ReportDict getReportDictFromCache(Long dictId) {
        String key = ReportRedisKeyUtil.makeReportDictKey(dictId);
        return commonRedisUtil.getFromCache(key, dictId, this::getById, ReportDict.class);
    }

    @Override
    public List<Map<String, Object>> getDataList(ReportDict dict, List<ReportDictFilterDto> filterDtoList) {
        if (dict.getDictType().equals(DictType.TABLE)) {
            List<String> selectFields = this.makeDictSelectFields(dict);
            DatasetParam datasetParam = new DatasetParam();
            datasetParam.setSelectColumnNameList(selectFields);
            datasetParam.setOrderBy("id");
            DatasetFilter filter = new DatasetFilter();
            if (StrUtil.isNotBlank(dict.getDeletedColumnName())) {
                DatasetFilter.FilterInfo logicDeleteFilter = new DatasetFilter.FilterInfo();
                logicDeleteFilter.setParamName(dict.getDeletedColumnName());
                logicDeleteFilter.setFilterType(FieldFilterType.EQUAL);
                logicDeleteFilter.setParamValue(GlobalDeletedFlag.NORMAL);
                filter.add(logicDeleteFilter);
            }
            if (StrUtil.isNotBlank(dict.getTenantFilterColumnName())) {
                DatasetFilter.FilterInfo tenantFilter = new DatasetFilter.FilterInfo();
                tenantFilter.setParamName(dict.getDeletedColumnName());
                tenantFilter.setFilterType(FieldFilterType.EQUAL);
                tenantFilter.setParamValue(TokenData.takeFromRequest().getTenantId());
                filter.add(tenantFilter);
            }
            if (CollUtil.isNotEmpty(filterDtoList)) {
                filterDtoList.forEach(filterDto -> {
                    DatasetFilter.FilterInfo filterInfo = new DatasetFilter.FilterInfo();
                    filterInfo.setParamName(filterDto.getColumnName());
                    filterInfo.setFilterType(FieldFilterType.EQUAL);
                    filterInfo.setParamValue(filterDto.getColumnValue());
                    filter.add(filterInfo);
                });
            }
            datasetParam.setFilter(filter);
            try {
                SqlResultSet<Map<String, Object>> rs =
                        dataSourceUtil.getTableDataList(dict.getDblinkId(), dict.getTableName(), datasetParam);
                return rs == null ? null : rs.getDataList();
            } catch (Exception e) {
                log.error("Failed to call getTableDataList.", e);
                throw new MyRuntimeException(e);
            }
        } else if (dict.getDictType().equals(DictType.CUSTOM)) {
            ConstDictInfo dictInfo = JSONObject.parseObject(dict.getDictDataJson(), ConstDictInfo.class);
            List<ConstDictInfo.ConstDictData> dictDataList = dictInfo.getDictData();
            List<Map<String, Object>> dataList = new LinkedList<>();
            for (ConstDictInfo.ConstDictData dictData : dictDataList) {
                JSONObject dataObject = new JSONObject();
                dataObject.put("id", dictData.getId());
                dataObject.put("name", dictData.getName());
                dataList.add(dataObject);
            }
            return dataList;
        } else if (dict.getDictType().equals(DictType.GLOBAL_DICT)) {
            List<GlobalDictItem> dictItems = this.getGlobalDictItems(dict);
            List<Map<String, Object>> resultList = new LinkedList<>();
            dictItems.forEach(item -> {
                JSONObject data = new JSONObject();
                data.put("id", item.getItemId());
                data.put("name", item.getItemName());
                resultList.add(data);
            });
            return resultList;
        }
        throw new MyRuntimeException("Unsupported ReportDictType [" + dict.getDictType() + "].");
    }

    @Override
    public List<String> makeDictSelectFields(ReportDict dict) {
        List<String> selectColumnList = new LinkedList<>();
        selectColumnList.add(dict.getKeyColumnName() + " id");
        selectColumnList.add(dict.getValueColumnName() + " name");
        if (BooleanUtil.isTrue(dict.getTreeFlag())) {
            selectColumnList.add(dict.getParentKeyColumnName() + " parentId");
        }
        return selectColumnList;
    }

    @Override
    public CallResult verifyRelatedData(ReportDict reportDict, ReportDict originalReportDict) {
        String errorMessageFormat = "数据验证失败，关联的%s并不存在，请刷新后重试！";
        //这里是基于字典的验证。
        if (this.needToVerify(reportDict, originalReportDict, ReportDict::getDblinkId)
                && !reportDblinkService.existId(reportDict.getDblinkId())) {
            return CallResult.error(String.format(errorMessageFormat, "数据库链接主键id"));
        }
        if (reportDict.getDictType().equals(DictType.GLOBAL_DICT)
                && this.needToVerify(reportDict, originalReportDict, ReportDict::getDictCode)) {
            ReportDict filter = new ReportDict();
            filter.setDictCode(reportDict.getDictCode());
            filter.setDictType(reportDict.getDictType());
            if (CollUtil.isNotEmpty(this.getReportDictList(filter, null))) {
                return CallResult.error("数据验证失败，该全局编码字典已被配置！");
            }
        }
        return CallResult.ok();
    }

    private ReportDict buildDefaultValue(ReportDict reportDict) {
        reportDict.setDictId(idGenerator.nextLongId());
        TokenData tokenData = TokenData.takeFromRequest();
        reportDict.setCreateUserId(tokenData.getUserId());
        reportDict.setUpdateUserId(tokenData.getUserId());
        Date now = new Date();
        reportDict.setCreateTime(now);
        reportDict.setUpdateTime(now);
        reportDict.setAppCode(tokenData.getAppCode());
        return reportDict;
    }

    private List<GlobalDictItem> getGlobalDictItems(ReportDict dict) {
        List<GlobalDictItem> dictItems;
        if (TokenData.takeFromRequest().getTenantId() != null) {
            TenantGlobalDict tenantGlobalDict =
                    tenantGlobalDictService.getTenantGlobalDictFromCache(dict.getDictCode());
            List<TenantGlobalDictItem> tenantDictItems =
                    tenantGlobalDictService.getGlobalDictItemListFromCache(tenantGlobalDict, null);
            dictItems = BeanUtil.copyToList(tenantDictItems, GlobalDictItem.class);
        } else {
            dictItems = globalDictService.getGlobalDictItemListFromCache(dict.getDictCode(), null);
        }
        return dictItems;
    }
}
