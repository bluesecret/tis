package io.wangk.peekaboo.webadmin.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.wangk.peekaboo.common.core.validator.UpdateGroup;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 设备Dto对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Schema(description = "设备Dto对象")
@Data
public class TisDeviceInfoDto {

    /**
     * 主键Id。
     */
    @Schema(description = "主键Id。", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "数据验证失败，主键Id不能为空！", groups = {UpdateGroup.class})
    private Long id;

    /**
     * 设备序列号。
     * NOTE: 可支持等于操作符的列表数据过滤。
     */
    @Schema(description = "设备序列号。可支持等于操作符的列表数据过滤。")
    private String serNo;

    /**
     * 设备名称。
     */
    @Schema(description = "设备名称。")
    private String deviceName;

    /**
     * 备用字段1。
     */
    @Schema(description = "备用字段1。")
    private String remark1;

    /**
     * 备用字段2。
     */
    @Schema(description = "备用字段2。")
    private String remark2;

    /**
     * 备用字段3。
     */
    @Schema(description = "备用字段3。")
    private String remark3;

    /**
     * 创建用户。
     */
    @Schema(description = "创建用户。")
    private Long createdUserId;
}
