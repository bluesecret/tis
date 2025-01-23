package io.wangk.peekaboo.webadmin.app.dto;

import io.wangk.peekaboo.common.core.validator.UpdateGroup;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.*;

/**
 * 患者Dto对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Schema(description = "患者Dto对象")
@Data
public class TisPatInfoDto {

    /**
     * 主键Id。
     */
    @Schema(description = "主键Id。", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "数据验证失败，主键Id不能为空！", groups = {UpdateGroup.class})
    private Long id;

    /**
     * 姓名。
     * NOTE: 可支持等于操作符的列表数据过滤。
     */
    @Schema(description = "姓名。可支持等于操作符的列表数据过滤。")
    private String patName;

    /**
     * 批次号。
     */
    @Schema(description = "批次号。")
    private String batchNo;

    /**
     * 年龄。
     */
    @Schema(description = "年龄。")
    private String age;

    /**
     * 检测项目。
     * NOTE: 可支持等于操作符的列表数据过滤。
     */
    @Schema(description = "检测项目。可支持等于操作符的列表数据过滤。")
    private String projectId;

    /**
     * 性别。
     */
    @Schema(description = "性别。")
    private String sex;

    /**
     * 样本编号。
     * NOTE: 可支持等于操作符的列表数据过滤。
     */
    @Schema(description = "样本编号。可支持等于操作符的列表数据过滤。")
    private String sampleNo;

    /**
     * 患者编号。
     * NOTE: 可支持等于操作符的列表数据过滤。
     */
    @Schema(description = "患者编号。可支持等于操作符的列表数据过滤。")
    private String patNo;

    /**
     * 样本类型。
     */
    @Schema(description = "样本类型。")
    private String sampleType;

    /**
     * 操作人员。
     * NOTE: 可支持等于操作符的列表数据过滤。
     */
    @Schema(description = "操作人员。可支持等于操作符的列表数据过滤。")
    private String operator;

    /**
     * cotful值。
     */
    @Schema(description = "cotful值。")
    private String cutoffVal;

    /**
     * 范围。
     */
    @Schema(description = "范围。")
    private String rangeVal;

    /**
     * 患者卡条图片路径。
     */
    @Schema(description = "患者卡条图片路径。")
    private String picPath;

    /**
     * 检测时间。
     */
    @Schema(description = "检测时间。")
    private String testTime;

    /**
     * 检测单位。
     */
    @Schema(description = "检测单位。")
    private String testUnit;

    /**
     * 检测状态。
     * NOTE: 可支持等于操作符的列表数据过滤。
     */
    @Schema(description = "检测状态。可支持等于操作符的列表数据过滤。")
    private String testStat;

    /**
     * txt文件路径。
     */
    @Schema(description = "txt文件路径。")
    private String filePath;

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
