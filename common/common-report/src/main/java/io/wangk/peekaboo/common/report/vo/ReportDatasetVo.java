package io.wangk.peekaboo.common.report.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 报表数据集视图对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Schema(description = "报表数据集视图对象")
@Data
public class ReportDatasetVo {

    /**
     * 主键Id。
     */
    @Schema(description = "主键Id")
    private Long datasetId;

    /**
     * 应用编码。为空时，表示非第三方应用接入。
     */
    @Schema(description = "应用编码。为空时，表示非第三方应用接入")
    private String appCode;

    /**
     * 数据集名称。
     */
    @Schema(description = "数据集名称")
    private String datasetName;

    /**
     * 分组Id。
     */
    @Schema(description = "分组Id")
    private Long groupId;

    /**
     * 数据库链接Id。
     */
    @Schema(description = "数据库链接Id")
    private Long dblinkId;

    /**
     * 数据集类型。
     */
    @Schema(description = "数据集类型")
    private Integer datasetType;

    /**
     * 数据表名。仅当集合为数据表时可用。
     */
    @Schema(description = "数据表名")
    private String tableName;

    /**
     * 表原始信息。
     */
    @Schema(description = "表原始信息")
    private String datasetInfo;

    /**
     * 创建时间。
     */
    @Schema(description = "创建时间")
    private Date createTime;

    /**
     * 创建者。
     */
    @Schema(description = "创建者")
    private Long createUserId;

    /**
     * 更新时间。
     */
    @Schema(description = "更新时间")
    private Date updateTime;

    /**
     * 更新者。
     */
    @Schema(description = "更新者")
    private Long updateUserId;

    /**
     * 数据库链接Id字典关联数据。
     */
    @Schema(description = "数据库链接Id字典关联数据")
    private Map<String, Object> dblinkIdDictMap;

    /**
     * 数据集类型常量字典关联数据。
     */
    @Schema(description = "数据集类型常量字典关联数据")
    private Map<String, Object> datasetTypeDictMap;

    /**
     * 数据列列表。
     */
    @Schema(description = "数据列列表")
    private List<ReportDatasetColumnVo> columnList;
}
