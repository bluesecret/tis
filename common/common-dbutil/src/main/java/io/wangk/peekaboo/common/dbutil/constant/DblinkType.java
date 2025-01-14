package io.wangk.peekaboo.common.dbutil.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据库连接类型常量对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
public final class DblinkType {

    /**
     * MySQL。
     */
    public static final int MYSQL = 0;
    /**
     * PostgreSQL。
     */
    public static final int POSTGRESQL = 1;
    /**
     * Oracle。
     */
    public static final int ORACLE = 2;
    /**
     * Dameng。
     */
    public static final int DAMENG = 3;
    /**
     * 人大金仓。
     */
    public static final int KINGBASE = 4;
    /**
     * OpenGauss。
     */
    public static final int OPENGAUSS = 5;
    /**
     * SQLServer。
     */
    public static final int SQLSERVER = 6;
    /**
     * ClickHouse。
     */
    public static final int CLICKHOUSE = 10;
    /**
     * Doris。
     */
    public static final int DORIS = 11;

    private static final Map<Object, String> DICT_MAP = new HashMap<>(3);
    static {
        DICT_MAP.put(MYSQL, "MySQL");
        DICT_MAP.put(POSTGRESQL, "PostgreSQL");
        DICT_MAP.put(ORACLE, "Oracle");
        DICT_MAP.put(SQLSERVER, "SQLServer");
        DICT_MAP.put(DAMENG, "Dameng");
        DICT_MAP.put(KINGBASE, "人大金仓");
        DICT_MAP.put(OPENGAUSS, "OpenGauss");
        DICT_MAP.put(CLICKHOUSE, "ClickHouse");
        DICT_MAP.put(DORIS, "Doris");
    }

    /**
     * 判断参数是否为当前常量字典的合法值。
     *
     * @param value 待验证的参数值。
     * @return 合法返回true，否则false。
     */
    public static boolean isValid(Integer value) {
        return value != null && DICT_MAP.containsKey(value);
    }

    /**
     * 私有构造函数，明确标识该常量类的作用。
     */
    private DblinkType() {
    }
}
