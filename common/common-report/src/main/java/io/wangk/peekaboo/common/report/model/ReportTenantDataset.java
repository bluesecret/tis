package io.wangk.peekaboo.common.report.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * 租户报表数据源关联表。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
@TableName(value = "zz_report_tenant_dataset")
public class ReportTenantDataset {

    /**
     * 租户Id。
     */
    @TableField(value = "tenant_id")
    private Long tenantId;

    /**
     * 数据集Id。
     */
    @TableField(value = "dataset_id")
    private Long datasetId;
}
