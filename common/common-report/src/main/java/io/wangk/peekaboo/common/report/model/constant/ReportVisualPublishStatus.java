package io.wangk.peekaboo.common.report.model.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 可视化对象发布状态常量对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
public final class ReportVisualPublishStatus {

    /**
     * 未发布。
     */
    public static final int UNPUBLISH = 0;
    /**
     * 发布。
     */
    public static final int PUBLISHED = 1;

    private static final Map<Object, String> DICT_MAP = new HashMap<>(3);
    static {
        DICT_MAP.put(UNPUBLISH, "未发布");
        DICT_MAP.put(PUBLISHED, "发布");
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
    private ReportVisualPublishStatus() {
    }
}
