package io.wangk.peekaboo.webadmin.app.model;

import com.baomidou.mybatisplus.annotation.*;
import io.wangk.peekaboo.common.core.upload.UploadStoreTypeEnum;
import io.wangk.peekaboo.common.core.annotation.*;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 患者实体对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
@TableName(value = "tis_pat_info")
public class TisPatInfo {

    /**
     * 主键Id。
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 姓名。
     */
    @TableField(value = "pat_name")
    private String patName;

    /**
     * 批次号。
     */
    @TableField(value = "batch_no")
    private String batchNo;

    /**
     * 年龄。
     */
    @TableField(value = "age")
    private String age;

    /**
     * 检测项目。
     */
    @TableField(value = "project_id")
    private String projectId;

    /**
     * 性别。
     */
    @TableField(value = "sex")
    private String sex;

    /**
     * 样本编号。
     */
    @TableField(value = "sample_no")
    private String sampleNo;

    /**
     * 患者编号。
     */
    @TableField(value = "pat_no")
    private String patNo;

    /**
     * 样本类型。
     */
    @TableField(value = "sample_type")
    private String sampleType;

    /**
     * 操作人员。
     */
    @TableField(value = "operator")
    private String operator;

    /**
     * cotful值。
     */
    @TableField(value = "cutoff_val")
    private String cutoffVal;

    /**
     * 范围。
     */
    @TableField(value = "range_val")
    private String rangeVal;

    /**
     * 患者卡条图片路径。
     */
    @UploadFlagColumn(storeType = UploadStoreTypeEnum.MINIO_SYSTEM)
    @TableField(value = "pic_path")
    private String picPath;

    /**
     * 检测时间。
     */
    @TableField(value = "test_time")
    private String testTime;

    /**
     * 检测单位。
     */
    @TableField(value = "test_unit")
    private String testUnit;

    /**
     * 检测状态。
     */
    @TableField(value = "test_stat")
    private String testStat;

    /**
     * txt文件路径。
     */
    @UploadFlagColumn(storeType = UploadStoreTypeEnum.MINIO_SYSTEM)
    @TableField(value = "file_path")
    private String filePath;

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

    /**
     * TisPatResult 的一对多关联表数据对象。
     * 通常在一对多的关联中，我们基于从表数据过滤主表数据，此时需要先对从表数据进行嵌套子查询过滤，并将从表过滤数据列表集成到该字段。
     */
    @RelationOneToMany(
            masterIdField = "id",
            slaveModelClass = TisPatResult.class,
            slaveIdField = "patId")
    @TableField(exist = false)
    private List<TisPatResult> tisPatResultList;
}
