package io.wangk.peekaboo.webadmin.app.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 用户角色实体对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
@TableName(value = "tis_user_device")
public class TisUserDevice {

    /**
     * 用户Id。
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 设备Id。
     */
    @TableField(value = "device_no")
    private String deviceNo;
}
