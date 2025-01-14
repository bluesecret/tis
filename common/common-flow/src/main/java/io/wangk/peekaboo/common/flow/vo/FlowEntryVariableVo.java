package io.wangk.peekaboo.common.flow.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 流程变量Vo对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Schema(description = "流程变量Vo对象")
@Data
public class FlowEntryVariableVo {

    /**
     * 主键Id。
     */
    @Schema(description = "主键Id")
    private Long variableId;

    /**
     * 流程Id。
     */
    @Schema(description = "流程Id")
    private Long entryId;

    /**
     * 变量名。
     */
    @Schema(description = "变量名")
    private String variableName;

    /**
     * 显示名。
     */
    @Schema(description = "显示名")
    private String showName;

    /**
     * 变量类型。
     */
    @Schema(description = "变量类型")
    private Integer variableType;

    /**
     * 绑定数据源Id。
     */
    @Schema(description = "绑定数据源Id")
    private Long bindDatasourceId;

    /**
     * 绑定数据源关联Id。
     */
    @Schema(description = "绑定数据源关联Id")
    private Long bindRelationId;

    /**
     * 绑定字段Id。
     */
    @Schema(description = "绑定字段Id")
    private Long bindColumnId;

    /**
     * 是否内置。
     */
    @Schema(description = "是否内置")
    private Boolean builtin;

    /**
     * 创建时间。
     */
    @Schema(description = "创建时间")
    private Date createTime;
}
