package io.wangk.peekaboo.webadmin.upms.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 数据权限与部门关联VO。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Schema(description = "数据权限与部门关联VO")
@Data
public class SysDataPermDeptVo {

    /**
     * 数据权限Id。
     */
    @Schema(description = "数据权限Id")
    private Long dataPermId;

    /**
     * 关联部门Id。
     */
    @Schema(description = "关联部门Id")
    private Long deptId;
}