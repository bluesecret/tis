package io.wangk.peekaboo.webadmin.app.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 患者检测结果VO视图对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Schema(description = "患者检测结果VO视图对象")
@Data
public class TisPatResultVo {

    /**
     * 主键Id。
     */
    @Schema(description = "主键Id")
    private Long id;

    /**
     * 患者ID。
     */
    @Schema(description = "患者ID")
    private Long patId;

    /**
     * 检测项目。
     */
    @Schema(description = "检测项目")
    private String projectName;

    /**
     * 检测结果。
     */
    @Schema(description = "检测结果")
    private String result;

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
}
