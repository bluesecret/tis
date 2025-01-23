package io.wangk.peekaboo.webadmin.app.vo;

import io.wangk.peekaboo.common.core.base.vo.BaseVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 检查结果VO视图对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Schema(description = "检查结果VO视图对象")
@Data
@EqualsAndHashCode(callSuper = true)
public class TisPatResultVo extends BaseVo {

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
