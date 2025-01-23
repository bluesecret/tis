package io.wangk.peekaboo.webadmin.app.model;

import com.baomidou.mybatisplus.annotation.*;
import io.wangk.peekaboo.common.core.base.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 检查结果实体对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "tis_pat_result")
public class TisPatResult extends BaseModel {

    /**
     * 主键Id。
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 患者ID。
     */
    @TableField(value = "pat_id")
    private Long patId;

    /**
     * 检测项目。
     */
    @TableField(value = "project_name")
    private String projectName;

    /**
     * 检测结果。
     */
    @TableField(value = "result")
    private String result;

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
}
