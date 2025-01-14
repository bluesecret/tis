package io.wangk.peekaboo.common.flow.model.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 流程变量类型。
 *
 * @author wangk
 * @date 2025-01-14
 */
public final class FlowVariableType {

    /**
     * 流程实例变量。
     */
    public static final int INSTANCE = 0;
    /**
     * 任务变量。
     */
    public static final int TASK = 1;
    /**
     * 系统内置变量。
     */
    public static final int SYSTEM = 2;
    /**
     * 输入变量。
     */
    public static final int INPUT = 4;

    private static final Map<Object, String> DICT_MAP = new HashMap<>(2);
    static {
        DICT_MAP.put(INSTANCE, "流程实例变量");
        DICT_MAP.put(TASK, "任务变量");
        DICT_MAP.put(SYSTEM, "系统内置变量");
        DICT_MAP.put(INPUT, "输入变量");
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
    private FlowVariableType() {
    }
}
