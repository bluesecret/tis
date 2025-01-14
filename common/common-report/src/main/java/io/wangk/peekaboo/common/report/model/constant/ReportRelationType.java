package io.wangk.peekaboo.common.report.model.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 报表关联类型常量对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
public final class ReportRelationType {

    /**
     * 一对一。
     */
    public static final int ONE_TO_ONE = 0;
    /**
     * 一对多。
     */
    public static final int ONE_TO_MANY = 1;

    private static final Map<Object, String> DICT_MAP = new HashMap<>(2);
    static {
        DICT_MAP.put(ONE_TO_ONE, "一对一");
        DICT_MAP.put(ONE_TO_MANY, "一对多");
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
    private ReportRelationType() {
    }
}
