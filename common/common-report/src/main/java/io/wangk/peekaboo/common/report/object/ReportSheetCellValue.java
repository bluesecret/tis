package io.wangk.peekaboo.common.report.object;

import lombok.Data;

@Data
public class ReportSheetCellValue {

    public static final int DATASET_FIELD = 1;
    public static final int IMAGE_CELL = 2;
    public static final int BARCODE_CELL = 3;
    public static final int QRCODE_CELL = 4;

    public static final int IMAGE_SOURCE_DATASET_FIELD = 1;
    /**
     * 单元格类型。
     */
    private Integer cellType;
    /**
     * 数据值。
     */
    private Object value;
    /**
     * 数据集Id。
     */
    private Long datasetId;
    /**
     * 关联Id。
     */
    private Long relationId;
    /**
     * 数据集中的字段名。
     */
    private String columnName;
    /**
     * 字段类型。
     */
    private String fieldType;
    /**
     * 图片来源类型。
     */
    private Integer imageSourceType;
    /**
     * 条形码编码类型。
     */
    private String barCodeType;
    /**
     * 条形码是否显示文字。
     */
    private Boolean barCodeShowWords;
    /**
     * 图片是否伸缩以适应单元格的尺寸显示。
     */
    private Boolean imageScale = true;
    /**
     * 数据集类型。
     */
    private Integer datasetType;
    /**
     * 日期字段显示格式。具体值可参考DateShowFormat常量类。
     */
    private Integer dateFormat;
    /**
     * 自定义格式。
     */
    private String customFormat;
}
