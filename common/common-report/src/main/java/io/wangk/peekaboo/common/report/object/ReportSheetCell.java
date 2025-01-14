package io.wangk.peekaboo.common.report.object;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ReportSheetCell {
    /**
     * 数据。
     */
    private String data;
    /**
     * 行坐标 (索引从0开始)。
     */
    private int rowIndex;
    /**
     * 列坐标 (索引从0开始)。
     */
    private int colIndex;
    /**
     * 列宽。
     */
    private int colWidth;
    /**
     * 是否为合并单元格。
     */
    private boolean mergeCell;
    /**
     * 是否为合并单元格的左上第一个cell。
     */
    private boolean mergeCellTopLeft;
    /**
     * 合并单元格的起始行坐标。
     */
    private Integer mergeCellTopRow;
    /**
     * 合并单元格的起始列坐标。
     */
    private Integer mergeCellLeftCol;
    /**
     * 合并单元格的行跨度。
     */
    private Integer mergeCellRowSpan;
    /**
     * 合并单元格的列跨度。
     */
    private Integer mergeCellColSpan;
    /**
     * 是否粗体。
     */
    private boolean bold;
    /**
     * 是否斜体。
     */
    private boolean italic;
    /**
     * 是否下划线。
     */
    private boolean underline;
    /**
     * 是否删除线。
     */
    private boolean cancelLine;
    /**
     * 垂直对齐 (0:中间, 1:上, 2:下)。
     */
    private Integer verticalType = 1;
    /**
     * 水平对齐 (0:中间, 1:左, 2:右)。
     */
    private Integer horizontalType = 1;
    /**
     * 字体颜色。
     */
    private String fontColor;
    /**
     * 字号。
     */
    private Integer fontSize = 9;
    /**
     * 字体家族。
     */
    private String fontFamily;
    /**
     * 背景色。
     */
    private String backgroundColor;
    /**
     * 是否存在顶部边框。
     */
    private boolean hasTopBorder;
    /**
     * 是否存在底部边框。
     */
    private boolean hasBottomBorder;
    /**
     * 是否存在左边框。
     */
    private boolean hasLeftBorder;
    /**
     * 是否存在右边框。
     */
    private boolean hasRightBorder;
    /**
     * 上边框颜色。
     */
    private String topBorderColor;
    /**
     * 下边框颜色。
     */
    private String bottomBorderColor;
    /**
     * 左边框颜色。
     */
    private String leftBorderColor;
    /**
     * 右边框颜色。
     */
    private String rightBorderColor;
    /**
     * 自定义格式类型。n 表示数字。
     */
    private String customFormatType;
    /**
     * 自定义格式。"0" 表示去掉小数。
     */
    private String customFormat;
    /**
     * 空单元。
     */
    private Boolean emptyCell;
    /**
     * 图片单元格。
     */
    private Boolean imageCell;
    /**
     * 样式索引。
     */
    private Integer styleIndex;
    /**
     * 是否存在边框。
     *
     * @return 存在任意边框返回true，否则false。
     */
    public boolean hasBorder() {
        return hasTopBorder || hasBottomBorder || hasLeftBorder || hasRightBorder;
    }

    public void setVerticalType(Integer verticalType) {
        this.verticalType = verticalType;
    }

    public void setHorizontalType(Integer horizontalType) {
        this.horizontalType = horizontalType;
    }

    public void setVerticalType(String align) {
        if (StrUtil.equals("top", align)) {
            this.verticalType = 1;
        } else if ("bottom".equals(align)) {
            this.verticalType = 2;
        } else {
            this.verticalType = 0;
        }
    }

    public void setHorizontalType(String align) {
        if (StrUtil.equals("left", align)) {
            this.horizontalType = 1;
        } else if ("right".equals(align)) {
            this.horizontalType = 2;
        } else {
            this.horizontalType = 0;
        }
    }

    public ReportSheetCell(ReportSheetCell cell, int startRows, int rowOffset) {
        this.rowIndex = startRows + rowOffset;
        this.mergeCell = cell.mergeCell;
        this.mergeCellTopLeft = cell.mergeCellTopLeft;
        this.mergeCellTopRow = cell.mergeCellTopRow;
        if (cell.mergeCell && cell.mergeCellTopLeft) {
            this.mergeCellTopRow = this.rowIndex;
        }
        this.mergeCellRowSpan = cell.mergeCellRowSpan;
        this.mergeCellLeftCol = cell.mergeCellLeftCol;
        this.mergeCellColSpan = cell.mergeCellColSpan;
        this.data = cell.data;
        this.hasRightBorder = cell.hasRightBorder;
        this.hasLeftBorder = cell.hasLeftBorder;
        this.hasTopBorder = cell.hasTopBorder;
        this.hasBottomBorder = cell.hasBottomBorder;
        this.topBorderColor = cell.topBorderColor;
        this.bottomBorderColor = cell.bottomBorderColor;
        this.leftBorderColor = cell.leftBorderColor;
        this.rightBorderColor = cell.rightBorderColor;
        this.colWidth = cell.colWidth;
        this.colIndex = cell.colIndex;
        this.bold = cell.bold;
        this.italic = cell.italic;
        this.underline = cell.underline;
        this.cancelLine = cell.cancelLine;
        this.backgroundColor = cell.backgroundColor;
        this.fontSize = cell.fontSize;
        this.fontColor = cell.fontColor;
        this.fontFamily = cell.fontFamily;
        this.verticalType = cell.verticalType;
        this.horizontalType = cell.horizontalType;
        this.customFormat = cell.customFormat;
    }
}
