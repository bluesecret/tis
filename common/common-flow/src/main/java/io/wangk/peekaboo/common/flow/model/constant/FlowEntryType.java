package io.wangk.peekaboo.common.flow.model.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 流程类型。
 *
 * @author wangk
 * @date 2025-01-14
 */
public final class FlowEntryType {

    /**
     * 普通审批。
     */
    public static final int NORMAL_TYPE = 0;
    /**
     * 自动化流程。
     */
    public static final int AUTO_TYPE = 1;

    private static final Map<Object, String> DICT_MAP = new HashMap<>(2);
    static {
        DICT_MAP.put(NORMAL_TYPE, "普通审批");
        DICT_MAP.put(AUTO_TYPE, "自动化流程");
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
    private FlowEntryType() {
    }
}
