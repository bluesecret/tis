package io.wangk.peekaboo.webadmin.upms.vo;

import io.wangk.peekaboo.common.core.base.vo.BaseVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 部门管理VO视图对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Schema(description = "部门管理VO视图对象")
@Data
@EqualsAndHashCode(callSuper = true)
public class SysDeptVo extends BaseVo {

    /**
     * 部门Id。
     */
    @Schema(description = "部门Id")
    private Long deptId;

    /**
     * 部门名称。
     */
    @Schema(description = "部门名称")
    private String deptName;

    /**
     * 显示顺序。
     */
    @Schema(description = "显示顺序")
    private Integer showOrder;

    /**
     * 父部门Id。
     */
    @Schema(description = "父部门Id")
    private Long parentId;
}
