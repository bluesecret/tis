package io.wangk.peekaboo.common.online.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 * 在线表单的数据源关联VO对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Schema(description = "在线表单的数据源关联VO对象")
@Data
public class OnlineDatasourceRelationVo {

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
     * 关联名称。
     */
    @Schema(description = "关联名称")
    private String relationName;

    /**
     * 变量名。
     */
    @Schema(description = "变量名")
    private String variableName;

    /**
     * 主数据源Id。
     */
    @Schema(description = "主数据源Id")
    private Long datasourceId;

    /**
     * 关联类型。
     */
    @Schema(description = "关联类型")
    private Integer relationType;

    /**
     * 主表关联字段Id。
     */
    @Schema(description = "主表关联字段Id")
    private Long masterColumnId;

    /**
     * 从表Id。
     */
    @Schema(description = "从表Id")
    private Long slaveTableId;

    /**
     * 从表关联字段Id。
     */
    @Schema(description = "从表关联字段Id")
    private Long slaveColumnId;

    /**
     * 删除主表的时候是否级联删除一对一和一对多的从表数据，多对多只是删除关联，不受到这个标记的影响。。
     */
    @Schema(description = "一对多从表级联删除标记")
    private Boolean cascadeDelete;

    /**
     * 是否左连接。
     */
    @Schema(description = "是否左连接")
    private Boolean leftJoin;

    /**
     * 关联过滤SQL。
     */
    @Schema(description = "关联过滤SQL。")
    private String relationFilterSql;

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
     * masterColumnId 的一对一关联数据对象，数据对应类型为OnlineColumnVo。
     */
    @Schema(description = "masterColumnId字段的一对一关联数据对象")
    private Map<String, Object> masterColumn;

    /**
     * slaveTableId 的一对一关联数据对象，数据对应类型为OnlineTableVo。
     */
    @Schema(description = "slaveTableId字段的一对一关联数据对象")
    private Map<String, Object> slaveTable;

    /**
     * slaveColumnId 的一对一关联数据对象，数据对应类型为OnlineColumnVo。
     */
    @Schema(description = "slaveColumnId字段的一对一关联数据对象")
    private Map<String, Object> slaveColumn;

    /**
     * masterColumnId 字典关联数据。
     */
    @Schema(description = "masterColumnId的字典关联数据")
    private Map<String, Object> masterColumnIdDictMap;

    /**
     * slaveTableId 字典关联数据。
     */
    @Schema(description = "slaveTableId的字典关联数据")
    private Map<String, Object> slaveTableIdDictMap;

    /**
     * slaveColumnId 字典关联数据。
     */
    @Schema(description = "slaveColumnId的字典关联数据")
    private Map<String, Object> slaveColumnIdDictMap;

    /**
     * relationType 常量字典关联数据。
     */
    @Schema(description = "常量字典关联数据")
    private Map<String, Object> relationTypeDictMap;
}
