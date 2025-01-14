package io.wangk.peekaboo.common.report.model.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 水平位置常量对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
public final class HorizontalPosition {

    /**
     * 左。
     */
    public static final int LEFT = 0;
    /**
     * 中。
     */
    public static final int CENTER = 1;
    /**
     * 右。
     */
    public static final int RIGHT = 2;

    private static final Map<Object, String> DICT_MAP = new HashMap<>(3);
    static {
        DICT_MAP.put(LEFT, "左");
        DICT_MAP.put(CENTER, "中");
        DICT_MAP.put(RIGHT, "右");
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
    private HorizontalPosition() {
    }
}
