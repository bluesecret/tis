package io.wangk.peekaboo.common.report.object.sheet;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.Map;

/**
 * x-spreadsheet的数据对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
public class SpreadSheetData {

    /**
     * 表单名。
     */
    private String name;
    /**
     * 样式列表。
     */
    private Style[] styles;
    /**
     * 合并单元格数据数组。
      */
    private String[] merges;
    /**
     * 行数据。
     */
    private JSONObject rows;
    /**
     * 列信息。
     */
    private JSONObject cols;

    /**
     * 行数量(解析后计算)。
     */
    private Integer rowCount;
    /**
     * 列数量(解析后计算)。
     */
    private Integer colCount;
    /**
     * 行数据对象(解析后计算)。
     */
    private Map<Integer, JSONObject> rowMap;
    /**
     * 列数据对象(解析后计算)。
     */
    private Map<Integer, JSONObject> colMap;
    /**
     * 行高度数组(解析后计算)。
     */
    private Integer[] rowHeights;
    /**
     * 列宽度数组(解析后计算)。
     */
    private Integer[] colWidths;

    @Data
    public static class Style {
        /**
         * 单元格边线对象。
         */
        private Border border;
        /**
         * 背景色。
         */
        private String bgcolor;
        /**
         * 颜色。
         */
        private String color;
        /**
         * 水平对齐。
         */
        private String align;
        /**
         * 垂直对齐。
         */
        private String valign;
        /**
         * 删除线。
         */
        private Boolean strike;
        /**
         * 是否有下划线。
         */
        private Boolean underline;
        /**
         * 格式。
         */
        private String format;
        /**
         * 字体数据。
         */
        private Font font;
    }

    @Data
    public static class Border {
        /**
         * 顶边线。
         */
        private String[] top;
        /**
         * 左边线。
         */
        private String[] left;
        /**
         * 底边线。
         */
        private String[] bottom;
        /**
         * 右边线。
         */
        private String[] right;
    }

    @Data
    public static class Font {
        /**
         * 字体名称。
         */
        private String name;
        /**
         * 是否粗体。
         */
        private Boolean bold;
        /**
         * 是否斜体。
         */
        private Boolean italic;
        /**
         * 字号。
         */
        private Integer size;
    }

    @Data
    public static class Cell {
        /**
         * 配置值。
         */
        private String v;
        /**
         * 文本。
         */
        private String text;
        /**
         * 样式对象数组中的索引位置。
         */
        private Integer style;
        /**
         * 合并单元格信息。
         */
        private Integer[] merge;
    }
}
