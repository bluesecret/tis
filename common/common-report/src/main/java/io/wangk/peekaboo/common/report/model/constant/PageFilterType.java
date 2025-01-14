package io.wangk.peekaboo.common.report.model.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 页面过滤类型常量对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
public final class PageFilterType {

    /**
     * 页面参数。
     */
    public static final int PAGE_PARAM = 1;
    /**
     * 视图参数。
     */
    public static final int VIEW_PARAM = 2;

    private static final Map<Object, String> DICT_MAP = new HashMap<>(2);
    static {
        DICT_MAP.put(PAGE_PARAM, "页面参数");
        DICT_MAP.put(VIEW_PARAM, "视图参数");
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
    private PageFilterType() {
    }
}
