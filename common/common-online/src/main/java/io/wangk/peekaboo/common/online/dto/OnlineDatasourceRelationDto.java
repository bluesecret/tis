package io.wangk.peekaboo.common.online.dto;

import io.wangk.peekaboo.common.core.validator.AddGroup;
import io.wangk.peekaboo.common.core.validator.ConstDictRef;
import io.wangk.peekaboo.common.core.validator.UpdateGroup;
import io.wangk.peekaboo.common.online.model.constant.RelationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 在线表单的数据源关联Dto对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Schema(description = "在线表单的数据源关联Dto对象")
@Data
public class OnlineDatasourceRelationDto {

    /**
     * 主键Id。
     */
    @Schema(description = "主键Id")
    @NotNull(message = "数据验证失败，主键Id不能为空！", groups = {UpdateGroup.class})
    private Long relationId;

    /**
     * 关联名称。
     */
    @Schema(description = "关联名称")
    @NotBlank(message = "数据验证失败，关联名称不能为空！")
    private String relationName;

    /**
     * 变量名。
     */
    @Schema(description = "变量名")
    @NotBlank(message = "数据验证失败，变量名不能为空！")
    private String variableName;

    /**
     * 主数据源Id。
     */
    @Schema(description = "主数据源Id")
    @NotNull(message = "数据验证失败，主数据源Id不能为空！")
    private Long datasourceId;

    /**
     * 关联类型。
     */
    @Schema(description = "关联类型")
    @NotNull(message = "数据验证失败，关联类型不能为空！")
    @ConstDictRef(constDictClass = RelationType.class, message = "数据验证失败，关联类型为无效值！")
    private Integer relationType;

    /**
     * 主表关联字段Id。
     */
    @Schema(description = "主表关联字段Id")
    @NotNull(message = "数据验证失败，主表关联字段Id不能为空！")
    private Long masterColumnId;

    /**
     * 从表Id。
     */
    @Schema(description = "从表Id")
    @NotNull(message = "数据验证失败，从表Id不能为空！", groups = {UpdateGroup.class})
    private Long slaveTableId;

    /**
     * 从表名。
     */
    @Schema(description = "从表名")
    @NotBlank(message = "数据验证失败，从表名不能为空！", groups = {AddGroup.class})
    private String slaveTableName;

    /**
     * 从表关联字段Id。
     */
    @Schema(description = "从表关联字段Id")
    @NotNull(message = "数据验证失败，从表关联字段Id不能为空！", groups = {UpdateGroup.class})
    private Long slaveColumnId;

    /**
     * 从表字段名。
     */
    @Schema(description = "从表字段名")
    @NotBlank(message = "数据验证失败，从表字段名不能为空！", groups = {AddGroup.class})
    private String slaveColumnName;

    /**
     * 是否级联删除标记。
     */
    @Schema(description = "是否级联删除标记")
    @NotNull(message = "数据验证失败，是否级联删除标记不能为空！")
    private Boolean cascadeDelete;

    /**
     * 是否左连接标记。
     */
    @Schema(description = "是否左连接标记")
    @NotNull(message = "数据验证失败，是否左连接标记不能为空！")
    private Boolean leftJoin;

    /**
     * 关联过滤SQL。
     */
    @Schema(description = "关联过滤SQL。")
    private String relationFilterSql;
}
