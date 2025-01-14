package io.wangk.peekaboo.common.report.object;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@Data
public class ReportSheetRow {

    /**
     * 所属Fragment的Id，如果为空，则不属于任何打印片段。
     */
    private Long fragmentId;
    /**
     * 行坐标 (索引从0开始)。
     */
    private int rowIndex;
    /**
     * 行高度。
     */
    private int rowHeight;
    /**
     * 是否为空行。
     */
    private boolean emptyRow = true;
    /**
     * 单元格列表。
     */
    private List<ReportSheetCell> cells;
    /**
     * 本行数据的数据对象。
     */
    @JSONField(serialize = false)
    private Map<String, Object> dataObject;

    public ReportSheetRow(ReportSheetRow row, int startRow, int offsetRows) {
        this.rowIndex = startRow + offsetRows;
        this.dataObject = row.dataObject;
        this.emptyRow = row.emptyRow;
        this.rowHeight = row.rowHeight;
        this.fragmentId = row.fragmentId;
        if (CollUtil.isNotEmpty(row.cells)) {
            this.cells = new LinkedList<>();
            for (ReportSheetCell cell : row.cells) {
                ReportSheetCell clonedCell = new ReportSheetCell(cell, startRow, offsetRows);
                this.cells.add(clonedCell);
            }
        }
    }
}
