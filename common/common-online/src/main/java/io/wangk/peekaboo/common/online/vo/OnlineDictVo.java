package io.wangk.peekaboo.common.online.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 * 在线表单关联的字典VO对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Schema(description = "在线表单关联的字典VO对象")
@Data
public class OnlineDictVo {

    /**
     * 主键Id。
     */
    @Schema(description = "主键Id")
    private Long dictId;

    /**
     * 应用编码。为空时，表示非第三方应用接入。
     */
    @Schema(description = "应用编码。为空时，表示非第三方应用接入")
    private String appCode;

    /**
     * 字典名称。
     */
    @Schema(description = "字典名称")
    private String dictName;

    /**
     * 字典类型。
     */
    @Schema(description = "字典类型")
    private Integer dictType;

    /**
     * 数据库链接Id。
     */
    @Schema(description = "数据库链接Id")
    private Long dblinkId;

    /**
     * 字典表名称。
     */
    @Schema(description = "字典表名称")
    private String tableName;

    /**
     * 全局字典编码。
     */
    @Schema(description = "全局字典编码")
    private String dictCode;

    /**
     * 逻辑删除字段。
     */
    @Schema(description = "逻辑删除字段")
    private String deletedColumnName;

    /**
     * 用户过滤滤字段名称。
     */
    @Schema(description = "用户过滤滤字段名称")
    private String userFilterColumnName;

    /**
     * 部门过滤字段名称。
     */
    @Schema(description = "部门过滤字段名称")
    private String deptFilterColumnName;

    /**
     * 租户过滤字段名称。
     */
    @Schema(description = "租户过滤字段名称")
    private String tenantFilterColumnName;

    /**
     * 字典表键字段名称。
     */
    @Schema(description = "字典表键字段名称")
    private String keyColumnName;

    /**
     * 字典表父键字段名称。
     */
    @Schema(description = "字典表父键字段名称")
    private String parentKeyColumnName;

    /**
     * 字典值字段名称。
     */
    @Schema(description = "字典值字段名称")
    private String valueColumnName;

    /**
     * 是否树形标记。
     */
    @Schema(description = "是否树形标记")
    private Boolean treeFlag;

    /**
     * 获取字典数据的url。
     */
    @Schema(description = "获取字典数据的url")
    private String dictListUrl;

    /**
     * 根据主键id批量获取字典数据的url。
     */
    @Schema(description = "根据主键id批量获取字典数据的url")
    private String dictIdsUrl;

    /**
     * 字典的JSON数据。
     */
    @Schema(description = "字典的JSON数据")
    private String dictDataJson;

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
     * dictType 常量字典关联数据。
     */
    @Schema(description = "dictType 常量字典关联数据")
    private Map<String, Object> dictTypeDictMap;

    /**
     * 数据库链接Id字典关联数据。
     */
    @Schema(description = "数据库链接Id字典关联数据")
    private Map<String, Object> dblinkIdDictMap;
}
