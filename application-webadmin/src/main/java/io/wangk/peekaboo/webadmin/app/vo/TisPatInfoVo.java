package io.wangk.peekaboo.webadmin.app.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.Map;
import java.util.List;

/**
 * 患者VO视图对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Schema(description = "患者VO视图对象")
@Data
public class TisPatInfoVo {

    /**
     * 主键Id。
     */
    @Schema(description = "主键Id")
    private Long id;

    /**
     * 姓名。
     */
    @Schema(description = "姓名")
    private String patName;

    /**
     * 批次号。
     */
    @Schema(description = "批次号")
    private String batchNo;

    /**
     * 年龄。
     */
    @Schema(description = "年龄")
    private String age;

    /**
     * 检测项目。
     */
    @Schema(description = "检测项目")
    private String projectId;

    /**
     * 性别。
     */
    @Schema(description = "性别")
    private String sex;

    /**
     * 样本编号。
     */
    @Schema(description = "样本编号")
    private String sampleNo;

    /**
     * 患者编号。
     */
    @Schema(description = "患者编号")
    private String patNo;

    /**
     * 样本类型。
     */
    @Schema(description = "样本类型")
    private String sampleType;

    /**
     * 操作人员。
     */
    @Schema(description = "操作人员")
    private String operator;

    /**
     * cotful值。
     */
    @Schema(description = "cotful值")
    private String cutoffVal;

    /**
     * 范围。
     */
    @Schema(description = "范围")
    private String rangeVal;

    /**
     * 患者卡条图片路径。
     */
    @Schema(description = "患者卡条图片路径")
    private String picPath;

    /**
     * 检测时间。
     */
    @Schema(description = "检测时间")
    private String testTime;

    /**
     * 检测单位。
     */
    @Schema(description = "检测单位")
    private String testUnit;

    /**
     * 检测状态。
     */
    @Schema(description = "检测状态")
    private String testStat;

    /**
     * txt文件路径。
     */
    @Schema(description = "txt文件路径")
    private String filePath;

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

    /**
     * TisPatResult 的一对多关联表数据对象。数据对应类型为TisPatResult。
     */
    @Schema(description = "TisPatResult 的一对多关联表数据对象。数据对应类型为TisPatResult")
    private List<Map<String, Object>> tisPatResultList;
}
