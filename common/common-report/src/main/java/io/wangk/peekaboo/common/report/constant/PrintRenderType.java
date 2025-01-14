package io.wangk.peekaboo.common.report.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 打印渲染类型。
 *
 * @author wangk
 * @date 2025-01-14
 */
public final class PrintRenderType {

    /**
     * PDF
     */
    public static final int PDF = 1;
    /**
     * Excel
     */
    public static final int EXCEL = 2;
    /**
     * HTML
     */
    public static final int HTML = 3;
    /**
     * Word
     */
    public static final int WORD = 4;

    private static final Map<Object, String> DICT_MAP = new HashMap<>(2);
    static {
        DICT_MAP.put(PDF, "Pdf");
        DICT_MAP.put(EXCEL, "Excel");
        DICT_MAP.put(HTML, "Html");
        DICT_MAP.put(WORD, "Word");
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
    private PrintRenderType() {
    }
}
