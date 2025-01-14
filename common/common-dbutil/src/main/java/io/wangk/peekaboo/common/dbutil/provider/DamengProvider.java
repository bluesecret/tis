package io.wangk.peekaboo.common.dbutil.provider;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import io.wangk.peekaboo.common.core.constant.ObjectFieldType;
import io.wangk.peekaboo.common.dbutil.constant.DblinkType;

/**
 * 达梦数据源的提供者实现类。
 *
 * @author wangk
 * @date 2025-01-14
 */
public class DamengProvider extends OracleProvider {

    @Override
    public int getDblinkType() {
        return DblinkType.DAMENG;
    }

    @Override
    public JdbcConfig getJdbcConfig(String configuration) {
        return JSON.parseObject(configuration, DamengConfig.class);
    }

    @Override
    public String convertColumnTypeToJavaType(String columnType, Integer numericPrecision, Integer numericScale) {
        if (StrUtil.equalsAnyIgnoreCase(columnType,
                "VARCHAR", "LONGVARCHAR", "VARCHAR2", "CHAR", "TEXT", "CLOB")) {
            return ObjectFieldType.STRING;
        }
        if (StrUtil.equalsAnyIgnoreCase(columnType, "REAL", "FLOAT", "DOUBLE")) {
            return ObjectFieldType.DOUBLE;
        }
        if (StrUtil.equalsAnyIgnoreCase(columnType, "DATE", "DATETIME", "TIMESTAMP", "TIME")) {
            return ObjectFieldType.DATE;
        }
        if (StrUtil.equalsAnyIgnoreCase(columnType, "INT", "SMALLINT", "TINYINT")) {
            return ObjectFieldType.INTEGER;
        }
        if (StrUtil.equalsIgnoreCase(columnType, "BIGINT")) {
            return ObjectFieldType.LONG;
        }
        if (StrUtil.equalsAnyIgnoreCase(columnType, "DECIMAL", "DEC")) {
            return ObjectFieldType.BIG_DECIMAL;
        }
        if (StrUtil.equalsIgnoreCase(columnType, "BLOB")) {
            return ObjectFieldType.BYTE_ARRAY;
        }
        if (StrUtil.equalsIgnoreCase(columnType, "BIT")) {
            return ObjectFieldType.BOOLEAN;
        }
        if (!"NUMBER".equalsIgnoreCase(columnType)) {
            return null;
        }
        if (numericScale != null && numericScale > 0) {
            return ObjectFieldType.DOUBLE;
        }
        if (numericPrecision == null) {
            return ObjectFieldType.LONG;
        }
        return numericPrecision > 10 ? ObjectFieldType.LONG : ObjectFieldType.INTEGER;
    }

    @Override
    public String makePageSql(String sql, Integer pageNum, Integer pageSize) {
        if (pageSize == null) {
            pageSize = 10;
        }
        int offset = pageNum > 0 ? (pageNum - 1) * pageSize : 0;
        return sql + " LIMIT " + offset + "," + pageSize;
    }
}
