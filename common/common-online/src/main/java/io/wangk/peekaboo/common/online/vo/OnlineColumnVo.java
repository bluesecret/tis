package io.wangk.peekaboo.common.online.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 * 在线表单数据表字段规则和字段多对多关联VO对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Schema(description = "在线表单数据表字段规则和字段多对多关联VO对象")
@Data
public class OnlineColumnVo {

    /**
     * 主键Id。
     */
    @Schema(description = "主键Id")
    private Long columnId;

    /**
     * 字段名。
     */
    @Schema(description = "字段名")
    private String columnName;

    /**
     * 数据表Id。
     */
    @Schema(description = "数据表Id")
    private Long tableId;

    /**
     * 数据表中的字段类型。
     */
    @Schema(description = "数据表中的字段类型")
    private String columnType;

    /**
     * 数据表中的完整字段类型(包括了精度和刻度)。
     */
    @Schema(description = "数据表中的完整字段类型")
    private String fullColumnType;

    /**
     * 是否为主键。
     */
    @Schema(description = "是否为主键")
    private Boolean primaryKey;

    /**
     * 是否是自增主键(0: 不是 1: 是)。
     */
    @Schema(description = "是否是自增主键")
    private Boolean autoIncrement;

    /**
     * 是否可以为空 (0: 不可以为空 1: 可以为空)。
     */
    @Schema(description = "是否可以为空")
    private Boolean nullable;

    /**
     * 缺省值。
     */
    @Schema(description = "缺省值")
    private String columnDefault;

    /**
     * 字段在数据表中的显示位置。
     */
    @Schema(description = "字段在数据表中的显示位置")
    private Integer columnShowOrder;

    /**
     * 数据表中的字段注释。
     */
    @Schema(description = "数据表中的字段注释")
    private String columnComment;

    /**
     * 对象映射字段名称。
     */
    @Schema(description = "对象映射字段名称")
    private String objectFieldName;

    /**
     * 对象映射字段类型。
     */
    @Schema(description = "对象映射字段类型")
    private String objectFieldType;

    /**
     * 数值型字段的精度(目前仅Oracle使用)。
     */
    @Schema(description = "数值型字段的精度")
    private Integer numericPrecision;

    /**
     * 数值型字段的刻度(小数点后位数，目前仅Oracle使用)。
     */
    @Schema(description = "数值型字段的刻度")
    private Integer numericScale;

    /**
     * 过滤类型。
     */
    @Schema(description = "过滤类型")
    private Integer filterType;

    /**
     * 是否是主键的父Id。
     */
    @Schema(description = "是否是主键的父Id")
    private Boolean parentKey;

    /**
     * 是否部门过滤字段。
     */
    @Schema(description = "是否部门过滤字段")
    private Boolean deptFilter;

    /**
     * 是否用户过滤字段。
     */
    @Schema(description = "是否用户过滤字段")
    private Boolean userFilter;

    /**
     * 字段类别。
     */
    @Schema(description = "字段类别")
    private Integer fieldKind;

    /**
     * 包含的文件文件数量，0表示无限制。
     */
    @Schema(description = "包含的文件文件数量，0表示无限制")
    private Integer maxFileCount;

    /**
     * 上传文件系统类型。
     */
    @Schema(description = "上传文件系统类型")
    private Integer uploadFileSystemType;

    /**
     * 编码规则的JSON格式数据。
     */
    @Schema(description = "编码规则的JSON格式数据")
    private String encodedRule;

    /**
     * 脱敏字段类型，具体值可参考MaskFieldTypeEnum枚举。
     */
    @Schema(description = "脱敏字段类型")
    private String maskFieldType;

    /**
     * 字典Id。
     */
    @Schema(description = "字典Id")
    private Long dictId;

    /**
     * 文件类型，多个之间逗号分隔。
     */
    @Schema(description = "文件类型")
    private String fileType;

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
     * fieldKind 常量字典关联数据。
     */
    @Schema(description = "常量字典关联数据")
    private Map<String, Object> fieldKindDictMap;

    /**
     * dictId 的一对一关联。
     */
    @Schema(description = "dictId 的一对一关联")
    private Map<String, Object> dictInfo;
}
