package io.wangk.peekaboo.common.report.model.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据集字段类别常量对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
public final class ReportFieldKind {

    /**
     * 数据表字段。
     */
    public static final int NORMAL = 1;
    /**
     * 函数字段。
     */
    public static final int FUNCTION = 2;

    private static final Map<Object, String> DICT_MAP = new HashMap<>(2);
    static {
        DICT_MAP.put(NORMAL, "数据表字段");
        DICT_MAP.put(FUNCTION, "函数字段");
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
    private ReportFieldKind() {
    }
}
