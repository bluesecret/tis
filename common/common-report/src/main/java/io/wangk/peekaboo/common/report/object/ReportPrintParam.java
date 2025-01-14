package io.wangk.peekaboo.common.report.object;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * 打印参数对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ReportPrintParam extends ArrayList<ReportPrintParam.FilterInfo> {

    @Data
    public static class FilterInfo {
        /**
         * 过滤参数名称。
         */
        private String paramName;
        /**
         * 过滤参数值是单值时。使用该字段值。
         */
        private String paramValue;
        /**
         * 过滤参数值是集合时，使用该字段值。
         */
        private List<String> paramValueList;
    }
}
