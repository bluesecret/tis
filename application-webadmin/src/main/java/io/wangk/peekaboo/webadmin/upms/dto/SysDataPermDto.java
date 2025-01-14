package io.wangk.peekaboo.webadmin.upms.dto;

import io.wangk.peekaboo.common.core.validator.UpdateGroup;
import io.wangk.peekaboo.common.core.validator.ConstDictRef;
import io.wangk.peekaboo.common.core.constant.DataPermRuleType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.*;

/**
 * 数据权限Dto。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Schema(description = "数据权限Dto")
@Data
public class SysDataPermDto {

    /**
     * 数据权限Id。
     */
    @Schema(description = "数据权限Id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "数据权限Id不能为空！", groups = {UpdateGroup.class})
    private Long dataPermId;

    /**
     * 显示名称。
     */
    @Schema(description = "显示名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "数据权限名称不能为空！")
    private String dataPermName;

    /**
     * 数据权限规则类型(参考DataPermRuleType常量类)。
     */
    @Schema(description = "数据权限规则类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "数据权限规则类型不能为空！")
    @ConstDictRef(constDictClass = DataPermRuleType.class)
    private Integer ruleType;

    /**
     * 扩展数据。
     */
    @Schema(description = "扩展数据")
    private String extraData;

    /**
     * 部门Id列表(逗号分隔)。
     */
    @Schema(hidden = true)
    private String deptIdListString;

    /**
     * 搜索字符串。
     */
    @Schema(description = "LIKE 模糊搜索字符串")
    private String searchString;
}