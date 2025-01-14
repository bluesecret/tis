package io.wangk.peekaboo.webadmin.upms.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 部门岗位VO对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Schema(description = "部门岗位VO")
@Data
public class SysDeptPostVo {

    /**
     * 部门岗位Id。
     */
    @Schema(description = "部门岗位Id")
    private Long deptPostId;

    /**
     * 部门Id。
     */
    @Schema(description = "部门Id")
    private Long deptId;

    /**
     * 岗位Id。
     */
    @Schema(description = "岗位Id")
    private Long postId;

    /**
     * 部门岗位显示名称。
     */
    @Schema(description = "部门岗位显示名称")
    private String postShowName;
}
