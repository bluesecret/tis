package io.wangk.peekaboo.common.report.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.StrFormatter;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.github.pagehelper.page.PageMethod;
import io.wangk.peekaboo.common.core.cache.CacheConfig;
import io.wangk.peekaboo.common.core.constant.ApplicationConstant;
import io.wangk.peekaboo.common.core.constant.FieldFilterType;
import io.wangk.peekaboo.common.core.constant.GlobalDeletedFlag;
import io.wangk.peekaboo.common.core.exception.MyRuntimeException;
import io.wangk.peekaboo.common.core.exception.NoDataPermException;
import io.wangk.peekaboo.common.core.object.*;
import io.wangk.peekaboo.common.core.util.ContextUtil;
import io.wangk.peekaboo.common.core.util.MyCommonUtil;
import io.wangk.peekaboo.common.core.util.RedisKeyUtil;
import io.wangk.peekaboo.common.core.util.MyPageUtil;
import io.wangk.peekaboo.common.core.constant.DataPermRuleType;
import io.wangk.peekaboo.common.dbutil.object.DatasetFilter;
import io.wangk.peekaboo.common.dbutil.object.DatasetParam;
import io.wangk.peekaboo.common.dbutil.provider.DataSourceProvider;
import io.wangk.peekaboo.common.report.config.ReportProperties;
import io.wangk.peekaboo.common.report.dao.ReportOperationMapper;
import io.wangk.peekaboo.common.report.model.ReportDataset;
import io.wangk.peekaboo.common.report.model.ReportDatasetColumn;
import io.wangk.peekaboo.common.report.model.ReportPage;
import io.wangk.peekaboo.common.report.model.constant.CalculateType;
import io.wangk.peekaboo.common.report.model.constant.DatasetType;
import io.wangk.peekaboo.common.report.model.constant.OrderType;
import io.wangk.peekaboo.common.report.object.*;
import io.wangk.peekaboo.common.report.object.view.ViewDimensionData;
import io.wangk.peekaboo.common.report.object.view.ViewIndexData;
import io.wangk.peekaboo.common.report.object.view.ViewOrderData;
import io.wangk.peekaboo.common.report.service.ReportDatasetColumnService;
import io.wangk.peekaboo.common.report.service.ReportDatasetService;
import io.wangk.peekaboo.common.report.service.ReportOperationService;
import io.wangk.peekaboo.common.report.service.ReportPageService;
import io.wangk.peekaboo.common.report.util.ReportDataSourceUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 在线报表运行时操作的数据服务实现类。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Slf4j
@Service("reportOperationService")
public class ReportOperationServiceImpl implements ReportOperationService {

    @Autowired
    private ReportProperties reportProperties;
    @Autowired
    private ReportDataSourceUtil dataSourceUtil;
    @Autowired
    private RedissonClient redissonClient;
    @Resource(name = "caffeineCacheManager")
    private CacheManager cacheManager;
    @Autowired
    private ReportDatasetService reportDatasetService;
    @Autowired
    private ReportDatasetColumnService reportDatasetColumnService;
    @Autowired
    private ReportPageService reportPageService;
    @Autowired
    private ReportOperationMapper reportOperationMapper;

    private static final String AND = " AND ";
    private static final String WHERE = " WHERE ";
    private static final String ORDER_BY = " ORDER BY ";

    @Override
    public List<Map<String, Object>> getDataListWithGroup(
            ReportDataset dataset,
            List<ViewDimensionData> dimensionDataList,
            List<ViewIndexData> indexDataList,
            List<ReportFilterParam> datasetFilterParams,
            List<ReportFilterParam> filterParams,
            List<ViewOrderData> orderDataList) {
        Map<Long, ReportDatasetColumn> columnMap = dataset.getColumnList()
                .stream().collect(Collectors.toMap(ReportDatasetColumn::getColumnId, c -> c));
        dataset.setColumnMap(dataset.getColumnList()
                .stream().collect(Collectors.toMap(ReportDatasetColumn::getColumnName, c -> c)));
        // 1. select list
        String selectFields = this.makeSelectFields(dimensionDataList, indexDataList, columnMap);
        StringBuilder sql = new StringBuilder(512);
        if (dataset.getDatasetType().equals(DatasetType.TABLE)) {
            sql.append("SELECT ").append(selectFields).append(" FROM ").append(dataset.getTableName());
        } else if (dataset.getDatasetType().equals(DatasetType.SQL)) {
            String subSelect = this.makeSubSelect(dataset, datasetFilterParams);
            sql.append("SELECT ").append(selectFields).append(" FROM (").append(subSelect).append(") tmp");
        }
        // 2. where
        List<Object> paramList = this.makeWhereParamList(sql, dataset, filterParams);
        // 3. group by
        String groupBy = this.makeGroupByClause(dimensionDataList, columnMap);
        sql.append(" GROUP BY ").append(groupBy);
        // 4. having
        String having = this.makeHavingClause(dataset.getDblinkId(), indexDataList, columnMap);
        if (!having.isEmpty()) {
            sql.append(" HAVING ").append(having);
        }
        // 5. order by
        String orderBy = this.makeOrderByClause(dataset.getDblinkId(), orderDataList, columnMap);
        if (!orderBy.isEmpty()) {
            sql.append(ORDER_BY).append(orderBy);
        }
        try {
            if (BooleanUtil.isFalse(reportProperties.getEnabledMultiDatabaseWrite())) {
                String sqlQuery = this.makeupSqlParam(sql.toString(), paramList);
                return reportOperationMapper.execQuery(sqlQuery);
            }
            return dataSourceUtil.query(dataset.getDblinkId(), sql.toString(), paramList);
        } catch (Exception e) {
            log.error("Failed to call query [" + sql + "].", e);
            throw new MyRuntimeException(e);
        }
    }

    @Override
    public MyPageData<Map<String, Object>> getDataListWithPage(
            ReportDataset dataset,
            List<ReportFilterParam> datasetFilterParams,
            List<ReportFilterParam> filterParams,
            List<ViewOrderData> orderDataList,
            MyPageParam pageParam) {
        Map<Long, ReportDatasetColumn> columnMap = dataset.getColumnList()
                .stream().collect(Collectors.toMap(ReportDatasetColumn::getColumnId, c -> c));
        dataset.setColumnMap(dataset.getColumnList()
                .stream().collect(Collectors.toMap(ReportDatasetColumn::getColumnName, c -> c)));
        StringBuilder sql = new StringBuilder(512);
        // 1. select list
        if (dataset.getDatasetType().equals(DatasetType.TABLE)) {
            sql.append("SELECT * FROM ").append(dataset.getTableName());
        } else if (dataset.getDatasetType().equals(DatasetType.SQL)) {
            String subSelect = this.makeSubSelect(dataset, datasetFilterParams);
            sql.append("SELECT * FROM (").append(subSelect).append(") tmp");
        }
        // 2. where
        List<Object> paramList = this.makeWhereParamList(sql, dataset, filterParams);
        // 3. order by
        String orderBy = this.makeOrderByClause(dataset.getDblinkId(), orderDataList, columnMap);
        if (!orderBy.isEmpty()) {
            sql.append(ORDER_BY).append(orderBy);
        }
        try {
            if (BooleanUtil.isFalse(reportProperties.getEnabledMultiDatabaseWrite())) {
                if (pageParam != null) {
                    PageMethod.startPage(pageParam.getPageNum(), pageParam.getPageSize());
                }
                String sqlQuery = this.makeupSqlParam(sql.toString(), paramList);
                List<Map<String, Object>> resultList = reportOperationMapper.execQuery(sqlQuery);
                return MyPageUtil.makeResponseData(resultList);
            }
            if (pageParam == null) {
                List<Map<String, Object>> dataList = dataSourceUtil.query(dataset.getDblinkId(), sql.toString(), paramList);
                return new MyPageData<>(dataList, (long) dataList.size());
            }
            List<Map<String, Object>> dataList = dataSourceUtil.query(dataset.getDblinkId(), sql.toString(), paramList, pageParam);
            String sqlCount = StrUtil.replaceFirst(sql, "SELECT *", "SELECT COUNT(1) TOTAL_COUNT");
            JSONObject totalCount = dataSourceUtil.queryOne(dataset.getDblinkId(), sqlCount, paramList, JSONObject.class);
            return new MyPageData<>(dataList, totalCount.getLong("TOTAL_COUNT"));
        } catch (Exception e) {
            log.error("Failed to call query [" + sql + "].", e);
            throw new MyRuntimeException(e);
        }
    }

    @Override
    public Map<Long, Set<String>> calculatePermData(Set<Long> pageIdSet) {
        Map<Long, Set<String>> resultPermDataMap = new HashMap<>(pageIdSet.size());
        List<ReportPage> pageList = reportPageService.getInList(pageIdSet);
        for (ReportPage page : pageList) {
            Set<String> pagePermUrlSet = reportProperties.getViewUrlList()
                    .stream().map(p -> p + page.getPageCode()).collect(Collectors.toSet());
            resultPermDataMap.put(page.getPageId(), pagePermUrlSet);
        }
        return resultPermDataMap;
    }

    private String makeupSqlParam(String sql, List<Object> paramList) {
        if (CollUtil.isEmpty(paramList)) {
            return sql;
        }
        List<String> parameters = new ArrayList<>();
        paramList.forEach(p -> parameters.add(MyCommonUtil.convertSqlParamValue(p)));
        for (String param : parameters) {
            sql = sql.replaceFirst("\\?", param);
        }
        return sql;
    }

    private List<Object> makeWhereParamList(
            StringBuilder sql, ReportDataset dataset, List<ReportFilterParam> filterParams) {
        List<Object> paramList = null;
        DatasetFilter filter = this.makeDatasetFilter(filterParams, dataset);
        if (CollUtil.isNotEmpty(filter)) {
            Tuple2<String, List<Object>> filterTuple =
                    dataSourceUtil.buildWhereClauseByFilters(dataset.getDblinkId(), filter);
            if (StrUtil.isNotBlank(filterTuple.getFirst())) {
                sql.append(filterTuple.getFirst());
                paramList = filterTuple.getSecond();
            }
        }
        // 在过滤条件中添加数据权限
        String dataPermFilter = this.makeDataPermFilter(dataset);
        if (StrUtil.isNotBlank(dataPermFilter)) {
            this.appendWhereOrAnd(sql);
            sql.append(dataPermFilter);
        }
        ReportDatasetColumn tenantFilterColumn = this.findTenantFilterColumn(dataset);
        if (tenantFilterColumn != null) {
            this.appendWhereOrAnd(sql);
            Long tenantId = TokenData.takeFromRequest().getTenantId();
            if (tenantId == null) {
                sql.append(tenantFilterColumn.getColumnName()).append(" IS NULL ");
            } else {
                sql.append(tenantFilterColumn.getColumnName()).append(" = ").append(tenantId).append(" ");
            }
        }
        return paramList;
    }

    private void appendWhereOrAnd(StringBuilder sql) {
        if (sql.indexOf(WHERE) == -1) {
            sql.append(WHERE);
        } else {
            sql.append(AND);
        }
    }

    private ReportDatasetColumn findTenantFilterColumn(ReportDataset dataset) {
        return dataset.getColumnMap().values()
                .stream().filter(c -> BooleanUtil.isTrue(c.getTenantFilter())).findFirst().orElse(null);
    }

    private String makeSubSelect(ReportDataset dataset, List<ReportFilterParam> datasetFilterParams) {
        ReportDatasetInfo datasetInfo = JSON.parseObject(dataset.getDatasetInfo(), ReportDatasetInfo.class);
        DatasetParam datasetParam = new DatasetParam();
        if (CollUtil.isNotEmpty(datasetFilterParams)) {
            DatasetFilter filter = new DatasetFilter();
            for (ReportFilterParam p : datasetFilterParams) {
                DatasetFilter.FilterInfo filterInfo = new DatasetFilter.FilterInfo();
                filterInfo.setParamName(p.getParamName());
                filterInfo.setParamValue(p.getParamValue());
                filter.add(filterInfo);
            }
            datasetParam.setSqlFilter(filter);
        }
        String subSelect = reportDatasetService.replaceParametersWithFilterParam(datasetInfo, datasetParam);
        try {
            return reportDatasetService.replaceParametersWithAlwayTrue(subSelect);
        } catch (JSQLParserException e) {
            String msg = StrFormatter.format("Failed to parse dataset [{}] with replaced SQL [{}]",
                    dataset.getDatasetName(), subSelect);
            log.error(msg, e);
            throw new MyRuntimeException(e);
        }
    }

    private String makeSelectFields(
            List<ViewDimensionData> dimensionDataList,
            List<ViewIndexData> indexDataList,
            Map<Long, ReportDatasetColumn> columnMap) {
        StringBuilder selectFields = new StringBuilder(128);
        for (ViewDimensionData dimensionData : dimensionDataList) {
            ReportDatasetColumn c = columnMap.get(dimensionData.getColumnId());
            selectFields.append(c.getColumnName()).append(", ");
        }
        for (ViewIndexData indexData : indexDataList) {
            ReportDatasetColumn c = columnMap.get(indexData.getColumnId());
            String f = CalculateType.makeSelectField(
                    indexData.getCalculateType(), c.getColumnName(), c.getFieldName());
            selectFields.append(f).append(", ");
        }
        return selectFields.substring(0, selectFields.length() - 2);
    }

    private String makeDataPermFilter(ReportDataset dataset) {
        if (!GlobalThreadLocal.enabledDataFilter()) {
            return null;
        }
        String deptFilterColumnName = null;
        String userFilterColumnName = null;
        for (ReportDatasetColumn column : dataset.getColumnList()) {
            if (BooleanUtil.isTrue(column.getDeptFilter())) {
                deptFilterColumnName = column.getColumnName();
            }
            if (BooleanUtil.isTrue(column.getUserFilter())) {
                userFilterColumnName = column.getColumnName();
            }
        }
        String datasetName = dataset.getTableName();
        if (!dataset.getDatasetType().equals(DatasetType.TABLE)) {
            datasetName = dataset.getDatasetName();
        }
        return this.processDataPerm(datasetName, deptFilterColumnName, userFilterColumnName);
    }

    private String processDataPerm(String datasetName, String deptFilterColumnName, String userFilterColumnName) {
        TokenData tokenData = TokenData.takeFromRequest();
        if (BooleanUtil.isTrue(tokenData.getIsAdmin())
                || StrUtil.isAllBlank(deptFilterColumnName, userFilterColumnName)) {
            return null;
        }
        String dataPermSessionKey = RedisKeyUtil.makeSessionDataPermIdKey(tokenData.getSessionId());
        Object cachedData = this.getCachedData(dataPermSessionKey);
        if (cachedData == null) {
            throw new NoDataPermException("No Related DataPerm found For ReportPage Module.");
        }
        JSONObject allMenuDataPermMap = cachedData instanceof JSONObject
                ? (JSONObject) cachedData : JSON.parseObject(cachedData.toString());
        JSONObject menuDataPermMap = this.getAndVerfiyMenuDataPerm(allMenuDataPermMap, datasetName);
        Map<Integer, String> dataPermMap = new HashMap<>(8);
        for (Map.Entry<String, Object> entry : menuDataPermMap.entrySet()) {
            dataPermMap.put(Integer.valueOf(entry.getKey()), entry.getValue().toString());
        }
        if (MapUtil.isEmpty(dataPermMap)) {
            throw new NoDataPermException(StrFormatter.format(
                    "No Related ReportPage DataPerm found for dataset [{}].", datasetName));
        }
        if (dataPermMap.containsKey(DataPermRuleType.TYPE_ALL)) {
            return null;
        }
        return doProcessDataPerm(datasetName, deptFilterColumnName, userFilterColumnName, dataPermMap);
    }

    private JSONObject getAndVerfiyMenuDataPerm(JSONObject allMenuDataPermMap, String datasetName) {
        String menuId = ContextUtil.getHttpRequest().getHeader(ApplicationConstant.HTTP_HEADER_MENU_ID);
        if (menuId == null) {
            menuId = ContextUtil.getHttpRequest().getParameter(ApplicationConstant.HTTP_HEADER_MENU_ID);
        }
        if (BooleanUtil.isFalse(reportProperties.getEnableMenuPermVerify()) && menuId == null) {
            menuId = ApplicationConstant.DATA_PERM_ALL_MENU_ID;
        }
        Assert.notNull(menuId);
        JSONObject menuDataPermMap = allMenuDataPermMap.getJSONObject(menuId);
        if (menuDataPermMap == null) {
            menuDataPermMap = allMenuDataPermMap.getJSONObject(ApplicationConstant.DATA_PERM_ALL_MENU_ID);
        }
        if (menuDataPermMap == null) {
            throw new NoDataPermException(StrFormatter.format(
                    "No Related ReportPage DataPerm found for menuId [{}] and dataset [{}].",
                    menuId, datasetName));
        }
        if (BooleanUtil.isTrue(reportProperties.getEnableMenuPermVerify())) {
            String url = ContextUtil.getHttpRequest().getHeader(ApplicationConstant.HTTP_HEADER_ORIGINAL_REQUEST_URL);
            if (StrUtil.isBlank(url)) {
                url = ContextUtil.getHttpRequest().getRequestURI();
            }
            Assert.notNull(url);
            if (!this.verifyMenuPerm(menuId, url, datasetName)) {
                String msg = StrFormatter.format("Mismatched ReportPage DataPerm " +
                        "for menuId [{}] and url [{}] and SQL_ID [{}].", menuId, url, datasetName);
                throw new NoDataPermException(msg);
            }
        }
        return menuDataPermMap;
    }

    private String doProcessDataPerm(
            String datasetName, String deptFilterColumnName, String userFilterColumnName, Map<Integer, String> dataPermMap) {
        List<String> criteriaList = new LinkedList<>();
        for (Map.Entry<Integer, String> entry : dataPermMap.entrySet()) {
            String filterClause = processDataPermRule(
                    datasetName, deptFilterColumnName, userFilterColumnName, entry.getKey(), entry.getValue());
            if (StrUtil.isNotBlank(filterClause)) {
                criteriaList.add(filterClause);
            }
        }
        if (CollUtil.isEmpty(criteriaList)) {
            return null;
        }
        StringBuilder filterBuilder = new StringBuilder(128);
        filterBuilder.append("(");
        filterBuilder.append(CollUtil.join(criteriaList, " OR "));
        filterBuilder.append(")");
        return filterBuilder.toString();
    }

    private String processDataPermRule(
            String datasetName, String deptFilterColumnName, String userFilterColumnName, Integer ruleType, String dataIds) {
        TokenData tokenData = TokenData.takeFromRequest();
        StringBuilder filter = new StringBuilder(128);
        if (ruleType == DataPermRuleType.TYPE_CUSTOM_SQL) {
            return this.processCustomSqlRule(dataIds);
        }
        if (ruleType != DataPermRuleType.TYPE_USER_ONLY
                && ruleType != DataPermRuleType.TYPE_DEPT_AND_CHILD_DEPT_USERS
                && ruleType != DataPermRuleType.TYPE_DEPT_USERS) {
            return this.processDeptDataPermRule(datasetName, deptFilterColumnName, ruleType, dataIds);
        }
        if (StrUtil.isBlank(userFilterColumnName)) {
            log.warn("No UserFilterColumn for REPORT dataset [{}] but USER_FILTER_DATA_PERM exists", datasetName);
            return filter.toString();
        }
        if (ruleType == DataPermRuleType.TYPE_USER_ONLY) {
            filter.append(userFilterColumnName).append(" = ").append(tokenData.getUserId());
        } else {
            filter.append(userFilterColumnName)
                    .append(" IN (")
                    .append(dataIds)
                    .append(") ");
        }
        return filter.toString();
    }

    private String processCustomSqlRule(String customSqls) {
        if (StrUtil.isBlank(customSqls)) {
            return StrUtil.EMPTY;
        }
        List<String> customSqlList = JSON.parseArray(customSqls, String.class);
        List<String> normalizedSqlList = new LinkedList<>();
        TokenData tokenData = TokenData.takeFromRequest();
        JSONObject variableData = new JSONObject();
        variableData.put("loginUserId", tokenData.getUserId());
        variableData.put("loginDeptId", tokenData.getDeptId());
        variableData.put("loginPostIds", this.processSqlVariable(tokenData.getPostIds()));
        variableData.put("loginDeptPostIds", this.processSqlVariable(tokenData.getDeptPostIds()));
        variableData.put("loginRoleIds", this.processSqlVariable(tokenData.getRoleIds()));
        variableData.put("loginName",  "'" + tokenData.getLoginName() + "'");
        for (String customSql : customSqlList) {
            normalizedSqlList.add(MyCommonUtil.replaceAllWithVariableData(customSql, variableData));
        }
        return CollUtil.join(normalizedSqlList, " OR ");
    }

    private String processSqlVariable(String ids) {
        // 如果为空，我们使用0的默认值，其结果是为了不返回任何数据，因为不会有0的id。
        return StrUtil.isBlank(ids) ? "0" : ids;
    }

    private String processDeptDataPermRule(
            String datasetName, String deptFilterColumnName, Integer ruleType, String deptIds) {
        TokenData tokenData = TokenData.takeFromRequest();
        StringBuilder filter = new StringBuilder(128);
        if (StrUtil.isBlank(deptFilterColumnName)) {
            log.warn("No DeptFilterColumn for REPORT dataset [{}] but DEPT_FILTER_DATA_PERM exists", datasetName);
            return filter.toString();
        }
        if (ruleType == DataPermRuleType.TYPE_DEPT_ONLY) {
            filter.append(deptFilterColumnName)
                    .append(" = ")
                    .append(tokenData.getDeptId());
        } else if (ruleType == DataPermRuleType.TYPE_CUSTOM_DEPT_LIST) {
            filter.append(deptFilterColumnName)
                    .append(" IN (")
                    .append(deptIds)
                    .append(") ");
        } else {
            String sessionId = TokenData.takeFromRequest().getSessionId();
            if (ruleType == DataPermRuleType.TYPE_DEPT_AND_CHILD_DEPT) {
                deptIds = tokenData.getDeptId().toString();
            }
            String childrenDeptIds;
            String cacheKey = RedisKeyUtil.makeSessionChildrenDeptIdKey(sessionId, deptIds);
            Cache cache = cacheManager.getCache(CacheConfig.CacheEnum.CHILDREN_DEPT_ID_CACHE.name());
            if (cache == null) {
                String msg = StrFormatter.format("No cache [{}] for DataPerm verify",
                        CacheConfig.CacheEnum.CHILDREN_DEPT_ID_CACHE.name());
                throw new MyRuntimeException(msg);
            }
            Cache.ValueWrapper wrapper = cache.get(cacheKey);
            if (wrapper == null) {
                childrenDeptIds = this.getChildrenDeptId(deptIds);
                cache.put(cacheKey, childrenDeptIds);
            } else {
                Object cacheData = wrapper.get();
                if (cacheData == null) {
                    String msg = StrFormatter.format(
                            "No cache key [{}] in cache [{}] for DataPerm verify",
                            cacheKey, CacheConfig.CacheEnum.CHILDREN_DEPT_ID_CACHE.name());
                    throw new MyRuntimeException(msg);
                }
                childrenDeptIds = cacheData.toString();
            }
            filter.append(deptFilterColumnName)
                    .append("  IN (")
                    .append(childrenDeptIds)
                    .append(") ");
        }
        return filter.toString();
    }

    private String getChildrenDeptId(String deptIds) {
        StringBuilder sb = new StringBuilder(128);
        sb.append(reportProperties.getDataPermAllChildrenDeptIdUrl()).append("?parentIds=").append(deptIds);
        Map<String, String> headerMap = new HashMap<>(1);
        headerMap.put(ApplicationConstant.HTTP_HEADER_INTERNAL_TOKEN, TokenData.takeFromRequest().getToken());
        HttpResponse httpResponse = HttpUtil.createGet(sb.toString()).addHeaders(headerMap).execute();
        if (!httpResponse.isOk()) {
            String msg = StrFormatter.format(
                    "Failed to call dataPermChildrenDeptIdUrl [{}] with ERROR HTTP Status [{}].",
                    sb.toString(), httpResponse.getStatus());
            throw new MyRuntimeException(msg);
        }
        String respJson = httpResponse.body();
        ResponseResult<List<Long>> responseResult =
                JSON.parseObject(respJson, new TypeReference<ResponseResult<List<Long>>>(){});
        if (!responseResult.isSuccess()) {
            String msg = StrFormatter.format(
                    "Failed to call dataPermChildrenDeptIdUrl [{}] with ERROR [{}].",
                    sb.toString(), responseResult.getErrorMessage());
            throw new MyRuntimeException(msg);
        }
        String childrenDeptId = CollUtil.join(responseResult.getData(), ",");
        if (StrUtil.isBlank(childrenDeptId)) {
            // 如果接口返回空数据集，就用当前参数为默认值。
            log.warn("No Children deptIds Found From dataPermChildrenDeptIdUrl deptIds for [{}]", deptIds);
            childrenDeptId = StrUtil.replace(deptIds, ",", "','");
        }
        return childrenDeptId;
    }

    private Object getCachedData(String dataPermSessionKey) {
        Object cachedData;
        Cache cache = cacheManager.getCache(CacheConfig.CacheEnum.DATA_PERMISSION_CACHE.name());
        if (cache == null) {
            String msg = StrFormatter.format("No cache [{}] for DataPerm.getCachedData!",
                    CacheConfig.CacheEnum.DATA_PERMISSION_CACHE.name());
            throw new MyRuntimeException(msg);
        }
        Cache.ValueWrapper wrapper = cache.get(dataPermSessionKey);
        if (wrapper == null) {
            cachedData = redissonClient.getBucket(dataPermSessionKey).get();
            if (cachedData != null) {
                cache.put(dataPermSessionKey, JSON.parseObject(cachedData.toString()));
            }
        } else {
            cachedData = wrapper.get();
        }
        return cachedData;
    }

    @SuppressWarnings("unchecked")
    private boolean verifyMenuPerm(String menuId, String url, String datasetName) {
        String sessionId = TokenData.takeFromRequest().getSessionId();
        String menuPermSessionKey = RedisKeyUtil.makeSessionMenuPermKey(sessionId, menuId);
        Cache c = cacheManager.getCache(CacheConfig.CacheEnum.MENU_PERM_CACHE.name());
        if (c == null) {
            String msg = StrFormatter.format(
                    "No cache [{}] for DataPerm verify", CacheConfig.CacheEnum.MENU_PERM_CACHE.name());
            throw new MyRuntimeException(msg);
        }
        Cache.ValueWrapper cachedMenuPermWrapper = c.get(menuPermSessionKey);
        if (cachedMenuPermWrapper != null) {
            Object cacheData = cachedMenuPermWrapper.get();
            if (cacheData != null) {
                return ((Set<String>) cacheData).contains(url);
            }
        }
        RBucket<String> bucket = redissonClient.getBucket(menuPermSessionKey);
        if (!bucket.isExists()) {
            String msg = StrFormatter.format("No Related MenuPerm found in Redis Cache " +
                    "for menuId [{}] and Report dataset[{}] with sessionId [{}].", menuId, datasetName, sessionId);
            throw new NoDataPermException(msg);
        }
        Set<String> cachedMenuPermSet = new HashSet<>(JSONArray.parseArray(bucket.get(), String.class));
        c.put(menuPermSessionKey, cachedMenuPermSet);
        return cachedMenuPermSet.contains(url);
    }

    private DatasetFilter makeDatasetFilter(List<ReportFilterParam> filterParams, ReportDataset dataset) {
        DatasetFilter filter = new DatasetFilter();
        if (CollUtil.isNotEmpty(filterParams)) {
            for (ReportFilterParam filterParam : filterParams) {
                filter.add(this.convertToDatasetFilter(dataset, filterParam));
            }
        }
        ReportDatasetColumn logicDeleteColumn = dataset.getColumnList()
                .stream().filter(ReportDatasetColumn::getLogicDelete).findFirst().orElse(null);
        if (logicDeleteColumn != null) {
            DatasetFilter.FilterInfo logicDeleteFilter = new DatasetFilter.FilterInfo();
            logicDeleteFilter.setDatasetId(dataset.getDatasetId());
            logicDeleteFilter.setParamName(logicDeleteColumn.getColumnName());
            logicDeleteFilter.setFilterType(FieldFilterType.EQUAL);
            logicDeleteFilter.setParamValue(GlobalDeletedFlag.NORMAL);
            filter.add(logicDeleteFilter);
        }
        return filter;
    }

    private String makeGroupByClause(
            List<ViewDimensionData> dimensionDataList, Map<Long, ReportDatasetColumn> columnMap) {
        StringBuilder groupBy = new StringBuilder(128);
        for (ViewDimensionData dimensionData : dimensionDataList) {
            ReportDatasetColumn c = columnMap.get(dimensionData.getColumnId());
            groupBy.append(c.getColumnName()).append(", ");
        }
        return groupBy.substring(0, groupBy.length() - 2);
    }

    private String makeHavingClause(
            Long dblinkId, List<ViewIndexData> indexDataList, Map<Long, ReportDatasetColumn> columnMap) {
        DataSourceProvider provider = dataSourceUtil.getProvider(dblinkId);
        StringBuilder having = new StringBuilder(64);
        for (ViewIndexData indexData : indexDataList) {
            if (CollUtil.isEmpty(indexData.getFilterParams())) {
                continue;
            }
            ReportDatasetColumn c = columnMap.get(indexData.getColumnId());
            String alias;
            if (provider.havingClauseUsingAlias()) {
                alias = CalculateType.makeFieldAlias(
                        indexData.getCalculateType(), c.getColumnName(), c.getFieldName());
            } else {
                alias = CalculateType.makeFunctionField(indexData.getCalculateType(), c.getColumnName());
            }
            for (ReportFilterParam filterParam : indexData.getFilterParams()) {
                if (filterParam.getParamValue() != null) {
                    having.append(alias)
                            .append(FieldFilterType.getName(filterParam.getFilterType()))
                            .append(Convert.toNumber(filterParam.getParamValue()))
                            .append(AND);
                }
            }
        }
        return having.length() == 0 ? "" : having.substring(0, having.length() - 5);
    }

    private String makeOrderByClause(
            Long dblinkId, List<ViewOrderData> orderDataList, Map<Long, ReportDatasetColumn> columnMap) {
        if (CollUtil.isEmpty(orderDataList)) {
            return "";
        }
        DataSourceProvider provider = dataSourceUtil.getProvider(dblinkId);
        StringBuilder orderBy = new StringBuilder(128);
        for (ViewOrderData orderData : orderDataList) {
            ReportDatasetColumn c = columnMap.get(orderData.getColumnId());
            String alias = CalculateType.makeFieldAlias(
                    orderData.getCalculateType(), c.getColumnName(), c.getFieldName());
            if (provider.aliasWithQuotes()) {
                orderBy.append("\"").append(alias).append("\"");
            } else {
                orderBy.append(alias);
            }
            if (orderData.getOrderType().equals(OrderType.ASC)) {
                orderBy.append(", ");
            } else {
                orderBy.append(" DESC, ");
            }
        }
        return orderBy.substring(0, orderBy.length() - 2);
    }

    private DatasetFilter.FilterInfo convertToDatasetFilter(ReportDataset dataset, ReportFilterParam filterParam) {
        ReportDatasetColumn column = dataset.getColumnMap().get(filterParam.getParamName());
        String paramStringValue = filterParam.getParamValue();
        DatasetFilter.FilterInfo filterInfo = new DatasetFilter.FilterInfo();
        filterInfo.setDatasetId(dataset.getDatasetId());
        filterInfo.setParamName(filterParam.getParamName());
        filterInfo.setFilterType(filterParam.getFilterType());
        filterInfo.setDateRange(filterParam.getDateRange());
        // 这里需要根据字段的类型，动态转换过滤参数值，这样可以在后面的查询中，直接使用占位符变量了。
        // 使用占位符变量，不仅效率略高，最重要的是可以避免SQL注入。
        if (filterInfo.getFilterType().equals(FieldFilterType.IN)
                || filterInfo.getFilterType().equals(FieldFilterType.NOT_IN)
                || filterInfo.getFilterType().equals(FieldFilterType.BETWEEN)) {
            List<String> paramStringValueList = JSON.parseArray(paramStringValue, String.class);
            List<Serializable> paramValueList =
                    reportDatasetColumnService.convertToColumnValueList(column, paramStringValueList);
            filterInfo.setParamValueList(paramValueList);
        } else {
            Object paramValue = reportDatasetColumnService.convertToColumnValue(column, paramStringValue);
            filterInfo.setParamValue(paramValue);
        }
        filterInfo.setDateValueFilter(StrUtil.equals("Date", column.getFieldType()));
        return filterInfo;
    }
}
