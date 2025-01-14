package io.wangk.peekaboo.common.mobile.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * 移动端入口和角色关联的实体对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
@TableName(value = "zz_mobile_entry_role")
public class MobileEntryRole {

    /**
     * 移动端入口Id。
     */
    @TableField(value = "entry_id")
    private Long entryId;

    /**
     * 角色Id。
     */
    @TableField(value = "role_id")
    private Long roleId;
}
