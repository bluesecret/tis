package io.wangk.peekaboo.common.report.object;

import io.wangk.peekaboo.common.core.object.MyOrderParam;
import io.wangk.peekaboo.common.report.model.ReportDataset;
import io.wangk.peekaboo.common.report.model.ReportDatasetRelation;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 打印模板中Fragment的配置。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
public class ReportPrintFragment {

    /**
     * 仅前端使用。
     */
    private Long fragmentId;
    /**
     * 显示名。
     */
    private String showName;
    /**
     * 变量名。
     */
    private String variableName;
    /**
     * 起始行坐标。
     */
    private Integer startRow;
    /**
     * 结束行坐标(后台自己计算并使用)。
     */
    private Integer endRow;
    /**
     * 行跨度。
     */
    private Integer rowSpan;
    /**
     * 是否循环。
     */
    private Boolean loop = false;
    /**
     * 关联的数据集Id。
     */
    private Long datasetId;
    /**
     * 数据集的一对多关联Id。如果该值为NULL，就基于datasetId过滤。
     */
    private Long relationId;
    /**
     * 过滤参数集合。
     */
    private List<ReportFilterParam> filterParams;
    /**
     * 主表结果集的参数集合(SQL结果集或API等其他有参数的结果集)。
     */
    private List<ReportFilterParam> datasetFilterParams;
    /**
     * 排序参数。
     */
    private MyOrderParam orderParam;
    /**
     * 关联的数据集。
     * 运行时动态计算，不会存入到数据表中。
     */
    private ReportDataset reportDataset;
    /**
     * 关联的数据集关联Map。key是ReportDatasetRelation的variableName。
     * 运行时动态计算，不会存入到数据表中。
     */
    private Map<String, ReportDatasetRelation> relationMap;
    /**
     * 通过orderParam字段计算而得。
     * 运行时动态计算，不会存入到数据表中。
     */
    private String orderBy;
    /**
     * 该字段仅在API数据源的循环打印中使用。
     */
    private List<String> bindColumnPath;
    /**
     * 查询出的数据集结果。
     */
    private List<Map<String, Object>> data;
    /**
     * 数据集字段信息列表，目前仅用于word模板。
     */
    private List<DatasetColumnInfo> datasetColumnInfo;

    public static final int IMAGE_DATA = 2;
    public static final int BARCODE_DATA = 3;
    public static final int QRCODE_DATA = 4;

    @Data
    public static class DatasetColumnInfo {
        /**
         * 字段名。
         */
        private String columnName;
        /**
         * 数据类型，参考上面的常量值。
         */
        private Integer dataType;
        /**
         * 日期格式的类型。具体值可参考DateShowFormat常量类。
         */
        private Integer dateFormat;
        /**
         * 二维码是否显示下面的文字。
         */
        private Boolean barCodeShowWords = false;
        /**
         * 二维码类型。
         */
        private String barCodeType;
        /**
         * 是否图片自适应宽高，仅当图片位于表格的单元格内时候，才可以设置该值为true。
         */
        private Boolean autoSize = false;
        /**
         * 图片宽度。仅当autoSize为false时使用该值。
         */
        private Integer imageWidth;
        /**
         * 图片高度。仅当autoSize为false时使用该值。
         */
        private Integer imageHeight;
    }
}
