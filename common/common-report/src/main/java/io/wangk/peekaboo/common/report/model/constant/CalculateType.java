package io.wangk.peekaboo.common.report.model.constant;

import cn.hutool.core.util.StrUtil;
import io.wangk.peekaboo.common.core.exception.MyRuntimeException;

import java.util.HashMap;
import java.util.Map;

/**
 * 指标计算类型常量对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
public final class CalculateType {

    /**
     * 累加。
     */
    public static final int SUM = 0;
    /**
     * 计数。
     */
    public static final int COUNT = 1;
    /**
     * 均值。
     */
    public static final int AVG = 2;
    /**
     * 最小值。
     */
    public static final int MIN_BY = 3;
    /**
     * 最大值。
     */
    public static final int MAX_BY = 4;
    /**
     * 标准差。
     */
    public static final int STD_DEV = 5;
    /**
     * 方差。
     */
    public static final int VAR_POP = 6;

    private static final Map<Object, String> DICT_MAP = new HashMap<>(7);

    static {
        DICT_MAP.put(SUM, "累加");
        DICT_MAP.put(COUNT, "计数");
        DICT_MAP.put(AVG, "均值");
        DICT_MAP.put(MIN_BY, "最小值");
        DICT_MAP.put(MAX_BY, "最大值");
        DICT_MAP.put(STD_DEV, "标准差");
        DICT_MAP.put(VAR_POP, "方差");
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

    public static String makeSelectField(Integer calculateType, String columnName, String fieldName) {
        if (calculateType == null) {
            return columnName;
        }
        String name = StrUtil.upperFirst(fieldName);
        StringBuilder s = new StringBuilder(128);
        switch (calculateType) {
            case SUM:
                s.append("SUM(").append(columnName).append(") \"sumOf").append(name).append("\"");
                break;
            case COUNT:
                s.append("COUNT(").append(columnName).append(") \"countOf").append(name).append("\"");
                break;
            case AVG:
                s.append("AVG(").append(columnName).append(") \"avgOf").append(name).append("\"");
                break;
            case MIN_BY:
                s.append("MIN(").append(columnName).append(") \"minOf").append(name).append("\"");
                break;
            case MAX_BY:
                s.append("MAX(").append(columnName).append(") \"maxOf").append(name).append("\"");
                break;
            case STD_DEV:
                s.append("STDDEV(").append(columnName).append(") \"stddevOf").append(name).append("\"");
                break;
            case VAR_POP:
                s.append("VAR_POP(").append(columnName).append(") \"varpopOf").append(name).append("\"");
                break;
            default:
                break;
        }
        return s.toString();
    }

    public static String makeFieldAlias(Integer calculateType, String columnName, String fieldName) {
        if (calculateType == null) {
            return columnName;
        }
        String name = StrUtil.upperFirst(fieldName);
        StringBuilder s = new StringBuilder(64);
        switch (calculateType) {
            case SUM:
                return s.append("sumOf").append(name).toString();
            case COUNT:
                return s.append("countOf").append(name).toString();
            case AVG:
                return s.append("avgOf").append(name).toString();
            case MIN_BY:
                return s.append("minOf").append(name).toString();
            case MAX_BY:
                return s.append("maxOf").append(name).toString();
            case STD_DEV:
                return s.append("stddevOf").append(name).toString();
            case VAR_POP:
                return s.append("varpopOf").append(name).toString();
            default:
                break;
        }
        throw new MyRuntimeException("Unsuported CalculateType [" + calculateType + "].");
    }

    public static String makeFunctionField(Integer calculateType, String columnName) {
        if (calculateType == null) {
            return columnName;
        }
        StringBuilder s = new StringBuilder(64);
        switch (calculateType) {
            case SUM:
                s.append("SUM(").append(columnName).append(")");
                break;
            case COUNT:
                s.append("COUNT(").append(columnName).append(")");
                break;
            case AVG:
                s.append("AVG(").append(columnName).append(")");
                break;
            case MIN_BY:
                s.append("MIN(").append(columnName).append(")");
                break;
            case MAX_BY:
                s.append("MAX(").append(columnName).append(")");
                break;
            case STD_DEV:
                s.append("STDDEV(").append(columnName).append(")");
                break;
            case VAR_POP:
                s.append("VAR_POP(").append(columnName).append(")");
                break;
            default:
                break;
        }
        return s.toString();
    }

    /**
     * 私有构造函数，明确标识该常量类的作用。
     */
    private CalculateType() {
    }
}
