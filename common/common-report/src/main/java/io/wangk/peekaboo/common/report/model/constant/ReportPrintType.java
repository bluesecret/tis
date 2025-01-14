package io.wangk.peekaboo.common.report.model.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 报表打印模板类型的常量对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
public final class ReportPrintType {

    /**
     * EXCEL。
     */
    public static final int EXCEL = 1;
    /**
     * WORD。
     */
    public static final int WORD = 2;

    private static final Map<Object, String> DICT_MAP = new HashMap<>(4);
    static {
        DICT_MAP.put(EXCEL, "Excel模板");
        DICT_MAP.put(WORD, "Word模板");
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
    private ReportPrintType() {
    }
}
