package io.wangk.peekaboo.common.online.model;

import com.baomidou.mybatisplus.annotation.*;
import io.wangk.peekaboo.common.core.annotation.RelationConstDict;
import io.wangk.peekaboo.common.online.model.constant.RuleType;
import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 * 在线表单数据表字段验证规则实体对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
@TableName(value = "zz_online_rule")
public class OnlineRule {

    /**
     * 主键Id。
     */
    @TableId(value = "rule_id")
    private Long ruleId;

    /**
     * 应用编码。为空时，表示非第三方应用接入。
     */
    @TableField(value = "app_code")
    private String appCode;

    /**
     * 规则名称。
     */
    @TableField(value = "rule_name")
    private String ruleName;

    /**
     * 规则类型。
     */
    @TableField(value = "rule_type")
    private Integer ruleType;

    /**
     * 内置规则标记。
     */
    @TableField(value = "builtin")
    private Boolean builtin;

    /**
     * 自定义规则的正则表达式。
     */
    @TableField(value = "pattern")
    private String pattern;

    /**
     * 创建时间。
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 更新时间。
     */
    @TableField(value = "update_time")
    private Date updateTime;

    /**
     * 创建者。
     */
    @TableField(value = "create_user_id")
    private Long createUserId;

    /**
     * 更新者。
     */
    @TableField(value = "update_user_id")
    private Long updateUserId;

    /**
     * 逻辑删除标记字段(1: 正常 -1: 已删除)。
     */
    @TableLogic
    @TableField(value = "deleted_flag")
    private Integer deletedFlag;

    /**
     * ruleId 的多对多关联表数据对象。
     */
    @TableField(exist = false)
    private OnlineColumnRule onlineColumnRule;

    @RelationConstDict(
            masterIdField = "ruleType",
            constantDictClass = RuleType.class)
    @TableField(exist = false)
    private Map<String, Object> ruleTypeDictMap;
}
