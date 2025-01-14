package io.wangk.peekaboo.common.report.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 过滤值类型。
 *
 * @author wangk
 * @date 2025-01-14
 */
public final class FilterValueType {

    /**
     * 表单参数。
     */
    public static final int FORM_PARAM = 1;
    /**
     * 组件数据。
     */
    public static final int WIDGET_DATA = 2;
    /**
     * 字典数据。
     */
    public static final int DICT_DATA = 3;
    /**
     * 字段数据。
     */
    public static final int COLUMN_DATA = 4;
    /**
     * 打印模板输入参数。
     */
    public static final int PRINT_INPUT_PARAM = 5;
    /**
     * 输入数据。
     */
    public static final int INPUT_DATA = 20;

    private static final Map<Object, String> DICT_MAP = new HashMap<>(2);
    static {
        DICT_MAP.put(FORM_PARAM, "表单参数");
        DICT_MAP.put(WIDGET_DATA, "组件数据");
        DICT_MAP.put(DICT_DATA, "字典数据");
        DICT_MAP.put(COLUMN_DATA, "字段数据");
        DICT_MAP.put(PRINT_INPUT_PARAM, "打印模板输入参数");
        DICT_MAP.put(INPUT_DATA, "输入数据");
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
    private FilterValueType() {
    }
}
