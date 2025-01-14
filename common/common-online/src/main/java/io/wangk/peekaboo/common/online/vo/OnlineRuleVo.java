package io.wangk.peekaboo.common.online.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 * 在线表单数据表字段验证规则VO对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Schema(description = "在线表单数据表字段验证规则VO对象")
@Data
public class OnlineRuleVo {

    /**
     * 主键Id。
     */
    @Schema(description = "主键Id")
    private Long ruleId;

    /**
     * 应用编码。为空时，表示非第三方应用接入。
     */
    @Schema(description = "应用编码。为空时，表示非第三方应用接入")
    private String appCode;

    /**
     * 规则名称。
     */
    @Schema(description = "规则名称")
    private String ruleName;

    /**
     * 规则类型。
     */
    @Schema(description = "规则类型")
    private Integer ruleType;

    /**
     * 内置规则标记。
     */
    @Schema(description = "内置规则标记")
    private Boolean builtin;

    /**
     * 自定义规则的正则表达式。
     */
    @Schema(description = "自定义规则的正则表达式")
    private String pattern;

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
     * ruleId 的多对多关联表数据对象，数据对应类型为OnlineColumnRuleVo。
     */
    @Schema(description = "ruleId 的多对多关联表数据对象")
    private Map<String, Object> onlineColumnRule;

    /**
     * ruleType 常量字典关联数据。
     */
    @Schema(description = "ruleType 常量字典关联数据")
    private Map<String, Object> ruleTypeDictMap;
}
