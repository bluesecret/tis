package io.wangk.peekaboo.common.report.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 * 报表数据集关联视图对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Schema(description = "报表数据集关联视图对象")
@Data
public class ReportDatasetRelationVo {

    /**
     * 主键Id。
     */
    @Schema(description = "主键Id")
    private Long relationId;

    /**
     * 应用编码。为空时，表示非第三方应用接入。
     */
    @Schema(description = "应用编码。为空时，表示非第三方应用接入")
    private String appCode;

    /**
     * 变量名。
     */
    @Schema(description = "变量名")
    private String variableName;

    /**
     * 主表数据集Id。
     */
    @Schema(description = "主表数据集Id")
    private Long masterDatasetId;

    /**
     * 主表关联字段Id。
     */
    @Schema(description = "主表关联字段Id")
    private Long masterColumnId;

    /**
     * 从表数据集Id。
     */
    @Schema(description = "从表数据集Id")
    private Long slaveDatasetId;

    /**
     * 从表关联字段Id。
     */
    @Schema(description = "从表关联字段Id")
    private Long slaveColumnId;

    /**
     * 关联类型 (0:一对一 1:一对多)。
     */
    @Schema(description = "关联类型")
    private Integer relationType;

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
     * 主表数据集Id字典关联数据。
     */
    @Schema(description = "主表数据集Id字典关联数据")
    private Map<String, Object> masterDatasetIdDictMap;

    /**
     * 主表关联字段Id字典关联数据。
     */
    @Schema(description = "主表关联字段Id字典关联数据")
    private Map<String, Object> masterColumnIdDictMap;

    /**
     * 从表数据集Id字典关联数据。
     */
    @Schema(description = "从表数据集Id字典关联数据")
    private Map<String, Object> slaveDatasetIdDictMap;

    /**
     * 从表关联字段Id字典关联数据。
     */
    @Schema(description = "从表关联字段Id字典关联数据")
    private Map<String, Object> slaveColumnIdDictMap;

    /**
     * 关联类型常量字典关联数据。
     */
    @Schema(description = "关联类型常量字典关联数据")
    private Map<String, Object> relationTypeDictMap;
}
