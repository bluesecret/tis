package io.wangk.peekaboo.common.report.model.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 排序类型常量对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
public final class OrderType {

    /**
     * 无排序。
     */
    public static final int NONE = 0;
    /**
     * 升序。
     */
    public static final int ASC = 1;
    /**
     * 降序。
     */
    public static final int DESC = 2;

    private static final Map<Object, String> DICT_MAP = new HashMap<>(3);
    static {
        DICT_MAP.put(NONE, "无排序");
        DICT_MAP.put(ASC, "升序");
        DICT_MAP.put(DESC, "降序");
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
    private OrderType() {
    }
}
