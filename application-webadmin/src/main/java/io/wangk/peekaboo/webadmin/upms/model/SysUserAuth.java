package io.wangk.peekaboo.webadmin.upms.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * 用户第三方授权数据实体对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
@TableName(value = "sys_user_auth")
public class SysUserAuth {

    /**
     * 主键Id。
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 用户Id。
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 授权来源。
     */
    @TableField(value = "source")
    private String source;

    /**
     * 授权方的OpenId。
     */
    @TableField(value = "open_id")
    private String openId;

    /**
     * 授权方的UnionId。
     */
    @TableField(value = "union_id")
    private String unionId;

    /**
     * 授权方的userId。
     */
    @TableField(value = "auth_user_id")
    private String authUserId;

    /**
     * 扩展数据。
     */
    @TableField(value = "extra_data")
    private String extraData;

    /**
     * 更新时间。
     */
    @TableField(value = "update_time")
    private Date updateTime;

    /**
     * 创建时间。
     */
    @TableField(value = "create_time")
    private Date createTime;
}
