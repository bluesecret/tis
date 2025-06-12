package io.wangk.peekaboo.webadmin.app.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.wangk.peekaboo.common.core.annotation.RelationOneToMany;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 设备实体对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
@TableName(value = "tis_device_info")
public class TisDeviceInfo {

    /**
     * 主键Id。
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 设备序列号。
     */
    @TableField(value = "ser_no")
    private String serNo;

    /**
     * 设备名称。
     */
    @TableField(value = "device_name")
    private String deviceName;


    /**
     * 备用字段1。
     */
    @TableField(value = "remark1")
    private String remark1;

    /**
     * 备用字段2。
     */
    @TableField(value = "remark2")
    private String remark2;

    /**
     * 备用字段3。
     */
    @TableField(value = "remark3")
    private String remark3;

    /**
     * 创建时间。
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 创建用户。
     */
    @TableField(value = "created_user_id")
    private Long createdUserId;

    /**
     * 修改时间。
     */
    @TableField(value = "update_time")
    private Date updateTime;

    /**
     * 修改用户。
     */
    @TableField(value = "update_user_id")
    private Long updateUserId;

}
