package io.wangk.peekaboo.common.online.vo;

import io.wangk.peekaboo.common.core.constant.AggregationType;
import io.wangk.peekaboo.common.core.validator.ConstDictRef;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 在线数据表虚拟字段VO对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Schema(description = "在线数据表虚拟字段VO对象")
@Data
public class OnlineVirtualColumnVo {

    /**
     * 主键Id。
     */
    @Schema(description = "主键Id")
    private Long virtualColumnId;

    /**
     * 所在表Id。
     */
    @Schema(description = "所在表Id")
    private Long tableId;

    /**
     * 字段名称。
     */
    @Schema(description = "字段名称")
    private String objectFieldName;

    /**
     * 属性类型。
     */
    @Schema(description = "属性类型")
    private String objectFieldType;

    /**
     * 字段提示名。
     */
    @Schema(description = "字段提示名")
    private String columnPrompt;

    /**
     * 虚拟字段类型(0: 聚合)。
     */
    @Schema(description = "虚拟字段类型(0: 聚合)")
    private Integer virtualType;

    /**
     * 关联数据源Id。
     */
    @Schema(description = "关联数据源Id")
    private Long datasourceId;

    /**
     * 关联Id。
     */
    @Schema(description = "关联Id")
    private Long relationId;

    /**
     * 聚合字段所在关联表Id。
     */
    @Schema(description = "聚合字段所在关联表Id")
    private Long aggregationTableId;

    /**
     * 关联表聚合字段Id。
     */
    @Schema(description = "关联表聚合字段Id")
    private Long aggregationColumnId;

    /**
     * 聚合类型(0: count 1: sum 2: avg 3: max 4:min 10:concat)。
     */
    @ConstDictRef(constDictClass = AggregationType.class, message = "数据验证失败，聚合类型字段值不存在！")
    @Schema(description = "聚合类型(0: count 1: sum 2: avg 3: max 4:min)")
    private Integer aggregationType;

    /**
     * 存储过滤条件的json。
     */
    @Schema(description = "存储过滤条件的json")
    private String whereClauseJson;
}
