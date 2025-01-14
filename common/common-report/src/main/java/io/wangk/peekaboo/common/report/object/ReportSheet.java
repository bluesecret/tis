package io.wangk.peekaboo.common.report.object;

import lombok.Data;

import java.util.List;

@Data
public class ReportSheet {

    /**
     * 可见数据行高列表。
     */
    private Integer[] visibleDataRow;
    /**
     * 可见数据列宽列表。
     */
    private Integer[] visibleDataColumn;
    /**
     * 电子表单包含数据的最大的列坐标。
     */
    private int maxCol = 0;
    /**
     * 电子表单包含数据的最大的行坐标。
     */
    private int maxRow = 0;
    /**
     * 判断是否存在支持迭代的片段。
     */
    private boolean hasLoopFragment = false;
    /**
     * 电子表单的行数据。
     */
    private List<ReportSheetRow> rows;
}
