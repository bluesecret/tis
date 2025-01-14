package io.wangk.peekaboo.common.report.model.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据集类型常量对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
public final class DatasetType {

    /**
     * 数据表。
     */
    public static final int TABLE = 1;
    /**
     * SQL语句。
     */
    public static final int SQL = 2;
    /**
     * API数据源。
     */
    public static final int API = 3;

    private static final Map<Object, String> DICT_MAP = new HashMap<>(2);
    static {
        DICT_MAP.put(TABLE, "数据表");
        DICT_MAP.put(SQL, "SQL语句");
        DICT_MAP.put(API, "API数据源");
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
    private DatasetType() {
    }
}
