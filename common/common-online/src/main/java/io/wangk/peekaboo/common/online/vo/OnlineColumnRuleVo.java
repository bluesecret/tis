package io.wangk.peekaboo.common.online.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 在线表单数据表字段规则和字段多对多关联VO对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Schema(description = "在线表单数据表字段规则和字段多对多关联VO对象")
@Data
public class OnlineColumnRuleVo {

    /**
     * 字段Id。
     */
    @Schema(description = "字段Id")
    private Long columnId;

    /**
     * 规则Id。
     */
    @Schema(description = "规则Id")
    private Long ruleId;

    /**
     * 规则属性数据。
     */
    @Schema(description = "规则属性数据")
    private String propDataJson;
}
