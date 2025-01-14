package io.wangk.peekaboo.common.report.object;

import lombok.Data;

/**
 * 报表过滤参数对象(前端传给后台的)。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
public class ReportFilterParam {

    /**
     * 唯一Id。
     */
    private Long paramId;
    /**
     * 是否为必填。
     */
    private Boolean required = false;
    /**
     * 如果为NULL，就是走dataset对应的表。
     */
    private Long relationId;
    /**
     * 参数名。TABLE类型的数据集是数据表的字段名。
     */
    private String paramName;
    /**
     * 过滤类型，具体值可参考FieldFilterType常量类。
     */
    private Integer filterType;
    /**
     * 过滤值的类型。参考FilterValueType常量。
     */
    private Integer filterValueType;
    /**
     * 参数值。一般用于EQUAL过滤类型
     */
    private String paramValue;
    /**
     * 日期精确到。year/month/week/day
     */
    private String dateRange;
}
