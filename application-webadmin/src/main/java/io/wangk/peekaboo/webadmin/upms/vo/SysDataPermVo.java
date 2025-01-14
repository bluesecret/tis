package io.wangk.peekaboo.webadmin.upms.vo;

import io.wangk.peekaboo.common.core.base.vo.BaseVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

/**
 * 数据权限VO。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Schema(description = "数据权限VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class SysDataPermVo extends BaseVo {

    /**
     * 数据权限Id。
     */
    @Schema(description = "数据权限Id")
    private Long dataPermId;

    /**
     * 显示名称。
     */
    @Schema(description = "显示名称")
    private String dataPermName;

    /**
     * 数据权限规则类型(参考DataPermRuleType常量类)。
     */
    @Schema(description = "数据权限规则类型")
    private Integer ruleType;

    /**
     * 扩展数据。
     */
    @Schema(description = "扩展数据")
    private String extraData;

    /**
     * 部门Id列表(逗号分隔)。
     */
    @Schema(description = "部门Id列表")
    private String deptIdListString;

    /**
     * 数据权限与部门关联对象列表。
     */
    @Schema(description = "数据权限与部门关联对象列表")
    private List<Map<String, Object>> dataPermDeptList;

    /**
     * 数据权限与菜单关联对象列表。
     */
    @Schema(description = "数据权限与菜单关联对象列表")
    private List<Map<String, Object>> dataPermMenuList;

    /**
     * 数据权限与移动端入口关联对象列表。
     */
    @Schema(description = "数据权限与移动端入口关联对象列表")
    private List<Map<String, Object>> dataPermMobileEntryList;
}