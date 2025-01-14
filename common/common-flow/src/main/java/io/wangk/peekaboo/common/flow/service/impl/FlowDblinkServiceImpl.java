package io.wangk.peekaboo.common.flow.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.Page;
import io.wangk.peekaboo.common.core.annotation.MyDataSourceResolver;
import io.wangk.peekaboo.common.core.base.dao.BaseDaoMapper;
import io.wangk.peekaboo.common.core.base.service.BaseService;
import io.wangk.peekaboo.common.core.cache.CacheConfig;
import io.wangk.peekaboo.common.core.constant.ApplicationConstant;
import io.wangk.peekaboo.common.core.object.MyRelationParam;
import io.wangk.peekaboo.common.core.object.TokenData;
import io.wangk.peekaboo.common.core.util.DefaultDataSourceResolver;
import io.wangk.peekaboo.common.dbutil.constant.DblinkType;
import io.wangk.peekaboo.common.dbutil.object.SqlTable;
import io.wangk.peekaboo.common.dbutil.object.SqlTableColumn;
import io.wangk.peekaboo.common.flow.dao.FlowDblinkMapper;
import io.wangk.peekaboo.common.flow.model.FlowDblink;
import io.wangk.peekaboo.common.flow.service.FlowDblinkService;
import io.wangk.peekaboo.common.flow.util.FlowDataSourceUtil;
import io.wangk.peekaboo.common.sequence.wrapper.IdGeneratorWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import jakarta.annotation.Resource;
import java.util.Date;
import java.util.List;

@Slf4j
@MyDataSourceResolver(
        resolver = DefaultDataSourceResolver.class,
        intArg = ApplicationConstant.COMMON_FLOW_AND_ONLINE_DATASOURCE_TYPE)
@Service("flowDblinkService")
public class FlowDblinkServiceImpl extends BaseService<FlowDblink, Long> implements FlowDblinkService {

    @Autowired
    private FlowDblinkMapper flowDblinkMapper;
    @Autowired
    private IdGeneratorWrapper idGenerator;
    @Autowired
    private FlowDataSourceUtil dataSourceUtil;
    @Resource(name = "caffeineCacheManager")
    private CacheManager cacheManager;

    /**
     * 返回当前Service的主表Mapper对象。
     *
     * @return 主表Mapper对象。
     */
    @Override
    protected BaseDaoMapper<FlowDblink> mapper() {
        return flowDblinkMapper;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public FlowDblink saveNew(FlowDblink flowDblink) {
        flowDblinkMapper.insert(this.buildDefaultValue(flowDblink));
        return flowDblink;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean update(FlowDblink flowDblink, FlowDblink originalFlowDblink) {
        if (!StrUtil.equals(flowDblink.getConfiguration(), originalFlowDblink.getConfiguration())) {
            dataSourceUtil.removeDataSource(flowDblink.getDblinkId());
        }
        flowDblink.setAppCode(TokenData.takeFromRequest().getAppCode());
        flowDblink.setCreateUserId(originalFlowDblink.getCreateUserId());
        flowDblink.setUpdateUserId(TokenData.takeFromRequest().getUserId());
        flowDblink.setCreateTime(originalFlowDblink.getCreateTime());
        flowDblink.setUpdateTime(new Date());
        // 这里重点提示，在执行主表数据更新之前，如果有哪些字段不支持修改操作，请用原有数据对象字段替换当前数据字段。
        UpdateWrapper<FlowDblink> uw = this.createUpdateQueryForNullValue(flowDblink, flowDblink.getDblinkId());
        return flowDblinkMapper.update(flowDblink, uw) == 1;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean remove(Long dblinkId) {
        dataSourceUtil.removeDataSource(dblinkId);
        return flowDblinkMapper.deleteById(dblinkId) == 1;
    }

    @Override
    public List<FlowDblink> getFlowDblinkList(FlowDblink filter, String orderBy) {
        if (filter == null) {
            filter = new FlowDblink();
        }
        filter.setAppCode(TokenData.takeFromRequest().getAppCode());
        return flowDblinkMapper.getFlowDblinkList(filter, orderBy);
    }

    @Override
    public List<FlowDblink> getFlowDblinkListWithRelation(FlowDblink filter, String orderBy) {
        List<FlowDblink> resultList = this.getFlowDblinkList(filter, orderBy);
        // 在缺省生成的代码中，如果查询结果resultList不是Page对象，说明没有分页，那么就很可能是数据导出接口调用了当前方法。
        // 为了避免一次性的大量数据关联，规避因此而造成的系统运行性能冲击，这里手动进行了分批次读取，开发者可按需修改该值。
        int batchSize = resultList instanceof Page ? 0 : 1000;
        this.buildRelationForDataList(resultList, MyRelationParam.normal(), batchSize);
        return resultList;
    }

    @Override
    public List<SqlTable> getDblinkTableList(FlowDblink dblink) {
        List<SqlTable> resultList = dataSourceUtil.getTableList(dblink.getDblinkId(), null);
        resultList.forEach(t -> t.setDblinkId(dblink.getDblinkId()));
        return resultList;
    }

    @Override
    public SqlTable getDblinkTable(FlowDblink dblink, String tableName) {
        if (dblink.getDblinkId() == null || StrUtil.isBlank(tableName)) {
            return null;
        }
        Cache cache = cacheManager.getCache(CacheConfig.CacheEnum.AUTO_FLOW_TABLE_CACHE.name());
        Assert.notNull(cache, "cache can't be null.");
        String key = dblink.getDblinkId() + "_" + tableName;
        Cache.ValueWrapper wrapper = cache.get(key);
        SqlTable sqlTable;
        if (wrapper == null) {
            sqlTable = dataSourceUtil.getTable(dblink.getDblinkId(), tableName);
            sqlTable.setDblinkId(dblink.getDblinkId());
            sqlTable.setColumnList(getDblinkTableColumnList(dblink, tableName));
            cache.put(key, sqlTable);
        } else {
            sqlTable = (SqlTable) wrapper.get();
        }
        return sqlTable;
    }

    @Override
    public List<SqlTableColumn> getDblinkTableColumnList(FlowDblink dblink, String tableName) {
        List<SqlTableColumn> columnList = dataSourceUtil.getTableColumnList(dblink.getDblinkId(), tableName);
        columnList.forEach(c -> this.makeupSqlTableColumn(c, dblink.getDblinkType()));
        return columnList;
    }

    private void makeupSqlTableColumn(SqlTableColumn sqlTableColumn, int dblinkType) {
        sqlTableColumn.setDblinkType(dblinkType);
        switch (dblinkType) {
            case DblinkType.POSTGRESQL:
            case DblinkType.OPENGAUSS:
                if (StrUtil.equalsAny(sqlTableColumn.getColumnType(), "char", "varchar")) {
                    sqlTableColumn.setFullColumnType(
                            sqlTableColumn.getColumnType() + "(" + sqlTableColumn.getStringPrecision() + ")");
                } else {
                    sqlTableColumn.setFullColumnType(sqlTableColumn.getColumnType());
                }
                break;
            case DblinkType.MYSQL:
                sqlTableColumn.setAutoIncrement("auto_increment".equals(sqlTableColumn.getExtra()));
                break;
            case DblinkType.ORACLE:
            case DblinkType.SQLSERVER:
                if (StrUtil.equalsAnyIgnoreCase(sqlTableColumn.getColumnType(),
                        "VARCHAR", "NVARCHAR", "VARCHAR2", "NVARCHAR2", "CHAR", "NCHAR")) {
                    sqlTableColumn.setFullColumnType(
                            sqlTableColumn.getColumnType() + "(" + sqlTableColumn.getStringPrecision() + ")");
                } else if (StrUtil.equalsAnyIgnoreCase(sqlTableColumn.getColumnType(), "NUMBER", "NUMERIC")) {
                    sqlTableColumn.setFullColumnType(sqlTableColumn.getColumnType() +
                            "(" + sqlTableColumn.getNumericPrecision() + "," + sqlTableColumn.getNumericScale() + ")");
                } else {
                    sqlTableColumn.setFullColumnType(sqlTableColumn.getColumnType());
                }
                break;
            case DblinkType.DAMENG:
            case DblinkType.KINGBASE:
                if (StrUtil.equalsAnyIgnoreCase(sqlTableColumn.getColumnType(), "VARCHAR", "VARCHAR2", "CHAR")) {
                    sqlTableColumn.setFullColumnType(
                            sqlTableColumn.getColumnType() + "(" + sqlTableColumn.getStringPrecision() + ")");
                } else if (StrUtil.equals(sqlTableColumn.getColumnType(), "NUMBER")) {
                    sqlTableColumn.setFullColumnType(sqlTableColumn.getColumnType() +
                            "(" + sqlTableColumn.getNumericPrecision() + "," + sqlTableColumn.getNumericScale() + ")");
                } else {
                    sqlTableColumn.setFullColumnType(sqlTableColumn.getColumnType());
                }
                break;
            default:
                break;
        }
    }

    private FlowDblink buildDefaultValue(FlowDblink flowDblink) {
        flowDblink.setDblinkId(idGenerator.nextLongId());
        TokenData tokenData = TokenData.takeFromRequest();
        flowDblink.setCreateUserId(tokenData.getUserId());
        flowDblink.setUpdateUserId(tokenData.getUserId());
        Date now = new Date();
        flowDblink.setCreateTime(now);
        flowDblink.setUpdateTime(now);
        flowDblink.setAppCode(tokenData.getAppCode());
        return flowDblink;
    }
}
