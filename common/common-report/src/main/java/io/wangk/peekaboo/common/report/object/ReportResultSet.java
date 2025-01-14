package io.wangk.peekaboo.common.report.object;

import cn.hutool.core.collection.CollUtil;
import io.wangk.peekaboo.common.dbutil.object.GenericResultSet;
import io.wangk.peekaboo.common.dbutil.object.SqlResultSet;
import io.wangk.peekaboo.common.report.model.ReportDatasetColumn;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 查询结果集对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
public class ReportResultSet<T> extends GenericResultSet<ReportDatasetColumn, T> {

    public ReportResultSet(List<ReportDatasetColumn> columnMetaList, List<T> dataList) {
        super(columnMetaList, dataList);
    }

    public static <D> ReportResultSet<D> fromSqlResultSet(
            SqlResultSet<D> source, List<ReportDatasetColumn> columnList) {
        ReportResultSet<D> resultSet = new ReportResultSet<>();
        if (source != null) {
            resultSet.setDataList(source.getDataList());
            resultSet.setTotalCount(source.getTotalCount());
            resultSet.setColumnMetaList(columnList);
        }
        return resultSet;
    }

    public static <D> boolean isEmpty(ReportResultSet<D> rs) {
        return rs == null || CollUtil.isEmpty(rs.getDataList());
    }
}
