package io.wangk.peekaboo.webadmin.upms.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 部门关联实体对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "sys_dept_relation")
public class SysDeptRelation {

    /**
     * 上级部门Id。
     */
    @TableField(value = "parent_dept_id")
    private Long parentDeptId;

    /**
     * 部门Id。
     */
    @TableField(value = "dept_id")
    private Long deptId;
}
