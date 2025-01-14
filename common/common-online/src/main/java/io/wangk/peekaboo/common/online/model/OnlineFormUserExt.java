package io.wangk.peekaboo.common.online.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * 在线表单用户扩展实体对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
@TableName(value = "zz_online_form_user_ext")
public class OnlineFormUserExt {

    /**
     * 用户Id。
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 用户Id。
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 在线表单主键Id。
     */
    @TableField(value = "online_form_id")
    private Long onlineFormId;

    /**
     * 扩展信息。
     */
    @TableField(value = "extra_data")
    private String extraData;
}
