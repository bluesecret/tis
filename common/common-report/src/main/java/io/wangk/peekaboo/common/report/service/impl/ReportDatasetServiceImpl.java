package io.wangk.peekaboo.common.report.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.StrFormatter;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.page.PageMethod;
import io.wangk.peekaboo.common.core.annotation.MyDataSourceResolver;
import io.wangk.peekaboo.common.core.constant.ApplicationConstant;
import io.wangk.peekaboo.common.core.constant.DictType;
import io.wangk.peekaboo.common.core.constant.FieldFilterType;
import io.wangk.peekaboo.common.core.constant.GlobalDeletedFlag;
import io.wangk.peekaboo.common.core.exception.MyRuntimeException;
import io.wangk.peekaboo.common.core.object.*;
import io.wangk.peekaboo.common.core.util.MyCommonUtil;
import io.wangk.peekaboo.common.core.util.MyModelUtil;
import io.wangk.peekaboo.common.core.util.DefaultDataSourceResolver;
import io.wangk.peekaboo.common.core.base.dao.BaseDaoMapper;
import io.wangk.peekaboo.common.core.base.service.BaseService;
import io.wangk.peekaboo.common.dbutil.object.DatasetFilter;
import io.wangk.peekaboo.common.dbutil.object.DatasetParam;
import io.wangk.peekaboo.common.dbutil.object.SqlResultSet;
import io.wangk.peekaboo.common.dbutil.object.SqlTableColumn;
import io.wangk.peekaboo.common.dict.model.TenantGlobalDict;
import io.wangk.peekaboo.common.dict.service.GlobalDictService;
import io.wangk.peekaboo.common.dict.service.TenantGlobalDictService;
import io.wangk.peekaboo.common.redis.util.CommonRedisUtil;
import io.wangk.peekaboo.common.sequence.wrapper.IdGeneratorWrapper;
import io.wangk.peekaboo.common.report.config.ReportProperties;
import io.wangk.peekaboo.common.report.dao.ReportDatasetMapper;
import io.wangk.peekaboo.common.report.dao.ReportOperationMapper;
import io.wangk.peekaboo.common.report.model.ReportDataset;
import io.wangk.peekaboo.common.report.model.ReportDatasetColumn;
import io.wangk.peekaboo.common.report.model.ReportDatasetRelation;
import io.wangk.peekaboo.common.report.model.ReportDict;
import io.wangk.peekaboo.common.report.model.constant.DatasetType;
import io.wangk.peekaboo.common.report.model.constant.ReportFieldKind;
import io.wangk.peekaboo.common.report.model.constant.ReportRelationType;
import io.wangk.peekaboo.common.report.object.*;
import io.wangk.peekaboo.common.report.service.*;
import io.wangk.peekaboo.common.report.util.ReportDataSourceUtil;
import io.wangk.peekaboo.common.report.util.ReportRedisKeyUtil;
import com.github.pagehelper.Page;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.util.deparser.ExpressionDeParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 报表数据集数据操作服务类。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Slf4j
@MyDataSourceResolver(
        resolver = DefaultDataSourceResolver.class,
        intArg = ApplicationConstant.COMMON_REPORT_DATASOURCE_TYPE)
@Service("reportDatasetService")
public class ReportDatasetServiceImpl extends BaseService<ReportDataset, Long> implements ReportDatasetService {

    @Autowired
    private ReportDatasetMapper reportDatasetMapper;
    @Autowired
    private ReportOperationMapper reportOperationMapper;
    @Autowired
    private ReportDatasetColumnService reportDatasetColumnService;
    @Autowired
    private ReportDblinkService reportDblinkService;
    @Autowired
    private ReportDatasetGroupService reportDatasetGroupService;
    @Autowired
    private ReportDictService reportDictService;
    @Autowired
    private GlobalDictService globalDictService;
    @Autowired
    private TenantGlobalDictService tenantGlobalDictService;
    @Autowired
    private ReportDataSourceUtil dataSourceUtil;
    @Autowired
    private IdGeneratorWrapper idGenerator;
    @Autowired
    private CommonRedisUtil commonRedisUtil;
    @Autowired
    private ReportProperties reportProperties;

    private static final String REGEX_VAR = "\\$\\{(.+?)\\}";
    private static final String SQL_DATASET_VAR = "__ORANGE_FORM_SQL_DATASET_VAR__";
    private static final String ALWAY_TRUE_EQUAL = " 1 = 1 ";

    /**
     * 返回当前Service的主表Mapper对象。
     *
     * @return 主表Mapper对象。
     */
    @Override
    protected BaseDaoMapper<ReportDataset> mapper() {
        return reportDatasetMapper;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ReportDataset saveNew(ReportDataset reportDataset) {
        this.buildDefaultValue(reportDataset);
        List<SqlTableColumn> columnList = null;
        if (reportDataset.getDatasetType().equals(DatasetType.TABLE)) {
            columnList = dataSourceUtil.getTableColumnList(reportDataset.getDblinkId(), reportDataset.getTableName());
        } else if (reportDataset.getDatasetType().equals(DatasetType.SQL)) {
            this.updateDatasetInfoAllParamNameSet(reportDataset);
            columnList = this.getSqlColumnList(reportDataset);
        }
        if (MyCommonUtil.equalsAny(reportDataset.getDatasetType(), DatasetType.TABLE, DatasetType.SQL)) {
            List<ReportDatasetColumn> datasetColumnList =
                    MyModelUtil.copyCollectionTo(columnList, ReportDatasetColumn.class);
            reportDatasetColumnService.saveNewBatch(reportDataset, datasetColumnList);
        }
        reportDatasetMapper.insert(reportDataset);
        return reportDataset;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean update(ReportDataset reportDataset, ReportDataset originalReportDataset) {
        commonRedisUtil.evictFormCache(ReportRedisKeyUtil.makeReportDatasetKey(reportDataset.getDatasetId()));
        List<SqlTableColumn> columnList = null;
        if (reportDataset.getDatasetType().equals(DatasetType.SQL)) {
            this.updateDatasetInfoAllParamNameSet(reportDataset);
            columnList = this.getSqlColumnList(reportDataset);
        }
        reportDataset.setAppCode(TokenData.takeFromRequest().getAppCode());
        reportDataset.setCreateUserId(originalReportDataset.getCreateUserId());
        reportDataset.setUpdateUserId(TokenData.takeFromRequest().getUserId());
        reportDataset.setCreateTime(originalReportDataset.getCreateTime());
        reportDataset.setUpdateTime(new Date());
        // 这里重点提示，在执行主表数据更新之前，如果有哪些字段不支持修改操作，请用原有数据对象字段替换当前数据字段。
        UpdateWrapper<ReportDataset> uw =
                this.createUpdateQueryForNullValue(reportDataset, reportDataset.getDatasetId());
        if (reportDatasetMapper.update(reportDataset, uw) != 1) {
            return false;
        }
        if (columnList != null) {
            this.doSync(reportDataset, columnList);
        }
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean remove(Long datasetId) {
        commonRedisUtil.evictFormCache(ReportRedisKeyUtil.makeReportDatasetKey(datasetId));
        if (reportDatasetMapper.deleteById(datasetId) == 0) {
            return false;
        }
        reportDatasetColumnService.removeByDatasetId(datasetId);
        return true;
    }

    @Override
    public List<ReportDataset> getReportDatasetList(ReportDataset filter, String orderBy) {
        if (filter == null) {
            filter = new ReportDataset();
        }
        TokenData tokenData = TokenData.takeFromRequest();
        if (BooleanUtil.isTrue(reportProperties.getIsVisualization())
                && BooleanUtil.isFalse(tokenData.getIsAdmin())) {
            filter.setCreateUserId(tokenData.getUserId());
        }
        filter.setAppCode(tokenData.getAppCode());
        List<ReportDataset> resultList;
        if (tokenData.getTenantId() != null) {
            resultList = reportDatasetMapper.getReportDatasetListByTenantId(tokenData.getTenantId(), filter, orderBy);
        } else {
            resultList = reportDatasetMapper.getReportDatasetList(filter, orderBy);
        }
        return resultList;
    }

    @Override
    public List<ReportDataset> getReportDatasetListWithRelation(ReportDataset filter, String orderBy) {
        List<ReportDataset> resultList = this.getReportDatasetList(filter, orderBy);
        int batchSize = resultList instanceof Page ? 0 : 1000;
        this.buildRelationForDataList(resultList, MyRelationParam.normal(), batchSize);
        return resultList;
    }

    @Override
    public List<ReportDataset> getInListWithRelation(Set<Long> datasetIds, MyRelationParam relationParam) {
        List<ReportDataset> resultList;
        TokenData tokenData = TokenData.takeFromRequest();
        if (tokenData != null && tokenData.getTenantId() != null) {
            resultList = reportDatasetMapper.getInListByTenantId(tokenData.getTenantId(), datasetIds);
        } else {
            resultList = this.getInList(datasetIds);
        }
        this.buildRelationForDataList(resultList, relationParam);
        for (ReportDataset dataset : resultList) {
            if (dataset.getDatasetType().equals(DatasetType.API)) {
                ReportDatasetInfo datasetInfo = JSON.parseObject(dataset.getDatasetInfo(), ReportDatasetInfo.class);
                dataset.setColumnList(BeanUtil.copyToList(datasetInfo.getColumnList(), ReportDatasetColumn.class));
            }
        }
        return resultList;
    }

    @Override
    public ReportDataset getById(Serializable datasetId) {
        TokenData tokenData = TokenData.takeFromRequest();
        if (tokenData.getTenantId() == null) {
            return super.getById(datasetId);
        }
        return reportDatasetMapper.getReportDatasetByTenantId(tokenData.getTenantId(), (Long) datasetId);
    }

    @Override
    public ReportDataset getReportDatasetFromCache(Long datasetId) {
        String key = ReportRedisKeyUtil.makeReportDatasetKey(datasetId);
        return commonRedisUtil.getFromCache(key, datasetId, id -> {
            ReportDataset dataset = this.getById(id);
            Set<String> ignoreFields = CollUtil.newHashSet(
                    "datasetTypeDictMap", "dblinkIdDictMap", "reportDblink", "relationList");
            this.buildRelationForData(dataset, MyRelationParam.full(), ignoreFields);
            return dataset;
        }, ReportDataset.class);
    }

    @Override
    public ReportResultSet<Map<String, Object>> getDataList(ReportDataset reportDataset, DatasetParam datasetParam) {
        List<ReportDatasetColumn> columnList = reportDataset.getColumnList();
        if (CollUtil.isNotEmpty(datasetParam.getOrderParam())) {
            Set<String> columnNameSet = columnList
                    .stream().map(ReportDatasetColumn::getColumnName).collect(Collectors.toSet());
            String orderBy = dataSourceUtil.makeOrderBy(
                    reportDataset.getDatasetName(), columnNameSet, datasetParam.getOrderParam());
            datasetParam.setOrderBy(orderBy);
        }
        this.makeTenantFilterParam(reportDataset, datasetParam);
        SqlResultSet<Map<String, Object>> resultSet;
        if (reportDataset.getDatasetType().equals(DatasetType.TABLE)) {
            try {
                if (BooleanUtil.isFalse(reportProperties.getEnabledMultiDatabaseWrite())) {
                    return this.doQueryFromSameDataabase(reportDataset.getDblinkId(),
                            reportDataset.getTableName(), datasetParam, columnList, true);
                }
                resultSet = dataSourceUtil.getTableDataList(
                        reportDataset.getDblinkId(), reportDataset.getTableName(), datasetParam);
            } catch (Exception e) {
                log.error("Failed to call getTableDataList with tableName [" + reportDataset.getTableName() + "].", e);
                throw new MyRuntimeException(e);
            }
        } else {
            try {
                ReportDatasetInfo datasetInfo = JSON.parseObject(reportDataset.getDatasetInfo(), ReportDatasetInfo.class);
                String sql = datasetInfo.getSql();
                if (BooleanUtil.isFalse(datasetParam.getDisableSqlDatasetFilter())) {
                    sql = this.replaceParametersWithFilterParam(datasetInfo, datasetParam);
                }
                sql = this.replaceParametersWithAlwayTrue(sql);
                if (BooleanUtil.isFalse(reportProperties.getEnabledMultiDatabaseWrite())) {
                    return this.doQueryFromSameDataabase(reportDataset.getDblinkId(), sql, datasetParam, columnList, false);
                }
                resultSet = dataSourceUtil.getSqlDataList(reportDataset.getDblinkId(), sql, datasetParam);
            } catch (Exception e) {
                log.error("Failed to call getSqlDataList with datasetName [" + reportDataset.getDatasetName() + "].", e);
                throw new MyRuntimeException(e);
            }
        }
        if (CollUtil.isNotEmpty(datasetParam.getSelectColumnNameList())) {
            Set<String> columnNameSet = new HashSet<>(datasetParam.getSelectColumnNameList());
            columnList = columnList.stream()
                    .filter(c -> columnNameSet.contains(c.getColumnName())).collect(Collectors.toList());
        }
        return ReportResultSet.fromSqlResultSet(resultSet, columnList);
    }

    @Override
    public ReportResultSet<Map<String, Object>> getDataListWithRelation(
            ReportDataset reportDataset, DatasetParam datasetParam) {
        ReportResultSet<Map<String, Object>> resultSet = this.getDataList(reportDataset, datasetParam);
        this.buildDataListWithDict(resultSet.getDataList(), resultSet.getColumnMetaList());
        return resultSet;
    }

    @Override
    public Object getDataByColumnName(ReportDataset reportDataset, DatasetParam datasetParam, String columnName) {
        ReportResultSet<Map<String, Object>> rs = this.getDataList(reportDataset, datasetParam);
        if (ReportResultSet.isEmpty(rs)) {
            return null;
        }
        List<Map<String, Object>> dataList = rs.getDataList();
        if (dataList.size() > 1) {
            String errorMessage = "The data count of current query is greater than 1"; 
            log.error(errorMessage);
            throw new MyRuntimeException(errorMessage);
        }
        return dataList.get(0).get(columnName);
    }

    @Override
    public void buildDataListWithDict(List<Map<String, Object>> resultList, List<ReportDatasetColumn> columnMetaList) {
        if (CollUtil.isEmpty(resultList)) {
            return;
        }
        Set<Long> dictIdSet = new HashSet<>();
        // 先找主表字段对字典的依赖。
        Multimap<Long, String> dictColumnMap = LinkedHashMultimap.create();
        for (ReportDatasetColumn column : columnMetaList) {
            if (column.getDictId() != null) {
                dictIdSet.add(column.getDictId());
                dictColumnMap.put(column.getDictId(), column.getColumnName());
            }
        }
        this.doBuildDataListWithDict(resultList, dictIdSet, dictColumnMap);
    }

    @Override
    public void buildDataListWithRelation(List<Map<String, Object>> resultList, ReportDatasetRelation relation) {
        if (CollUtil.isEmpty(resultList)) {
            return;
        }
        ReportDataset relationDataset =
                this.getByIdWithRelation(relation.getSlaveDatasetId(), MyRelationParam.full());
        String masterColumnName = (String) relation.getMasterColumnIdDictMap().get("name");
        String slaveColumnName = (String) relation.getSlaveColumnIdDictMap().get("name");
        List<Serializable> masterDataList = resultList.stream()
                .map(c -> (Serializable) c.get(masterColumnName)).collect(Collectors.toList());
        DatasetFilter relationParam = new DatasetFilter();
        DatasetFilter.FilterInfo filterInfo = new DatasetFilter.FilterInfo();
        filterInfo.setParamName(slaveColumnName);
        filterInfo.setFilterType(FieldFilterType.IN);
        filterInfo.setParamValueList(masterDataList);
        relationParam.add(filterInfo);
        DatasetParam datasetParam = new DatasetParam();
        datasetParam.setFilter(relationParam);
        ReportResultSet<Map<String, Object>> rs = this.getDataListWithRelation(relationDataset, datasetParam);
        if (rs == null || CollUtil.isEmpty(rs.getDataList())) {
            return;
        }
        Map<Object, List<Map<String, Object>>> relationDataMap =
                rs.getDataList().stream().collect(Collectors.groupingBy(c -> c.get(slaveColumnName)));
        for (Map<String, Object> masterData : resultList) {
            Object masterColumnData = masterData.get(masterColumnName);
            if (masterColumnData == null) {
                continue;
            }
            List<Map<String, Object>> slaveData = relationDataMap.get(masterColumnData);
            if (CollUtil.isNotEmpty(slaveData)) {
                if (relation.getRelationType().equals(ReportRelationType.ONE_TO_ONE)) {
                    masterData.put(relation.getVariableName(), slaveData.get(0));
                } else if (relation.getRelationType().equals(ReportRelationType.ONE_TO_MANY)) {
                    masterData.put(relation.getVariableName(), slaveData);
                }
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CallResult sync(ReportDataset dataset) {
        commonRedisUtil.evictFormCache(ReportRedisKeyUtil.makeReportDatasetKey(dataset.getDatasetId()));
        List<SqlTableColumn> columnList;
        Long dblinkId = dataset.getDblinkId();
        if (dataset.getDatasetType().equals(DatasetType.TABLE)) {
            columnList = dataSourceUtil.getTableColumnList(dblinkId, dataset.getTableName());
        } else {
            columnList = this.getSqlColumnList(dataset);
        }
        this.doSync(dataset, columnList);
        return CallResult.ok();
    }

    @Override
    public CallResult verifyRelatedData(ReportDataset reportDataset, ReportDataset originalReportDataset) {
        if (reportDataset.getDatasetType().equals(DatasetType.TABLE)) {
            if (StrUtil.isBlank(reportDataset.getTableName())) {
                return CallResult.error("数据表类型的数据集 [" + reportDataset.getDatasetName() + "]，表名称不能为空！");
            }
            if (dataSourceUtil.getTable(reportDataset.getDblinkId(), reportDataset.getTableName()) == null) {
                return CallResult.error("数据表类型的数据集 [" + reportDataset.getDatasetName()
                        + "] 关联的数据表 [" + reportDataset.getTableName() + " ] 并不存在！");
            }
        } else if (reportDataset.getDatasetType().equals(DatasetType.SQL)) {
            if (StrUtil.isBlank(reportDataset.getDatasetInfo())) {
                return CallResult.error("SQL类型数据集的 [ datasetInfo ] 字段不能为空！");
            }
            ReportDatasetInfo datasetInfo = JSON.parseObject(reportDataset.getDatasetInfo(), ReportDatasetInfo.class);
            if (StrUtil.isBlank(datasetInfo.getSql())) {
                return CallResult.error("SQL类型数据集的 [ datasetInfo ] 字段没有包含SQL语句！");
            }
        }
        String errorMessageFormat = "数据验证失败，关联的%s并不存在，请刷新后重试！";
        //这里是基于字典的验证。
        if (this.needToVerify(reportDataset, originalReportDataset, ReportDataset::getDblinkId)
                && !reportDblinkService.existId(reportDataset.getDblinkId())) {
            return CallResult.error(String.format(errorMessageFormat, "数据库链接Id"));
        }
        if (this.needToVerify(reportDataset, originalReportDataset, ReportDataset::getGroupId)
                && !reportDatasetGroupService.existId(reportDataset.getGroupId())) {
            return CallResult.error(String.format(errorMessageFormat, "数据集分组Id"));
        }
        return CallResult.ok();
    }

    @Override
    public String replaceParametersWithFilterParam(ReportDatasetInfo datasetInfo, DatasetParam datasetParam) {
        String sql = datasetInfo.getSql();
        if (CollUtil.isNotEmpty(datasetParam.getSqlFilter())) {
            for (DatasetFilter.FilterInfo filterInfo : datasetParam.getSqlFilter()) {
                String paramValue = filterInfo.getParamValue().toString();
                String paramName = filterInfo.getParamName();
                if (!CollUtil.contains(datasetInfo.getAllParamNameSet(), paramName)) {
                    String msg = StrFormatter.format(
                            "The SQL Dataset doesn't has ParamName [{}].", paramName);
                    throw new MyRuntimeException(msg);
                }
                Matcher matcher = ApplicationConstant.SQL_INJECT_PATTERN.matcher(paramValue.toLowerCase());
                if (matcher.find()) {
                    String msg = StrFormatter.format(
                            "The ParamValue [{}] of ParamName [{}] has SQL Inject Words", paramValue, paramName);
                    throw new MyRuntimeException(msg);
                }
                sql = StrUtil.replace(sql, "${" + paramName + "}", paramValue);
            }
        }
        if (CollUtil.isNotEmpty(datasetInfo.getParamList())) {
            for (ReportDatasetInfo.SqlDatasetParam sqlParam : datasetInfo.getParamList()) {
                if (StrUtil.isNotBlank(sqlParam.getDefaultValue())) {
                    sql = StrUtil.replace(sql, "${" + sqlParam.getParamName() + "}", sqlParam.getDefaultValue());
                }
            }
        }
        return sql;
    }

    @Override
    public String replaceParametersWithAlwayTrue(String sql) throws JSQLParserException {
        Pattern pattern = Pattern.compile(REGEX_VAR);
        Matcher matcher = pattern.matcher(sql);
        boolean existVariable = false;
        while (matcher.find()) {
            existVariable = true;
            sql = sql.replace(matcher.group(), SQL_DATASET_VAR);
        }
        if (!existVariable && !sql.contains(SQL_DATASET_VAR)) {
            return sql;
        }
        Statement statement = CCJSqlParserUtil.parse(sql);
        Select select = (Select) statement;
        PlainSelect plainSelect = ((PlainSelect) select.getSelectBody());
        FromItem fromItem = plainSelect.getFromItem();
        if (fromItem instanceof SubSelect) {
            SelectBody selectBody = ((SubSelect) fromItem).getSelectBody();
            plainSelect.setFromItem(this.makeNewSubSelect(selectBody, fromItem.getAlias()));
        }
        this.handeJoinClause(plainSelect);
        Expression expr = plainSelect.getWhere();
        if (expr == null) {
            return handleWithClause(plainSelect, select);
        }
        StringBuilder sb = new StringBuilder();
        BinaryExpression binaryExpression = null;
        if (expr instanceof BinaryExpression) {
            binaryExpression = (BinaryExpression) expr;
        }
        if (binaryExpression != null && existVariable(binaryExpression)) {
            if (isStepInto(binaryExpression)) {
                expr.accept(getExpressionDeParser(sb));
            } else {
                sb.append(ALWAY_TRUE_EQUAL);
            }
        } else {
            expr.accept(getExpressionDeParser(sb));
        }
        plainSelect.setWhere(CCJSqlParserUtil.parseCondExpression(sb.toString()));
        return handleWithClause(plainSelect, select);
    }

    private ReportResultSet<Map<String, Object>> doQueryFromSameDataabase(
            Long dblinkId, String tableOrSql, DatasetParam param, List<ReportDatasetColumn> columnList, boolean tableQuery) {
        String sql = dataSourceUtil.getQuerySql(dblinkId, tableOrSql, param, tableQuery);
        MyPageParam pageParam = param.getPageParam();
        if (pageParam != null) {
            PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        }
        List<Map<String, Object>> dataList = reportOperationMapper.execQuery(sql);
        if (CollUtil.isNotEmpty(param.getSelectColumnNameList())) {
            Set<String> columnNameSet = new HashSet<>(param.getSelectColumnNameList());
            columnList = columnList.stream()
                    .filter(c -> columnNameSet.contains(c.getColumnName())).collect(Collectors.toList());
        }
        ReportResultSet<Map<String, Object>> rs = new ReportResultSet<>();
        rs.setColumnMetaList(columnList);
        rs.setDataList(dataList);
        rs.setTotalCount((long) dataList.size());
        if (dataList instanceof Page) {
            rs.setTotalCount(((Page) dataList).getTotal());
        }
        return rs;
    }

    private SubSelect makeNewSubSelect(SelectBody selectBody, Alias alias) throws JSQLParserException {
        if (alias == null) {
            String msg = StrFormatter.format(
                    "Alias doesn't exist from Nested SubQuery[{}]", selectBody.toString());
            throw new MyRuntimeException(msg);
        }
        SubSelect subSelect = new SubSelect();
        Select subSelectTmp = (Select) CCJSqlParserUtil.parse(replaceParametersWithAlwayTrue(selectBody.toString()));
        subSelect.setSelectBody(subSelectTmp.getSelectBody());
        subSelect.setAlias(alias);
        return subSelect;
    }

    private void handeJoinClause(PlainSelect plainSelect) throws JSQLParserException {
        List<Join> joins = plainSelect.getJoins();
        if (CollUtil.isEmpty(joins)) {
            return;
        }
        for (Join join : joins) {
            if (join.getRightItem() instanceof SubSelect) {
                SubSelect rightItem = (SubSelect) join.getRightItem();
                join.setRightItem(this.makeNewSubSelect(rightItem.getSelectBody(), rightItem.getAlias()));
            }
        }
    }

    private void makeTenantFilterParam(ReportDataset dataset, DatasetParam datasetParam) {
        if (TokenData.takeFromRequest().getTenantId() == null) {
            return;
        }
        Collection<ReportDatasetColumn> columnList = dataset.getColumnList();
        if (columnList == null) {
            columnList = dataset.getColumnMap().values();
        }
        ReportDatasetColumn tenantFilterColumn =
                columnList.stream().filter(c -> BooleanUtil.isTrue(c.getTenantFilter())).findFirst().orElse(null);
        if (tenantFilterColumn != null) {
            if (datasetParam.getFilter() == null) {
                datasetParam.setFilter(new DatasetFilter());
            }
            DatasetFilter.FilterInfo filterInfo = new DatasetFilter.FilterInfo();
            filterInfo.setDatasetId(dataset.getDatasetId());
            filterInfo.setParamName(tenantFilterColumn.getColumnName());
            TokenData tokenData = TokenData.takeFromRequest();
            if (tokenData.getTenantId() != null) {
                filterInfo.setFilterType(FieldFilterType.EQUAL);
                filterInfo.setParamValue(tokenData.getTenantId());
            } else {
                filterInfo.setFilterType(FieldFilterType.IS_NULL);
            }
            datasetParam.getFilter().add(filterInfo);
        }
    }

    private String handleWithClause(PlainSelect plainSelect, Select select) throws JSQLParserException {
        StringBuilder sb = new StringBuilder();
        if (CollUtil.isNotEmpty(select.getWithItemsList())) {
            sb.append("WITH ");
            for (Iterator<WithItem> it = select.getWithItemsList().iterator(); it.hasNext(); ) {
                WithItem withItem = it.next();
                sb.append(withItem.getName())
                        .append(" AS ( ")
                        .append(replaceParametersWithAlwayTrue(withItem.getSubSelect().toString()))
                        .append(" ) ");
                if (it.hasNext()) {
                    sb.append(",");
                }
            }
        }
        sb.append(" ").append(plainSelect);
        return sb.toString();
    }

    private void doBuildDataListWithDict(
            List<Map<String, Object>> resultList, Set<Long> dictIdSet, Multimap<Long, String> dictColumnMap) {
        if (CollUtil.isEmpty(dictIdSet)) {
            return;
        }
        List<ReportDict> dictList = reportDictService.getReportDictList(dictIdSet);
        for (ReportDict dict : dictList) {
            Collection<String> columnNameList = dictColumnMap.get(dict.getDictId());
            for (String columnName : columnNameList) {
                Set<Serializable> dictIdDataSet = resultList.stream()
                        .map(o -> (Serializable) o.get(columnName))
                        .filter(ObjectUtil::isNotEmpty).collect(Collectors.toSet());
                if (CollUtil.isNotEmpty(dictIdDataSet)) {
                    this.doBindColumnDictData(resultList, columnName, dict, dictIdDataSet);
                }
            }
        }
    }

    private void doBindColumnDictData(
            List<Map<String, Object>> resultList, String columnName, ReportDict dict, Set<Serializable> dictIdDataSet) {
        Map<String, Object> dictResultMap = this.doBuildColumnDictDataMap(dict, dictIdDataSet);
        if (MapUtil.isEmpty(dictResultMap)) {
            return;
        }
        String dictKeyName = columnName + "__DictMap";
        for (Map<String, Object> result : resultList) {
            Object dictNameData = null;
            Object dictIdData = result.get(columnName);
            if (ObjectUtil.isEmpty(dictIdData)) {
                continue;
            }
            if (dictIdData instanceof Boolean) {
                dictIdData = BooleanUtil.isTrue((Boolean) dictIdData) ? 1 : 0;
            } else if (dictIdData instanceof String) {
                dictNameData = this.processMultiSelection(dictIdData.toString(), dictResultMap);
            }
            if (dictNameData == null) {
                dictNameData = dictResultMap.get(dictIdData.toString());
            }
            Map<String, Object> dictMap = new HashMap<>(2);
            dictMap.put("id", dictIdData);
            dictMap.put("name", dictNameData);
            result.put(dictKeyName, dictMap);
        }
    }

    private String processMultiSelection(String dictIdData, Map<String, Object> dictResultMap) {
        List<Object> dictNameDataList = new ArrayList<>();
        String[] dictIdDataArray = StrUtil.splitToArray(dictIdData, ",");
        if (dictIdDataArray.length > 0) {
            for (String dictIdDataItem : dictIdDataArray) {
                Object dictNameDataItem = dictResultMap.get(dictIdDataItem);
                if (dictNameDataItem != null) {
                    dictNameDataList.add(dictNameDataItem);
                }
            }
        }
        return CollUtil.join(dictNameDataList, ",");
    }

    private Map<Serializable, String> getGlobalDictItemDictMapFromCache(String dictCode, Set<Serializable> itemIds) {
        if (TokenData.takeFromRequest().getTenantId() == null) {
            return globalDictService.getGlobalDictItemDictMapFromCache(dictCode, itemIds);
        }
        TenantGlobalDict dict = tenantGlobalDictService.getTenantGlobalDictFromCache(dictCode);
        if (dict == null) {
            throw new MyRuntimeException("绑定的租户全局编码字典 [" + dictCode + "] 并不存在！");
        }
        return tenantGlobalDictService.getGlobalDictItemDictMapFromCache(dict, itemIds);
    }

    private Map<String, Object> doBuildColumnDictDataMap(ReportDict dict, Set<Serializable> dictIdDataSet) {
        Map<String, Object> dictResultMap = new HashMap<>(dictIdDataSet.size());
        if (dict.getDictType().equals(DictType.GLOBAL_DICT)) {
            Map<Serializable, String> dictDataMap =
                    this.getGlobalDictItemDictMapFromCache(dict.getDictCode(), dictIdDataSet);
            dictResultMap = new HashMap<>(dictDataMap.size());
            for (Map.Entry<Serializable, String> entry : dictDataMap.entrySet()) {
                dictResultMap.put(entry.getKey().toString(), entry.getValue());
            }
        } else if (dict.getDictType().equals(DictType.CUSTOM)) {
            ConstDictInfo dictInfo =
                    JSONObject.parseObject(dict.getDictDataJson(), ConstDictInfo.class);
            List<ConstDictInfo.ConstDictData> dictDataList = dictInfo.getDictData();
            dictResultMap = new HashMap<>(dictDataList.size());
            for (ConstDictInfo.ConstDictData dictData : dictDataList) {
                dictResultMap.put(dictData.getId().toString(), dictData.getName());
            }
        } else {
            DatasetFilter filter = new DatasetFilter();
            if (StrUtil.isNotBlank(dict.getDeletedColumnName())) {
                DatasetFilter.FilterInfo logicDeleteFilter = new DatasetFilter.FilterInfo();
                logicDeleteFilter.setParamName(dict.getDeletedColumnName());
                logicDeleteFilter.setFilterType(FieldFilterType.EQUAL);
                logicDeleteFilter.setParamValue(GlobalDeletedFlag.NORMAL);
                filter.add(logicDeleteFilter);
            }
            DatasetFilter.FilterInfo inlistFilter = new DatasetFilter.FilterInfo();
            inlistFilter.setParamName(dict.getKeyColumnName());
            inlistFilter.setParamValueList(dictIdDataSet);
            inlistFilter.setFilterType(FieldFilterType.IN);
            filter.add(inlistFilter);
            try {
                DatasetParam datasetParam = new DatasetParam();
                List<String> selectFields = reportDictService.makeDictSelectFields(dict);
                datasetParam.setSelectColumnNameList(selectFields);
                datasetParam.setFilter(filter);
                SqlResultSet<Map<String, Object>> rs =
                        dataSourceUtil.getTableDataList(dict.getDblinkId(), dict.getTableName(), datasetParam);
                if (SqlResultSet.isEmpty(rs)) {
                    return dictResultMap;
                }
                dictResultMap = new HashMap<>(rs.getDataList().size());
                for (Map<String, Object> dictResult : rs.getDataList()) {
                    dictResultMap.put(getDictValue(dictResult, "id"), getDictValue(dictResult, "name"));
                }
            } catch (Exception e) {
                log.error("failed to call getTableDataList", e);
                throw new MyRuntimeException(e);
            }
        }
        return dictResultMap;
    }

    private String getDictValue(Map<String, Object> dictMap, String key) {
        Object o = dictMap.get(key);
        if (o == null) {
            o = dictMap.get(key.toUpperCase());
        }
        return o.toString();
    }

    private ReportDataset buildDefaultValue(ReportDataset reportDataset) {
        reportDataset.setDatasetId(idGenerator.nextLongId());
        TokenData tokenData = TokenData.takeFromRequest();
        reportDataset.setCreateUserId(tokenData.getUserId());
        reportDataset.setUpdateUserId(tokenData.getUserId());
        Date now = new Date();
        reportDataset.setCreateTime(now);
        reportDataset.setUpdateTime(now);
        reportDataset.setAppCode(tokenData.getAppCode());
        return reportDataset;
    }

    private void updateDatasetInfoAllParamNameSet(ReportDataset reportDataset) {
        ReportDatasetInfo datasetInfo = JSON.parseObject(reportDataset.getDatasetInfo(), ReportDatasetInfo.class);
        List<String> allParamNames = ReUtil.findAll(REGEX_VAR, datasetInfo.getSql(), 0);
        if (CollUtil.isNotEmpty(allParamNames)) {
            datasetInfo.setAllParamNameSet(allParamNames.stream()
                    .map(v -> v.substring(2, v.length() - 1)).collect(Collectors.toSet()));
            reportDataset.setDatasetInfo(JSON.toJSONString(datasetInfo));
        }
    }

    private List<SqlTableColumn> getSqlColumnList(ReportDataset dataset) {
        ReportDatasetInfo datasetInfo = JSON.parseObject(dataset.getDatasetInfo(), ReportDatasetInfo.class);
        String sql = datasetInfo.getSql();
        if (CollUtil.isNotEmpty(datasetInfo.getParamList())) {
            for (ReportDatasetInfo.SqlDatasetParam sqlParam : datasetInfo.getParamList()) {
                if (ObjectUtil.isEmpty(sqlParam.getDefaultValue())) {
                    continue;
                }
                Matcher matcher = ApplicationConstant
                        .SQL_INJECT_PATTERN.matcher(sqlParam.getDefaultValue().toLowerCase());
                if (matcher.find()) {
                    String msg = StrFormatter.format(
                            "The default value [{}] of ParamName [{}] has SQL Inject Words",
                            sqlParam.getDefaultValue(), sqlParam.getParamName());
                    throw new MyRuntimeException(msg);
                }
                sql = StrUtil.replace(sql, "${" + sqlParam.getParamName() + "}", sqlParam.getDefaultValue());
            }
        }
        try {
            sql = this.replaceParametersWithAlwayTrue(sql);
            List<SqlTableColumn> columnList = dataSourceUtil.getSqlColumnList(dataset.getDblinkId(), sql);
            for (int i = 0; i < columnList.size(); i++) {
                columnList.get(i).setColumnShowOrder(i + 1);
            }
            return columnList;
        } catch (JSQLParserException e) {
            String errorMsg = "Failed to parse [" + datasetInfo.getSql() + "].";
            log.error(errorMsg, e);
            throw new MyRuntimeException(errorMsg);
        }
    }

    private void doSync(ReportDataset reportDataset, List<SqlTableColumn> sqlTableColumnList) {
        List<ReportDatasetColumn> newDatasetColumnList =
                MyModelUtil.copyCollectionTo(sqlTableColumnList, ReportDatasetColumn.class);
        // 获取数据集的原有字段列表。
        Map<String, ReportDatasetColumn> originalDatasetColumnMap =
                reportDatasetColumnService.getReportDatasetColumnListByDatasetId(reportDataset.getDatasetId())
                        .stream().collect(Collectors.toMap(ReportDatasetColumn::getColumnName, c -> c));
        // 如果字段名存在，就要保证columnId不变。
        for (ReportDatasetColumn c : newDatasetColumnList) {
            ReportDatasetColumn o = originalDatasetColumnMap.get(c.getColumnName());
            if (o != null) {
                c.setColumnId(o.getColumnId());
                c.setColumnComment(o.getColumnComment());
                c.setDictId(o.getDictId());
                c.setFieldKind(o.getFieldKind());
                c.setImage(o.getImage());
                c.setLogicDelete(o.getLogicDelete());
                c.setDimension(o.getDimension());
                c.setUserFilter(o.getUserFilter());
                c.setDeptFilter(o.getDeptFilter());
                c.setTenantFilter(o.getTenantFilter());
            }
        }
        // 先删除所有普通字段，后插入新字段列表。
        ReportDatasetColumn filter = new ReportDatasetColumn();
        filter.setDatasetId(reportDataset.getDatasetId());
        filter.setFieldKind(ReportFieldKind.NORMAL);
        reportDatasetColumnService.removeBy(filter);
        reportDatasetColumnService.saveNewBatch(reportDataset, newDatasetColumnList);
    }

    private ExpressionDeParser getExpressionDeParser(StringBuilder sb) {
        return new MyExpressionDeParser(null, sb);
    }

    private static class MyExpressionDeParser extends ExpressionDeParser {

        public MyExpressionDeParser(SelectVisitor selectVisitor, StringBuilder buffer) {
            super(selectVisitor, buffer);
        }

        @Override
        public void visit(Parenthesis parenthesis) {
            buffer.append("(");
            parenthesis.getExpression().accept(this);
            buffer.append(")");
        }

        @Override
        public void visit(OrExpression or) {
            visitBinaryExpr(or, " OR ");
        }

        @Override
        public void visit(AndExpression and) {
            visitBinaryExpr(and, " AND ");
        }

        @Override
        public void visit(Between between) {
            if (existVariable(between.getBetweenExpressionStart())
                    || existVariable(between.getBetweenExpressionEnd())) {
                buffer.append(ALWAY_TRUE_EQUAL);
            } else {
                buffer.append(between.getLeftExpression())
                        .append(" BETWEEN ")
                        .append(between.getBetweenExpressionStart())
                        .append(" AND ")
                        .append(between.getBetweenExpressionEnd());
            }
        }

        @Override
        public void visit(MinorThan lt) {
            this.visitComparableExpr(lt, " < ");
        }

        @Override
        public void visit(MinorThanEquals le) {
            this.visitComparableExpr(le, " <= ");
        }

        @Override
        public void visit(GreaterThanEquals ge) {
            this.visitComparableExpr(ge, " >= ");
        }

        @Override
        public void visit(GreaterThan gt) {
            this.visitComparableExpr(gt, " > ");
        }

        @Override
        public void visit(ExpressionList list) {
            for (Iterator<Expression> it = list.getExpressions().iterator(); it.hasNext(); ) {
                Expression expression = it.next();
                expression.accept(this);
                if (it.hasNext()) {
                    buffer.append(", ");
                }
            }
        }

        @Override
        public void visit(LikeExpression like) {
            if (existVariable(like)) {
                buffer.append(ALWAY_TRUE_EQUAL);
                return;
            }
            visitBinaryExpression(like, like.isNot() ? " NOT LIKE " : " LIKE ");
            String escape = like.getEscape().toString();
            if (escape != null) {
                buffer.append(" ESCAPE '").append(escape).append('\'');
            }
        }

        @Override
        public void visit(InExpression in) {
            if (in.getRightItemsList() != null
                    && existVariable(in.getRightItemsList().toString())) {
                buffer.append(ALWAY_TRUE_EQUAL);
                return;
            }
            if (in.getRightExpression() != null
                    && in.getRightExpression().toString().equals(SQL_DATASET_VAR)) {
                buffer.append(ALWAY_TRUE_EQUAL);
                return;
            }
            in.getLeftExpression().accept(this);
            buffer.append(in.isNot() ? " NOT IN " : " IN ");
            if (in.getRightItemsList() != null) {
                buffer.append(in.getRightItemsList());
            }
            if (in.getRightExpression() != null) {
                buffer.append(" ( ");
                in.getRightExpression().accept(this);
                buffer.append(" )");
            }
        }

        @Override
        public void visit(SubSelect sub) {
            StringBuilder sb = new StringBuilder();
            Expression in = ((PlainSelect) sub.getSelectBody()).getWhere();
            if (in instanceof BinaryExpression && existVariable(in)) {
                sb.append(SQL_DATASET_VAR);
            } else {
                in.accept(new MyExpressionDeParser(null, sb));
            }
            try {
                Expression where = CCJSqlParserUtil.parseCondExpression(sb.toString());
                ((PlainSelect) sub.getSelectBody()).setWhere(where);
                buffer.append(sub.getSelectBody());
            } catch (JSQLParserException e) {
                log.error("Failed to call visit", e);
                throw new MyRuntimeException(e);
            }
        }

        private void visitComparableExpr(ComparisonOperator expr, String op) {
            if (existVariable(expr.getLeftExpression()) || existVariable(expr.getRightExpression())) {
                buffer.append(ALWAY_TRUE_EQUAL);
            } else {
                buffer.append(expr.getLeftExpression()).append(op).append(expr.getRightExpression());
            }
        }

        private void visitBinaryExpr(BinaryExpression expr, String op) {
            if (expr.getLeftExpression() instanceof BinaryExpression && existVariable(expr.getLeftExpression())) {
                BinaryExpression leftExpr = (BinaryExpression) expr.getLeftExpression();
                if (isStepInto(leftExpr)) {
                    expr.getLeftExpression().accept(this);
                } else {
                    buffer.append(ALWAY_TRUE_EQUAL);
                }
            } else {
                expr.getLeftExpression().accept(this);
            }
            buffer.append(op);
            if (expr.getRightExpression() instanceof BinaryExpression && existVariable(expr.getRightExpression())) {
                BinaryExpression rightExpr = (BinaryExpression) expr.getRightExpression();
                if (isStepInto(rightExpr)) {
                    expr.getRightExpression().accept(this);
                } else {
                    buffer.append(ALWAY_TRUE_EQUAL);
                }
            } else {
                expr.getRightExpression().accept(this);
            }
        }
    }

    private static boolean isStepInto(BinaryExpression expr) {
        return expr.getLeftExpression() instanceof BinaryExpression
                || expr.getLeftExpression() instanceof Parenthesis
                || expr.getRightExpression() instanceof BinaryExpression
                || expr.getRightExpression() instanceof Parenthesis;
    }

    private static boolean existVariable(String sql) {
        return sql.contains(SQL_DATASET_VAR);
    }

    private static boolean existVariable(Expression expr) {
        return existVariable(expr.toString());
    }
}
