package io.wangk.peekaboo.common.report.object;

import io.wangk.peekaboo.common.report.vo.ReportDatasetColumnVo;
import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * 数据集配置信息对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
public class ReportDatasetInfo {

    /**
     * SQL语句。
     */
    private String sql;
    /**
     * SQL语句中所有参数名的集合。
     */
    private Set<String> allParamNameSet;
    /**
     * 参数值信息列表。
     */
    private List<SqlDatasetParam> paramList;
    /**
     * 仅api数据源使用。
     */
    private String url;
    /**
     * 仅api数据源使用。
     */
    private String method;
    /**
     * 仅api数据源使用。
     */
    private List<ReportDatasetColumnVo> columnList;

    @Data
    public static class SqlDatasetParam {
        private String paramName;
        private String paramType;
        private String defaultValue;
    }
}
