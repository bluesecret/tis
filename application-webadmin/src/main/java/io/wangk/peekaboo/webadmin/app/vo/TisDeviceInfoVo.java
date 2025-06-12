package io.wangk.peekaboo.webadmin.app.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 设备VO视图对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Schema(description = "设备VO视图对象")
@Data
public class TisDeviceInfoVo {

    /**
     * 主键Id。
     */
    @Schema(description = "主键Id")
    private Long id;

    /**
     * 设备编码。
     */
    @Schema(description = "设备编码")
    private String serNo;

    /**
     * 设备名称。
     */
    @Schema(description = "设备名称")
    private String deviceName;

    /**
     * 备用字段1。
     */
    @Schema(description = "备用字段1")
    private String remark1;

    /**
     * 备用字段2。
     */
    @Schema(description = "备用字段2")
    private String remark2;

    /**
     * 备用字段3。
     */
    @Schema(description = "备用字段3")
    private String remark3;

    /**
     * 创建时间。
     */
    @Schema(description = "创建时间")
    private Date createTime;

    /**
     * 创建用户。
     */
    @Schema(description = "创建用户")
    private Long createdUserId;

    /**
     * 修改时间。
     */
    @Schema(description = "修改时间")
    private Date updateTime;

    /**
     * 修改用户。
     */
    @Schema(description = "修改用户")
    private Long updateUserId;
}
