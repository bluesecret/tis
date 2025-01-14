package io.wangk.peekaboo.webadmin.upms.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * 部门岗位多对多关联实体对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
@TableName(value = "sys_dept_post")
public class SysDeptPost {

    /**
     * 部门岗位Id。
     */
    @TableId(value = "dept_post_id")
    private Long deptPostId;

    /**
     * 部门Id。
     */
    @TableField(value = "dept_id")
    private Long deptId;

    /**
     * 岗位Id。
     */
    @TableField(value = "post_id")
    private Long postId;

    /**
     * 部门岗位显示名称。
     */
    @TableField(value = "post_show_name")
    private String postShowName;
}
