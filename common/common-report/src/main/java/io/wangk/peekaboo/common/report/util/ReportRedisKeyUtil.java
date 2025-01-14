package io.wangk.peekaboo.common.report.util;

/**
 * 统计打印模块Redis键生成工具类。
 *
 * @author wangk
 * @date 2025-01-14
 */
public class ReportRedisKeyUtil {

    /**
     * 计算报表统计页面缓存在Redis中的键值。
     *
     * @param pageCode 页面编码。
     * @return 统计页面缓存在Redis中的键值。
     */
    public static String makeReportPageKey(String pageCode) {
        return "REPORT_PAGE:" + pageCode;
    }

    /**
     * 计算报表统计数据集缓存在Redis中的键值。
     *
     * @param datasetId 数据集Id。
     * @return 统计数据集缓存在Redis中的键值。
     */
    public static String makeReportDatasetKey(Long datasetId) {
        return "REPORT_DATASET:" + datasetId;
    }

    /**
     * 计算统计打印模块中字典对象缓存在Redis中的键值。
     *
     * @param dictId 统计打印模块字典主键Id。
     * @return 统计打印模块中字典对象缓存在Redis中的键值。
     */
    public static String makeReportDictKey(Long dictId) {
        return "REPORT_DICT:" + dictId;
    }

    /**
     * 私有构造函数，明确标识该常量类的作用。
     */
    private ReportRedisKeyUtil() {
    }
}
