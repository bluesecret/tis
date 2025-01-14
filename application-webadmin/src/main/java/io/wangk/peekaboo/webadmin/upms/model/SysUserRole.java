package io.wangk.peekaboo.webadmin.upms.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * 用户角色实体对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
@TableName(value = "sys_user_role")
public class SysUserRole {

    /**
     * 用户Id。
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 角色Id。
     */
    @TableField(value = "role_id")
    private Long roleId;
}
