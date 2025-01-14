package io.wangk.peekaboo.common.report.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import io.wangk.peekaboo.common.core.annotation.MyDataSourceResolver;
import io.wangk.peekaboo.common.core.constant.ApplicationConstant;
import io.wangk.peekaboo.common.core.constant.ObjectFieldType;
import io.wangk.peekaboo.common.core.exception.MyRuntimeException;
import io.wangk.peekaboo.common.core.util.DefaultDataSourceResolver;
import io.wangk.peekaboo.common.dbutil.provider.DataSourceProvider;
import io.wangk.peekaboo.common.redis.util.CommonRedisUtil;
import io.wangk.peekaboo.common.report.dao.ReportDatasetColumnMapper;
import io.wangk.peekaboo.common.report.model.ReportDataset;
import io.wangk.peekaboo.common.report.model.ReportDatasetColumn;
import io.wangk.peekaboo.common.report.model.ReportDblink;
import io.wangk.peekaboo.common.report.model.constant.ReportFieldKind;
import io.wangk.peekaboo.common.report.service.ReportDatasetColumnService;
import io.wangk.peekaboo.common.report.service.ReportDblinkService;
import io.wangk.peekaboo.common.core.base.dao.BaseDaoMapper;
import io.wangk.peekaboo.common.core.base.service.BaseService;
import io.wangk.peekaboo.common.report.util.ReportDataSourceUtil;
import io.wangk.peekaboo.common.report.util.ReportRedisKeyUtil;
import io.wangk.peekaboo.common.sequence.wrapper.IdGeneratorWrapper;
import com.google.common.base.CaseFormat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据集字段数据操作服务类。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Slf4j
@MyDataSourceResolver(
        resolver = DefaultDataSourceResolver.class,
        intArg = ApplicationConstant.COMMON_REPORT_DATASOURCE_TYPE)
@Service("reportDatasetColumnService")
public class ReportDatasetColumnServiceImpl
        extends BaseService<ReportDatasetColumn, Long> implements ReportDatasetColumnService {

    @Autowired
    private ReportDatasetColumnMapper reportDatasetColumnMapper;
    @Autowired
    private ReportDblinkService reportDblinkService;
    @Autowired
    private IdGeneratorWrapper idGenerator;
    @Autowired
    private ReportDataSourceUtil dataSourceUtil;
    @Autowired
    private CommonRedisUtil commonRedisUtil;

    /**
     * 返回当前Service的主表Mapper对象。
     *
     * @return 主表Mapper对象。
     */
    @Override
    protected BaseDaoMapper<ReportDatasetColumn> mapper() {
        return reportDatasetColumnMapper;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveNewBatch(ReportDataset reportDataset, List<ReportDatasetColumn> reportDatasetColumnList) {
        if (CollUtil.isEmpty(reportDatasetColumnList)) {
            return;
        }
        commonRedisUtil.evictFormCache(ReportRedisKeyUtil.makeReportDatasetKey(reportDataset.getDatasetId()));
        ReportDblink dblink = reportDblinkService.getById(reportDataset.getDblinkId());
        for (ReportDatasetColumn column : reportDatasetColumnList) {
            this.buildDefaultValue(dblink.getDblinkType(), reportDataset, column);
        }
        reportDatasetColumnMapper.insertList(reportDatasetColumnList);
    }

    /**
     * 更新数据对象。
     *
     * @param reportDatasetColumn         更新的对象。
     * @param originalReportDatasetColumn 原有数据对象。
     * @return 成功返回true，否则false。
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean update(ReportDatasetColumn reportDatasetColumn, ReportDatasetColumn originalReportDatasetColumn) {
        commonRedisUtil.evictFormCache(ReportRedisKeyUtil.makeReportDatasetKey(reportDatasetColumn.getDatasetId()));
        UpdateWrapper<ReportDatasetColumn> uw =
                this.createUpdateQueryForNullValue(reportDatasetColumn, reportDatasetColumn.getColumnId());
        return reportDatasetColumnMapper.update(reportDatasetColumn, uw) == 1;
    }

    @Override
    public void removeByDatasetId(Long datasetId) {
        LambdaQueryWrapper<ReportDatasetColumn> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ReportDatasetColumn::getDatasetId, datasetId);
        reportDatasetColumnMapper.delete(queryWrapper);
    }

    /**
     * 获取单表查询结果。由于没有关联数据查询，因此在仅仅获取单表数据的场景下，效率更高。
     * 如果需要同时获取关联数据，请移步(getReportDatasetColumnListWithRelation)方法。
     *
     * @param filter 过滤对象。
     * @return 查询结果集。
     */
    @Override
    public List<ReportDatasetColumn> getReportDatasetColumnList(ReportDatasetColumn filter) {
        return reportDatasetColumnMapper.getReportDatasetColumnList(filter);
    }

    @Override
    public List<ReportDatasetColumn> getReportDatasetColumnListByDatasetId(Long datasetId) {
        ReportDatasetColumn filter = new ReportDatasetColumn();
        filter.setDatasetId(datasetId);
        return this.getReportDatasetColumnList(filter);
    }

    @Override
    public Serializable convertToColumnValue(ReportDatasetColumn column, String value) {
        if (value == null) {
            return null;
        }
        switch (column.getFieldType()) {
            case "Long":
                return Convert.toLong(value);
            case "Integer":
                return Convert.toInt(value);
            case "BigDecimal":
                return Convert.toBigDecimal(value);
            case "Double":
                return Convert.toDouble(value);
            case "Boolean":
                return Convert.toBool(value);
            case "Date":
            case "String":
                return value;
            default:
                break;
        }
        return null;
    }

    @Override
    public List<Serializable> convertToColumnValueList(ReportDatasetColumn column, List<String> strValueList) {
        if (CollUtil.isEmpty(strValueList)) {
            return new LinkedList<>();
        }
        return strValueList.stream().map(v -> convertToColumnValue(column, v)).collect(Collectors.toList());
    }

    private ReportDatasetColumn buildDefaultValue(
            Integer dblinkType, ReportDataset dataset, ReportDatasetColumn datasetColumn) {
        if (datasetColumn.getColumnId() == null) {
            datasetColumn.setColumnId(idGenerator.nextLongId());
        }
        if (datasetColumn.getPrimaryKey() == null) {
            datasetColumn.setPrimaryKey(false);
        }
        if (datasetColumn.getColumnComment() == null) {
            datasetColumn.setColumnComment(datasetColumn.getColumnName());
        }
        datasetColumn.setDatasetId(dataset.getDatasetId());
        datasetColumn.setFieldName(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, datasetColumn.getColumnName()));
        datasetColumn.setFieldType(this.convertToJavaType(datasetColumn, dblinkType));
        if (datasetColumn.getFieldKind() == null) {
            datasetColumn.setFieldKind(ReportFieldKind.NORMAL);
        }
        if (datasetColumn.getImage() == null) {
            datasetColumn.setImage(false);
        }
        if (datasetColumn.getLogicDelete() == null) {
            datasetColumn.setLogicDelete(false);
        }
        if (datasetColumn.getDimension() == null) {
            datasetColumn.setDimension(this.possibleDimensionColumn(datasetColumn));
        }
        if (datasetColumn.getDeptFilter() == null) {
            datasetColumn.setDeptFilter(false);
        }
        if (datasetColumn.getUserFilter() == null) {
            datasetColumn.setUserFilter(false);
        }
        if (datasetColumn.getTenantFilter() == null) {
            datasetColumn.setTenantFilter(false);
        }
        return datasetColumn;
    }

    private boolean possibleDimensionColumn(ReportDatasetColumn column) {
        return column.getDictId() != null 
                || StrUtil.equalsAny(column.getFieldType(), ObjectFieldType.STRING, ObjectFieldType.BOOLEAN, ObjectFieldType.DATE)
                || !column.getFieldKind().equals(ReportFieldKind.FUNCTION);
    }

    private String convertToJavaType(ReportDatasetColumn column, int dblinkType) {
        DataSourceProvider provider = dataSourceUtil.getProvider(dblinkType);
        if (provider == null) {
            throw new MyRuntimeException("Unsupported Data Type");
        }
        return provider.convertColumnTypeToJavaType(
                column.getColumnType(), column.getNumericPrecision(), column.getNumericScale());
    }
}
