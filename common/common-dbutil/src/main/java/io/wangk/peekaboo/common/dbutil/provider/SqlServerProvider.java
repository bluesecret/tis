package io.wangk.peekaboo.common.dbutil.provider;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import io.wangk.peekaboo.common.core.constant.ObjectFieldType;
import io.wangk.peekaboo.common.dbutil.constant.DblinkType;

/**
 * SQL Server数据源的提供者实现类。
 *
 * @author wangk
 * @date 2025-01-14
 */
public class SqlServerProvider implements DataSourceProvider {

    @Override
    public int getDblinkType() {
        return DblinkType.SQLSERVER;
    }

    @Override
    public JdbcConfig getJdbcConfig(String configuration) {
        return JSON.parseObject(configuration, SqlServerConfig.class);
    }

    @Override
    public String getTableMetaListSql(String searchString) {
        StringBuilder sql = new StringBuilder();
        sql.append(this.getTableMetaListSql());
        if (StrUtil.isNotBlank(searchString)) {
            sql.append(" WHERE t.name LIKE ?");
        }
        return sql.append(" ORDER BY t.name").toString();
    }

    @Override
    public String getTableMetaSql() {
        return this.getTableMetaListSql() + " WHERE t.name = ?";
    }

    @Override
    public String getTableColumnMetaListSql() {
        return "SELECT "
                + "    a.name AS columnName, "
                + "    b.name AS columnType, "
                + "    isnull(g.[value], '') AS columnComment, "
                + "    CASE WHEN exists(SELECT 1 FROM sysobjects where xtype = 'PK' AND parent_obj = a.id AND name IN (SELECT name FROM sysindexes WHERE indid IN (SELECT indid FROM sysindexkeys WHERE id = a.id AND colid = a.colid))) THEN 1 else 0 END AS primaryKey, "
                + "    CASE WHEN a.isnullable = 1 THEN 1 ELSE 0 END AS nullable, "
                + "    a.colorder AS columnShowOrder, "
                + "    CASE WHEN COLUMNPROPERTY(a.id, a.name, 'IsIdentity') = 1 then 1 ELSE 0 END AS autoIncrement, "
                + "    a.length AS stringPrecision, "
                + "    COLUMNPROPERTY(a.id, a.name, 'PRECISION') AS numericPrecision, "
                + "    isnull(COLUMNPROPERTY(a.id, a.name, 'Scale'),0) AS numericScale, "
                + "    isnull(e.text, '') AS columnDefault "
                + "FROM syscolumns a "
                + "LEFT JOIN systypes b ON a.xusertype = b.xusertype "
                + "INNER JOIN sysobjects d ON a.id = d.id AND d.xtype = 'U' AND d.name<>'dtproperties' "
                + "LEFT JOIN syscomments e ON a.cdefault = e.id "
                + "LEFT JOIN sys.extended_properties g ON a.id = G.major_id AND a.colid = g.minor_id "
                + "LEFT JOIN sys.extended_properties f ON d.id = f.major_id AND f.minor_id = 0 "
                + "WHERE d.name = ? "
                + "ORDER BY a.id, a.colorder ";
    }

    @Override
    public String makePageSql(String sql, Integer pageNum, Integer pageSize) {
        if (pageSize == null) {
            pageSize = 10;
        }
        int offset = pageNum > 0 ? (pageNum - 1) * pageSize : 0;
        return "SELECT TOP " + pageSize + " * FROM (SELECT ROW_NUMBER() OVER(ORDER BY RAND() DESC) PAGE_ROW_NUMBER, * FROM ("
                + sql + ") AS PAGE_TABLE_ALIAS) AS PAGE_TABLE_ALIAS WHERE PAGE_ROW_NUMBER > " + offset + " ORDER BY PAGE_ROW_NUMBER";
    }

    @Override
    public String convertColumnTypeToJavaType(String columnType, Integer numericPrecision, Integer numericScale) {
        if (StrUtil.equalsAnyIgnoreCase(columnType,
                "varchar", "char", "nchar", "nvarchar", "text", "ntext")) {
            return ObjectFieldType.STRING;
        }
        if (StrUtil.equalsIgnoreCase(columnType, "bigint")) {
            return ObjectFieldType.LONG;
        }
        if (StrUtil.equalsAnyIgnoreCase(columnType, "int", "smallint", "tinyint")) {
            return ObjectFieldType.INTEGER;
        }
        if (StrUtil.equalsIgnoreCase(columnType, "bit")) {
            return ObjectFieldType.BOOLEAN;
        }
        if (StrUtil.equalsIgnoreCase(columnType, "decimal")) {
            return ObjectFieldType.BIG_DECIMAL;
        }
        if (StrUtil.equalsAnyIgnoreCase(columnType, "float")) {
            return ObjectFieldType.DOUBLE;
        }
        if ("numeric".equalsIgnoreCase(columnType)) {
            if (numericScale != null && numericScale > 0) {
                return ObjectFieldType.DOUBLE;
            }
            if (numericPrecision == null) {
                return ObjectFieldType.LONG;
            }
            return numericPrecision > 10 ? ObjectFieldType.LONG : ObjectFieldType.INTEGER;
        }
        if (StrUtil.equalsAnyIgnoreCase(columnType, "date", "datetime", "datetime2", "timestamp", "time")) {
            return ObjectFieldType.DATE;
        }
        if (StrUtil.equalsAnyIgnoreCase(columnType, "binary")) {
            return ObjectFieldType.BYTE_ARRAY;
        }
        return null;
    }

    private String getTableMetaListSql() {
        return "SELECT "
                + "    t.name tableName, "
                + "    v.tableComment "
                + "FROM "
                + "    sys.tables t "
                + "LEFT JOIN "
                + "    (SELECT t.name AS tableName, ep.value AS tableComment FROM sys.tables t, sys.extended_properties ep WHERE t.object_id = ep.major_id AND minor_id = 0) v "
                + "ON t.name = v.tableName ";
    }
}
