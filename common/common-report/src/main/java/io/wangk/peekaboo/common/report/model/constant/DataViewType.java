package io.wangk.peekaboo.common.report.model.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据视图类型常量对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
public final class DataViewType {

    /**
     * 统计表格。
     */
    public static final int STATS_TABLE = 1;
    /**
     * 明细表格。
     */
    public static final int DETAIL_TABLE = 2;
    /**
     * 透视表格。
     */
    public static final int PERSP_TABLE = 3;
    /**
     * 指标卡片。
     */
    public static final int INDEX_CARD = 4;
    /**
     * 仪表盘。
     */
    public static final int INSTRUMENT_PANEL = 5;
    /**
     * 折线图。
     */
    public static final int LINE_CHART = 20;
    /**
     * 堆叠折线图。
     */
    public static final int PILE_LINE_CHART = 21;
    /**
     * 柱状图。
     */
    public static final int HISTOGRAM_CHART = 22;
    /**
     * 堆叠柱状图。
     */
    public static final int PILE_HISTOGRAM_CHART = 23;
    /**
     * 横向柱状图。
     */
    public static final int HORIZONTAL_HISTOGRAM_CHART = 24;
    /**
     * 横向堆叠柱状图。
     */
    public static final int HORIZONTAL_PILE_HISTOGRAM_CHART = 25;
    /**
     * 饼图。
     */
    public static final int PIE_CHART = 26;
    /**
     * 南丁格尔饼图。
     */
    public static final int NIGHTINGALE_PIE_CHART = 27;
    /**
     * 雷达图。
     */
    public static final int RADAR_CHART = 28;
    /**
     * 散点图。
     */
    public static final int SCATTER_PLOT_CHART = 29;
    /**
     * 漏斗图。
     */
    public static final int FUNNEL_CHART = 30;

    private static final Map<Object, String> DICT_MAP = new HashMap<>(16);
    static {
        DICT_MAP.put(STATS_TABLE, "统计表格");
        DICT_MAP.put(DETAIL_TABLE, "明细表格");
        DICT_MAP.put(PERSP_TABLE, "透视表格");
        DICT_MAP.put(INDEX_CARD, "指标卡片");
        DICT_MAP.put(INSTRUMENT_PANEL, "仪表盘");
        DICT_MAP.put(LINE_CHART, "折线图");
        DICT_MAP.put(PILE_LINE_CHART, "堆叠折线图");
        DICT_MAP.put(HISTOGRAM_CHART, "柱状图");
        DICT_MAP.put(PILE_HISTOGRAM_CHART, "堆叠柱状图");
        DICT_MAP.put(HORIZONTAL_HISTOGRAM_CHART, "横向柱状图");
        DICT_MAP.put(HORIZONTAL_PILE_HISTOGRAM_CHART, "横向堆叠柱状图");
        DICT_MAP.put(PIE_CHART, "饼图");
        DICT_MAP.put(NIGHTINGALE_PIE_CHART, "南丁格尔饼图");
        DICT_MAP.put(RADAR_CHART, "雷达图");
        DICT_MAP.put(SCATTER_PLOT_CHART, "散点图");
        DICT_MAP.put(FUNNEL_CHART, "漏斗图");
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
    private DataViewType() {
    }
}
